'use client';

import { fetchAllPaymentMethods } from "@/app/lib/data.settings";
import { useTableData } from "@/app/lib/data.tables";
import { PaymentMethod } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import CardTable from "@/components/ui/card-table";
import { showToast } from "@/lib/toaster-util";
import { Pencil } from "lucide-react";
import { useState } from "react";

const PaymentMethodTable = () => {
    // This state can be used to force a refresh when needed
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const {
        data: paymentMethods,
        isLoading,
        error,
        refresh
    } = useTableData<PaymentMethod>({
        fetchFunction: fetchAllPaymentMethods,
        initialData: [],
        onError: (err) => {
            console.error("Error fetching payment methods:", err);
            showToast.error('Error al cargar los métodos de pago');
        },
        dependencies: [refreshTrigger]
    });

    const handleEdit = (paymentMethod: PaymentMethod) => {
        // Implementation for editing a payment method
        alert(`Edit payment method: ${paymentMethod.name}`);
    };

    // Function to render table rows
    const renderRows = () => {
        if (!paymentMethods || paymentMethods.length === 0) {
            return null; // Let the CardTable component handle the empty state
        }

        return paymentMethods.map((paymentMethod) => (
            <tr key={paymentMethod.id} className="border-b hover:bg-gray-50">
                <td className="px-4 py-3 text-sm">
                    {paymentMethod.name}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                    {paymentMethod.description || '-'}
                </td>
                <td className="px-4 py-3 text-sm text-gray-600">
                    {paymentMethod.displayOrder || '-'}
                </td>
                <td className="px-4 py-3 text-sm">
                    <Button variant="ghost" onClick={() => handleEdit(paymentMethod)}>
                        <Pencil className="w-4 h-4 text-gray-600" />
                    </Button>
                </td>
            </tr>
        ));
    };

    // Empty state component
    const emptyState = (
        <tr>
            <td colSpan={4} className="px-4 py-8 text-center text-gray-500">
                No se han encontrado métodos de pago
            </td>
        </tr>
    );

    return (
        <CardTable
            cardTitle="Métodos de Pago"
            headers={['Nombre', 'Descripción', 'Orden', 'Acciones']}
            loadRows={renderRows}
            isLoading={isLoading}
            error={error}
            onRetry={refresh}
            emptyState={emptyState}
        />
    );
};

export default PaymentMethodTable;