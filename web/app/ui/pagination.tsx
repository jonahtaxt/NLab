// In web/components/ui/pagination.tsx
'use client';

import React from 'react';
import { Button } from '@/components/ui/button';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select';
import { ChevronLeft, ChevronRight } from 'lucide-react';
import { Card, CardContent } from '@/components/ui/card';

interface PaginationProps {
    currentPage: number;
    pageSize: number;
    totalPages: number;
    totalElements: number;
    onPageChange: (page: number) => void;
    onPageSizeChange: (size: number) => void;
    isFirstPage: boolean;
    isLastPage: boolean;
    pageSizeOptions?: number[];
}

export function Pagination({
    currentPage,
    pageSize,
    totalPages,
    totalElements,
    onPageChange,
    onPageSizeChange,
    isFirstPage,
    isLastPage,
    pageSizeOptions = [5, 10, 20, 50, 100]
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

    // Calculate display range information
    const startItem = totalElements === 0 ? 0 : currentPage * pageSize + 1;
    const endItem = Math.min((currentPage + 1) * pageSize, totalElements);

    return (
        <Card className="mt-4">
            <CardContent className="p-4">
                <div className="flex flex-col md:flex-row items-center justify-between space-y-4 md:space-y-0">
                    <div className="text-sm text-muted-foreground">
                        Mostrando {startItem}-{endItem} de {totalElements} resultados
                    </div>
                    
                    <div className="flex items-center space-x-6">
                        <div className="flex items-center space-x-2">
                            <span className="text-sm">Filas por página:</span>
                            <Select
                                value={pageSize.toString()}
                                onValueChange={(value) => onPageSizeChange(Number(value))}
                            >
                                <SelectTrigger className="h-8 w-[70px]">
                                    <SelectValue placeholder={pageSize.toString()} />
                                </SelectTrigger>
                                <SelectContent>
                                    {pageSizeOptions.map((size) => (
                                        <SelectItem key={size} value={size.toString()}>
                                            {size}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                        </div>
                        
                        <div className="flex items-center space-x-2">
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
                    </div>
                </div>
            </CardContent>
        </Card>
    );
}