import { useState, useEffect, useRef } from 'react';

interface UseTableDataOptions<T> {
    fetchFunction: () => Promise<T>;
    initialData: T;
    onError?: (error: Error) => void;
    dependencies?: any[];
    loadingDelay?: number; // Optional delay for loading state to reduce UI flicker
}

export function useTableData<T>({
    fetchFunction,
    initialData,
    onError,
    dependencies = []
}: UseTableDataOptions<T>) {
    const [data, setData] = useState<T>(initialData);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    
    const loadData = async () => {
        try {
            setIsLoading(true);
            setError(null);
            await new Promise((resolve) => setTimeout(resolve, 300));
            const fetchedData = await fetchFunction();
            setData(fetchedData);
        } catch (err) {
            console.error('Failed to fetch data:', err);
            let errorMessage = 'Error al cargar los datos. Por favor, inténtelo de nuevo más tarde.';
            if (err instanceof Error) {
                errorMessage = err.message || errorMessage;
            }
            setError(errorMessage);
            if (onError && err instanceof Error) {
                onError(err);
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadData();
    }, dependencies);

    const refresh = () => {
        loadData();
    };

    return {
        data,
        isLoading,
        error,
        refresh,
        setData
    };
}

/**
 * A specialized version of useTableData for handling paginated responses
 */
export function usePaginatedTableData<T, R>({
    fetchFunction,
    initialData,
    onError,
    dependencies = [],
    loadingDelay = 300
}: UseTableDataOptions<T>) {
    const result = useTableData<T>({
        fetchFunction,
        initialData,
        onError,
        dependencies,
        loadingDelay
    });

    return {
        ...result,
        // Add any pagination-specific functionality here if needed
    };
}