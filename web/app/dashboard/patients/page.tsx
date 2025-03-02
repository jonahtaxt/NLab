'use client';

import { useState } from 'react';
import { fetchPaginatedPatients } from '@/app/lib/data.patient';
import { Patient, PaginatedResponse } from '@/app/lib/definitions';
import dynamic from 'next/dynamic';
import { Loader2 } from 'lucide-react';
import { useTableData } from '@/app/hooks/useTableData';

// Use dynamic import for the PatientTable component
const PatientTable = dynamic(() => import('@/app/ui/patients/patients-table'), {
    loading: () => (
        <div className="flex justify-center items-center h-64">
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            Cargando...
        </div>
    )
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

    // Use the useTableData hook to fetch and manage patient data
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
        onError: (err) => {
            console.error('Failed to load patients:', err);
        },
        dependencies: [currentPage, pageSize, sortBy, sortDirection, searchTerm, refreshTrigger]
    });

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

    if (isLoading && !patientsData.content.length) {
        return (
            <div className="flex justify-center items-center h-64">
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Cargando...
            </div>
        );
    }

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }

    return (
        <main>
            <PatientTable
                patients={patientsData?.content || []}
                onSearch={handleSearch}
                onSort={handleSort}
                sortBy={sortBy}
                sortDirection={sortDirection}
                isLoading={isLoading}
                onRefresh={handleRefresh}
                error={error}
            />

            {patientsData && (
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
        </main>
    );
}