'use client';

import { fetchAllCardPaymentTypes } from "@/app/lib/data.settings";
import { useTableData } from "@/app/hooks/useTableData";
import { CardPaymentType, PaymentMethod } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import CardTable from "@/components/ui/card-table";
import { showToast } from "@/lib/toaster-util";
import { Pencil } from "lucide-react";
import { useState } from "react";

const CardPaymentTypeTable = () => {
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const {
        data: cardPaymentTypes,
        isLoading,
        error,
        refresh
    } = useTableData<CardPaymentType[]>({
        fetchFunction: fetchAllCardPaymentTypes,
        initialData: [],
        onError: (err) => {
            console.error("Error fetching card payment types:", err);
            showToast.error('Error al cargar los tipos de pagos con tarjeta');
        },
        dependencies: [refreshTrigger]
    });

    const renderRows = () => {
        if (!cardPaymentTypes || cardPaymentTypes.length === 0) {
            return null;
        }

        return cardPaymentTypes.map((cardPaymentType) => (
            <tr key={cardPaymentType.id} className="border-b hover:bg-gray-50">
                <td className="px-4 py-3 text-sm">
                    {cardPaymentType.name}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                    {cardPaymentType.description}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                    {cardPaymentType.bankFeePercentage}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                    {cardPaymentType.numberOfInstallments}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                    <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${cardPaymentType.active
                        ? 'bg-green-100 text-green-700'
                        : 'bg-gray-100 text-gray-700'
                        }`}>
                        {cardPaymentType.active ? 'Activo' : 'Inactivo'}
                    </span>
                </td>
                <td className="px-4 py-3 text-sm">
                    <Button variant="ghost" onClick={() => alert('Edit')}>
                        <Pencil className="w-4 h-4 text-gray-600" />
                    </Button>
                </td>
            </tr>
        ));
    };

    const emptyState = (
        <tr>
            <td colSpan={5} className="px-4 py-8 text-center text-gray-500">
                No hay tipos de pagos con tarjeta
            </td>
        </tr>
    );

    return (
        <CardTable
            cardTitle="Tipos de Pagos con Tarjeta"
            headers={[
                'Nombre',
                'Descripción',
                'Porcentaje Bancario',
                'Activo',
                'Acciones'
            ]}
            loadRows={renderRows}
            isLoading={isLoading}
            error={error}
            onRetry={refresh}
            emptyState={emptyState}
        />
    );
};

export default CardPaymentTypeTable;