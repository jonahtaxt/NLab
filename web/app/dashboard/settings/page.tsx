'use client';

import { fetchAllCardPaymentTypes, fetchAllPaymentMethods } from "@/app/lib/data.settings";
import { CardPaymentType, PaymentMethod } from "@/app/lib/definitions";
import CardPaymentTypeTable from "@/app/ui/settings/card-payment-type-table";
import PaymentMethodTable from "@/app/ui/settings/payment-method-table";
import { useEffect, useState } from "react";

export default function Page() {
    const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
    const [cardPaymentTypes, setCardPaymentTypes] = useState<CardPaymentType[]>([]);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadPaymentMethods() {
            try {
                const data = await fetchAllPaymentMethods();
                setPaymentMethods(data);
            } catch (err) {
                console.error('Failed to load payment methods:', err);
                setError('Error al cargar los métodos de pago. Por favor, inténtalo de nuevo más tarde.');
            }
        }

        async function loadCardPaymentTypes() {
            try {
                const data = await fetchAllCardPaymentTypes();
                setCardPaymentTypes(data);
            } catch (err) {
                console.error('Failed to load card payment types:', err);
                setError('Error al cargar los tipos de pago con tarjeta. Por favor, inténtalo de nuevo más tarde.');
            }
        }

        loadPaymentMethods();
        loadCardPaymentTypes();
    }, []);

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }
    
    return (
        <main>
            <div className="flex flex-col md:flex-row md:gap-8">
                <div className="w-full md:w-1/2 mb-6 md:mb-0">
                    <PaymentMethodTable paymentMethods={paymentMethods} />
                </div>
                <div className="w-full md:w-1/2">
                    <CardPaymentTypeTable cardPaymentTypes={cardPaymentTypes} />
                </div>
            </div>
        </main>
    )
}