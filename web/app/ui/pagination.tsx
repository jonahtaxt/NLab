'use client';

import React from 'react';
import { Button } from '@/components/ui/button';
import { ChevronLeft, ChevronRight } from 'lucide-react';

interface PaginationProps {
    currentPage: number;
    totalPages: number;
    onPageChange: (page: number) => void;
    isFirstPage: boolean;
    isLastPage: boolean;
}

export function Pagination({
    currentPage,
    totalPages,
    onPageChange,
    isFirstPage,
    isLastPage
}: PaginationProps) {
    // Generate page numbers to display
    const getPageNumbers = () => {
        const pageNumbers = [];
        
        // Always show current page
        // Show 2 pages before and after current if available
        const startPage = Math.max(0, currentPage - 2);
        const endPage = Math.min(totalPages - 1, currentPage + 2);
        
        for (let i = startPage; i <= endPage; i++) {
            pageNumbers.push(i);
        }
        
        return pageNumbers;
    };

    return (
        <div className="flex items-center justify-center space-x-2 py-8">
            <Button
                variant="outline"
                size="sm"
                onClick={() => onPageChange(currentPage - 1)}
                disabled={isFirstPage}
            >
                <ChevronLeft className="h-4 w-4" />
            </Button>
            
            {getPageNumbers().map((pageNumber) => (
                <Button
                    key={pageNumber}
                    variant={pageNumber === currentPage ? "default" : "outline"}
                    size="sm"
                    onClick={() => onPageChange(pageNumber)}
                >
                    {pageNumber + 1}
                </Button>
            ))}
            
            <Button
                variant="outline"
                size="sm"
                onClick={() => onPageChange(currentPage + 1)}
                disabled={isLastPage}
            >
                <ChevronRight className="h-4 w-4" />
            </Button>
        </div>
    );
}