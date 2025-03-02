import { useState, useEffect } from 'react';

interface UseTableDataOptions<T> {
    fetchFunction: () => Promise<T[]>;
    initialData?: T[];
    onError?: (error: Error) => void;
    dependencies?: any[];
}

export function useTableData<T>({
    fetchFunction,
    initialData,
    onError,
    dependencies = []
}: UseTableDataOptions<T>) {
    const [data, setData] = useState<T[]>(initialData || []);
    const [isLoading, setIsLoading] = useState(!initialData);
    const [error, setError] = useState<string | null>(null);

    const loadData = async () => {
        if (initialData && !isLoading) return; // Don't reload if we have initial data

        setIsLoading(true);
        try {
            setError(null);
            setIsLoading(true);
            await new Promise((resolve) => setTimeout(resolve, 7500));
            const fetchedData = await fetchFunction();
            setData(fetchedData);
            setError(null);
        } catch (err) {
            console.error('Failed to fetch data:', err);
            setError('Error al cargar los datos. Por favor, inténtalo de nuevo más tarde.');
            if (onError && err instanceof Error) {
                onError(err);
            }
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadData();
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, dependencies);

    const refresh = () => {
        setIsLoading(true);
        loadData();
    };

    return {
        data,
        isLoading,
        error,
        refresh,
        setData // Useful for updating the data after mutations
    };
}