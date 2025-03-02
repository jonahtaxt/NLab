// components/ui/card-table.tsx
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Loader2 } from "lucide-react";

interface CardTableProps {
    cardTitle: string;
    headers: string[];
    loadRows: () => React.ReactNode;
    isLoading?: boolean;
    emptyState?: React.ReactNode;
    error?: string | null;
    onRetry?: () => void;
}

const CardTable = ({
    cardTitle,
    headers,
    loadRows,
    isLoading = false,
    emptyState,
    error = null,
    onRetry
}: CardTableProps) => {
    // Skeleton loader for table rows
    const renderSkeletonRows = () => {
        return Array(5).fill(0).map((_, index) => (
            <tr key={`skeleton-${index}`} className="border-b animate-pulse">
                {headers.map((_, colIndex) => (
                    <td key={`skeleton-col-${colIndex}`} className="px-4 py-3">
                        <div className="h-4 bg-gray-200 rounded-md w-3/4"></div>
                    </td>
                ))}
            </tr>
        ));
    };

    // Empty state
    const renderEmptyState = () => {
        if (emptyState) return emptyState;

        return (
            <tr>
                <td colSpan={headers.length} className="px-4 py-8 text-center text-gray-500">
                    No hay datos disponibles
                </td>
            </tr>
        );
    };

    // Error state
    const renderErrorState = () => (
        <tr>
            <td colSpan={headers.length} className="px-4 py-8 text-center">
                <div className="text-red-500 mb-2">{error}</div>
                {onRetry && (
                    <button
                        onClick={onRetry}
                        className="px-4 py-2 bg-nlab-coral text-white rounded-md hover:bg-nlab-coral/90 text-sm"
                    >
                        Reintentar
                    </button>
                )}
            </td>
        </tr>
    );

    const rows = loadRows();

    return (
        <Card className="w-full h-full">
            <CardHeader className="bg-gray-50 rounded-t-xl">
                <div className="flex justify-between items-center">
                    <CardTitle className="flex items-center gap-2 text-nlab-black">
                        {cardTitle}
                        {isLoading && <Loader2 className="h-4 w-4 ml-2 loader-spin" />}
                    </CardTitle>
                </div>
            </CardHeader>
            <CardContent>
                <div className="overflow-x-auto">
                    <table className="w-full">
                        <thead>
                            <tr className="border-b">
                                {headers.map((header, index) => (
                                    <th
                                        key={`header-${index}`}
                                        className="px-4 py-3 text-left text-sm font-medium text-gray-500"
                                    >
                                        {header}
                                    </th>
                                ))}
                            </tr>
                        </thead>
                        <tbody>
                            {error ? renderErrorState() :
                                isLoading ? renderSkeletonRows() :
                                    rows || renderEmptyState()}
                        </tbody>
                    </table>
                </div>
            </CardContent>
        </Card>
    );
};

export default CardTable;