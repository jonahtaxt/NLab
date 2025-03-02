'use client';
import { fetchAllCardPaymentTypes, fetchAllPackageTypes, fetchAllPaymentMethods } from "@/app/lib/data.settings";
import { CardPaymentType, PackageType, PaymentMethod } from "@/app/lib/definitions";
import { TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Tabs } from "@radix-ui/react-tabs";
import { Loader2 } from "lucide-react";
import dynamic from "next/dynamic";
import { useEffect, useState } from "react";

const PackageTypeTable = dynamic(
    () => import("@/app/ui/settings/package-type-table"),
    {
        loading: () => <div className="p-8">
            <Loader2 className="mr-2 h-4 w-4 animate-spin" /> Cargando...
        </div>
    }
);

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
                    <TabsTrigger value="packages">Paquetes</TabsTrigger>
                    <TabsTrigger value="payments">M&eacute;todos de Pago</TabsTrigger>
                    <TabsTrigger value="cards">Pagos con Tarjeta</TabsTrigger>
                </TabsList>

                {/* <TabsContent value="packages">
                    {activeTab === "packages" && <PackageTypeTable />}
                </TabsContent> */}

                <TabsContent value="payments">
                    {activeTab === "payments" && <PaymentMethodTable />}
                </TabsContent>

                {/* <TabsContent value="cards">
                    {activeTab === "cards" && <CardPaymentTypeTable />}
                </TabsContent> */}
            </Tabs>
        </main>
    )
}