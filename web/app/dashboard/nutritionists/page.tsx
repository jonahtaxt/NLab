'use client';

import { useState } from 'react';
import { PaginatedResponse, Nutritionist } from '@/app/lib/definitions';
import dynamic from 'next/dynamic';
import { useTableData } from '@/app/hooks/useTableData';
import { fetchPaginatedNutritionists } from '@/app/lib/data.nutritionist';

// Use dynamic import for the NutritionistTable component
const NutritionistTable = dynamic(() => import('@/app/ui/nutritionists/nutritionist-table'), {
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

    const {
        data: nutritionistsData,
        isLoading,
        error,
        refresh
    } = useTableData<PaginatedResponse<Nutritionist>>({
        fetchFunction: async () => {
            return await fetchPaginatedNutritionists(
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
        <main>
            <NutritionistTable
                nutritionists={nutritionistsData?.content || []}
                onSearch={handleSearch}
                onSort={handleSort}
                sortBy={sortBy}
                sortDirection={sortDirection}
                isLoading={isLoading}
                onRefresh={handleRefresh}
                error={error}
            />

            {nutritionistsData.totalElements > 0 && (
                <Pagination
                    currentPage={nutritionistsData.pageNumber}
                    pageSize={nutritionistsData.pageSize}
                    totalPages={nutritionistsData.totalPages}
                    totalElements={nutritionistsData.totalElements}
                    onPageChange={handlePageChange}
                    onPageSizeChange={handlePageSizeChange}
                    isFirstPage={nutritionistsData.first}
                    isLastPage={nutritionistsData.last}
                    pageSizeOptions={[5, 10, 20, 50, 100]}
                />
            )}
        </main>
    );
}