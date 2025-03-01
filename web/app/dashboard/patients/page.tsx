'use client';

import { useState, useEffect } from 'react';
import { fetchPaginatedPatients } from '@/app/lib/data.patient';
import PatientTable from '@/app/ui/patients/patients-table';
import { PaginatedResponse, Patient } from '@/app/lib/definitions';
import { Pagination } from '@/app/ui/pagination';

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

    useEffect(() => {
        async function loadPatients() {
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
                setLoading(false);
            } catch (err) {
                console.error('Failed to load patients:', err);
                setError('Failed to load patients. Please try again later.');
                setLoading(false);
            }
        }

        loadPatients();
    }, []);

    if (loading) {
        return <div className="flex justify-center items-center h-64">Loading...</div>;
    }

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }

    const handlePageChange = (page: number) => {
        setCurrentPage(page);
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
            />
            
            {patientsData && (
                <Pagination
                    currentPage={patientsData.pageNumber}
                    totalPages={patientsData.totalPages}
                    onPageChange={handlePageChange}
                    isFirstPage={patientsData.first}
                    isLastPage={patientsData.last}
                />
            )}
        </main>
    );
}