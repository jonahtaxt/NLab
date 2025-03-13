'use client';

import { useState, useEffect } from 'react';
import { fetchPaginatedPatients, fetchPatientById } from '@/app/lib/data.patient';
import { Patient, PaginatedResponse } from '@/app/lib/definitions';
import dynamic from 'next/dynamic';
import { useTableData } from '@/app/hooks/useTableData';
import PatientDetailView from '@/app/ui/patients/patient-detail-view';

// Use dynamic import for the PatientTable component
const PatientTable = dynamic(() => import('@/app/ui/patients/patients-table'), {
    loading: () => null
});

// Use dynamic import for the Pagination component
const Pagination = dynamic(() => import('@/app/ui/pagination').then(mod => ({
    default: mod.Pagination
})), {
    ssr: true
});

export default function Page() {
    // Pagination and sorting state
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState('lastName');
    const [sortDirection, setSortDirection] = useState('ASC');
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    
    // Patient detail view state
    const [selectedPatientId, setSelectedPatientId] = useState<number | null>(null);
    const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
    const [loadingPatient, setLoadingPatient] = useState(false);
    const [isViewingDetails, setIsViewingDetails] = useState(false);

    const {
        data: patientsData,
        isLoading,
        error,
        refresh
    } = useTableData<PaginatedResponse<Patient>>({
        fetchFunction: async () => {
            return await fetchPaginatedPatients(
                currentPage,
                pageSize,
                sortBy,
                sortDirection,
                searchTerm
            );
        },
        initialData: {
            content: [],
            pageNumber: 0,
            pageSize: 10,
            totalElements: 0,
            totalPages: 0,
            first: true,
            last: true
        },
        dependencies: [currentPage, pageSize, sortBy, sortDirection, searchTerm, refreshTrigger]
    });

    // Fetch patient details when selectedPatientId changes
    useEffect(() => {
        const getPatientDetails = async () => {
            if (selectedPatientId) {
                setLoadingPatient(true);
                try {
                    const patient = await fetchPatientById(selectedPatientId);
                    setSelectedPatient(patient);
                    // Add a small delay before showing the detail view to allow for a smooth transition
                    setTimeout(() => {
                        setIsViewingDetails(true);
                        setLoadingPatient(false);
                    }, 50);
                } catch (error) {
                    console.error('Error fetching patient details:', error);
                    setLoadingPatient(false);
                }
            }
        };

        getPatientDetails();
    }, [selectedPatientId]);

    const handlePatientRowClick = (patient: Patient) => {
        setSelectedPatientId(patient.id);
    };

    const handleBackToList = () => {
        setIsViewingDetails(false);
        // Add a delay before clearing the selected patient to allow for smooth transition
        setTimeout(() => {
            setSelectedPatientId(null);
            setSelectedPatient(null);
        }, 300); // Match this with your CSS transition duration
    };

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
    };

    const handlePageSizeChange = (size: number) => {
        setPageSize(size);
        setCurrentPage(0); // Reset to first page when changing page size
    };

    const handleSearch = (term: string) => {
        setSearchTerm(term);
        setCurrentPage(0); // Reset to first page on new search
    };

    const handleSort = (column: string) => {
        if (sortBy === column) {
            // Toggle sort direction if clicking the same column
            setSortDirection(sortDirection === 'ASC' ? 'DESC' : 'ASC');
        } else {
            setSortBy(column);
            setSortDirection('ASC');
        }
        setCurrentPage(0); // Reset to first page on sort change
    };

    const handleRefresh = () => {
        setRefreshTrigger(prev => prev + 1);
    };

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }

    return (
        <main className="relative overflow-hidden">
            <div 
                className={`transition-transform duration-300 ease-in-out flex w-full`}
                style={{ 
                    transform: isViewingDetails ? 'translateX(-50%)' : 'translateX(0)',
                    width: '200%', // Double the width to accommodate both views
                    height: '100%'
                }}
            >
                {/* Left panel - Patient Table */}
                <div className="w-1/2 pr-4">
                    <PatientTable
                        patients={patientsData?.content || []}
                        onSearch={handleSearch}
                        onSort={handleSort}
                        sortBy={sortBy}
                        sortDirection={sortDirection}
                        isLoading={isLoading}
                        onRefresh={handleRefresh}
                        error={error}
                        onRowClick={handlePatientRowClick}
                    />

                    {patientsData.totalElements > 0 && (
                        <Pagination
                            currentPage={patientsData.pageNumber}
                            pageSize={patientsData.pageSize}
                            totalPages={patientsData.totalPages}
                            totalElements={patientsData.totalElements}
                            onPageChange={handlePageChange}
                            onPageSizeChange={handlePageSizeChange}
                            isFirstPage={patientsData.first}
                            isLastPage={patientsData.last}
                            pageSizeOptions={[5, 10, 20, 50, 100]}
                        />
                    )}
                </div>
                
                {/* Right panel - Patient Detail */}
                <div className="w-1/2 pl-4">
                    {loadingPatient ? (
                        <div className="flex items-center justify-center h-full">
                            <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-nlab-coral"></div>
                        </div>
                    ) : (
                        selectedPatient && (
                            <PatientDetailView 
                                patient={selectedPatient} 
                                onBack={handleBackToList} 
                            />
                        )
                    )}
                </div>
            </div>
        </main>
    );
}