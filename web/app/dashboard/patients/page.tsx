'use client';

import { useState, useEffect, useCallback } from 'react';
import { fetchPaginatedPatients } from '@/app/lib/data.patient';
import { Patient, PaginatedResponse } from '@/app/lib/definitions';
import dynamic from 'next/dynamic';
import { Loader2 } from 'lucide-react';

const PatientTable = dynamic(() => import('@/app/ui/patients/patients-table'), {
    loading: () =>
        <div className="flex justify-center items-center h-64">
            <Loader2 className="mr-2 h-4 w-4 animate-spin" />
            Cargando...
        </div>
});

const Pagination = dynamic(() => import('@/app/ui/pagination').then(mod => ({
    default: mod.Pagination
})), {
    ssr: true
});

export default function Page() {
    const [patientsData, setPatientsData] = useState<PaginatedResponse<Patient> | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // Pagination state
    const [currentPage, setCurrentPage] = useState(0);
    const [pageSize, setPageSize] = useState(10);
    const [searchTerm, setSearchTerm] = useState('');
    const [sortBy, setSortBy] = useState('lastName');
    const [sortDirection, setSortDirection] = useState('ASC');

    // Define loadPatients outside of useEffect so it can be reused
    const loadPatients = useCallback(async () => {
        try {
            setLoading(true);
            const data = await fetchPaginatedPatients(
                currentPage,
                pageSize,
                sortBy,
                sortDirection,
                searchTerm
            );
            setPatientsData(data);
        } catch (err) {
            console.error('Failed to load patients:', err);
            setError('Failed to load patients. Please try again later.');
        } finally {
            setLoading(false);
        }
    }, [currentPage, pageSize, sortBy, sortDirection, searchTerm]);

    // Use the loadPatients function in useEffect
    useEffect(() => {
        loadPatients();
    }, [loadPatients]);

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

    // Now we can use loadPatients for refreshData
    const refreshData = () => {
        loadPatients();
    };

    if (loading && !patientsData) {
        return <div className="flex justify-center items-center h-64">Loading...</div>;
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
                loading={loading}
                onRefresh={refreshData}
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