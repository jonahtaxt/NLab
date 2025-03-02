'use client';

import { fetchAllCardPaymentTypes } from "@/app/lib/data.settings";
import { CardPaymentType, PaymentMethod } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import CardErrorRetry from "@/components/ui/card-error-retry";
import CardLoader from "@/components/ui/card-loader";
import { Pencil } from "lucide-react";
import { useEffect, useState } from "react";

const CardPaymentTypeTable = () => {
    const [cardPaymentTypes, setCardPaymentTypes] = useState<CardPaymentType[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadCardPaymentTypes();
    }, [cardPaymentTypes]);

    const loadCardPaymentTypes = async () => {
        try {
            const data = await fetchAllCardPaymentTypes();
            setCardPaymentTypes(data);
            setError(null);
        } catch (err) {
            console.error("Failed to load card payment types:", err);
            setError("Ocurrió un error al cargar los Pagos con Tarjeta. Por favor intenta más tarde.");
        } finally {
            setLoading(false);
        }
    };

    if (loading) {
        return (
            <CardLoader />
        );
    }

    if (error) {
        return (
            <CardErrorRetry error={error} retry={loadCardPaymentTypes} />
        );
    }

    return (
        <>
            <Card className="w-full h-full">
                <CardHeader className="bg-gray-50 rounded-t-xl">
                    <div className="flex justify-between items-center">
                        <CardTitle className="flex itemts-center gap-2 bg-nl text-nlab-black">
                            Tipos de Pagos con Tarjeta
                        </CardTitle>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                                <tr className="border-b">
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Nombre</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Descripci&oacute;n</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Porcentaje Bancario</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Activo</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {cardPaymentTypes.map((cardPaymentType) => (
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
                                ))}
                            </tbody>
                        </table>
                    </div>
                </CardContent>
            </Card>
        </>
    );
};

export default CardPaymentTypeTable;