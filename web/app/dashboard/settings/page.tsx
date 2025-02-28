'use client';

import { fetchAllPaymentMethods } from "@/app/lib/data.paymentmethod";
import { PaymentMethod } from "@/app/lib/definitions";
import PaymentMethodTable from "@/app/ui/settings/payment-method-table";
import { useEffect, useState } from "react";

export default function Page() {
    const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
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

        loadPaymentMethods();
    }, []);

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }
    
    return (
        <main>
            <PaymentMethodTable paymentMethods={paymentMethods} />
        </main>
    )
}