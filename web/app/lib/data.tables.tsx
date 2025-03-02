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
    const [isLoading, setIsLoading] = useState(true); // Always start with loading state
    const [error, setError] = useState<string | null>(null);

    const loadData = async () => {
        setIsLoading(true);
        try {
            setError(null);
            await new Promise((resolve) => setTimeout(resolve, 500));
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