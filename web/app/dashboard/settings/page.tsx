'use client';

import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Loader2 } from "lucide-react";
import dynamic from "next/dynamic";
import { useState } from "react";

const PaymentMethodTable = dynamic(
    () => import("@/app/ui/settings/payment-method-table"),
    {
        loading: () => <div className="p-8">
            <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Cargando...
        </div>
    }
);

const CardPaymentTypeTable = dynamic(
    () => import("@/app/ui/settings/card-payment-type-table"),
    {
        loading: () => <div className="p-8">
            <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Cargando...
        </div>
    }
);

export default function Page() {
    const [activeTab, setActiveTab] = useState("payments");

    return (
        <main className="space-y-8">
            <Tabs value={activeTab} onValueChange={setActiveTab}>
                <TabsList>
                    <TabsTrigger value="payments">M&eacute;todos de Pago</TabsTrigger>
                    <TabsTrigger value="cards">Pagos con Tarjeta</TabsTrigger>
                </TabsList>

                <TabsContent value="payments">
                    {activeTab === "payments" && <PaymentMethodTable />}
                </TabsContent>

                <TabsContent value="cards">
                    {activeTab === "cards" && <CardPaymentTypeTable />}
                </TabsContent>
            </Tabs>
        </main>
    )
}