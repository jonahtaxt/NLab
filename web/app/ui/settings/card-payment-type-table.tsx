'use client';

import { fetchAllCardPaymentTypes } from "@/app/lib/data.settings";
import { CardPaymentType, PaymentMethod } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { showToast } from "@/lib/toaster-util";
import { Loader2, Pencil } from "lucide-react";
import { useEffect, useState } from "react";

const CardPaymentTypeTable = ({ cardPaymentTypes } : { cardPaymentTypes: CardPaymentType[] }) => {
    const [isRefreshing, setIsRefreshing] = useState(false);
    const [cardPaymentTypesList, setCardPaymentTypesList] = useState<CardPaymentType[]>([]);

    useEffect(() => {
        setCardPaymentTypesList(cardPaymentTypes);
    }, [cardPaymentTypes]);

    const refreshPaymentMethods = async () => {
        setIsRefreshing(true);
        try {
            const refreshedCardPaymentTypes = await fetchAllCardPaymentTypes();
            setCardPaymentTypesList(refreshedCardPaymentTypes);
            if (isRefreshing) setIsRefreshing(false);
        } catch (err) {
            console.error('Failed to load payment methods:', err);
            showToast.error('Error al cargar los métodos de pago. Por favor, inténtalo de nuevo más tarde.');
        } finally {
            setIsRefreshing(false);
        }
    };

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
                                {cardPaymentTypesList.map((cardPaymentType) => (
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
                                                <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                                                    cardPaymentType.active
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