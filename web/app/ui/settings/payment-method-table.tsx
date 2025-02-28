'use client';

import { fetchAllPaymentMethods } from "@/app/lib/data.paymentmethod";
import { PaymentMethod } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { showToast } from "@/lib/toaster-util";
import { Loader2, Pencil } from "lucide-react";
import { useEffect, useState } from "react";

const PaymentMethodTable = ({ paymentMethods } : { paymentMethods: PaymentMethod[] }) => {
    const [isRefreshing, setIsRefreshing] = useState(false);
    const [paymentMethodsList, setPaymentMethods] = useState<PaymentMethod[]>([]);

    useEffect(() => {
        setPaymentMethods(paymentMethods);
    }, [paymentMethods]);

    const refreshPaymentMethods = async () => {
        setIsRefreshing(true);
        try {
            const refreshedPaymentMethods = await fetchAllPaymentMethods();
            setPaymentMethods(refreshedPaymentMethods);
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
            <Card className="w-10">
                <CardHeader className="bg-gray-50 rounded-t-xl">
                    <div className="flex justify-between items-center">
                        <CardTitle className="flex itemts-center gap-2 bg-nl text-nlab-black">
                            M&eacute;todos de Pago
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
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Orden</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {paymentMethods.map((paymentMethod) => (
                                        <tr key={paymentMethod.id} className="border-b hover:bg-gray-50">
                                            <td className="px-4 py-3 text-sm">
                                                {paymentMethod.name}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {paymentMethod.description}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {paymentMethod.displayOrder}
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

export default PaymentMethodTable;