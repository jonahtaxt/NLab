'use client';

import { PackageType } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { CheckCircle, Pencil, Plus, XCircle } from "lucide-react";
import { useEffect, useState } from "react";
import PackageTypeForm from "@/app/ui/settings/package-type-form";
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { fetchAllPackageTypes } from "@/app/lib/data.settings";
import CardLoader from "@/components/ui/card-loader";
import CardErrorRetry from "@/components/ui/card-error-retry";

const PackageTypeTable = () => {
    const [packageTypes, setPackageTypes] = useState<PackageType[]>([]);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedPackageType, setSelectedPackageType] = useState<PackageType | null>(null);
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        loadPackageTypes();
    }, [packageTypes]);

    const loadPackageTypes = async () => {
        try {
            setLoading(true);
            const data = await fetchAllPackageTypes();
            setPackageTypes(data);
            setError(null);
        } catch (err) {
            console.error("Failed to load package types:", err);
            setError("Error al cargar los paquetes. Por favor intenta más tarde.");
        } finally {
            setLoading(false);
        }
    };

    const handleOpenDialog = (packageType?: PackageType) => {
        setSelectedPackageType(packageType || null);
        setDialogOpen(true);
    };

    const handleFormSubmitStart = () => {
        setFormSubmitting(true);
    };

    const handleFormSubmitEnd = () => {
        setFormSubmitting(false);
    };

    if (loading) {
        return (
            <CardLoader />
        );
    }

    if (error) {
        return (
            <CardErrorRetry error={error} retry={loadPackageTypes} />
        );
    }

    return (
        <>
            <Card className="w-full h-full">
                <CardHeader className="bg-gray-50 rounded-t-xl">
                    <div className="flex justify-between items-center">
                        <CardTitle className="flex itemts-center gap-2 bg-nl text-nlab-black">
                            Paquetes
                        </CardTitle>
                        <div className="flex gap-4 items-center">
                            <Button onClick={() => handleOpenDialog()} className="flex items-center gap-2">
                                <Plus className="w-4 h-4" /> Agregar
                            </Button>
                        </div>
                    </div>
                </CardHeader>
                <CardContent>
                    <div className="overflow-x-auto">
                        <table className="w-full">
                            <thead>
                                <tr className="border-b">
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Nombre</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Descripci&oacute;n</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">No. de Citas</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Paquete?</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Precio</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Comisi&oacute;n Nutri&oacute;logo</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Activo</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {packageTypes.length > 0 ? (
                                    packageTypes.map((packageType) => (
                                        <tr key={packageType.id} className="border-b hover:bg-gray-50">
                                            <td className="px-4 py-3 text-sm">
                                                {packageType.name}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {packageType.description}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {packageType.numberOfAppointments}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                <span className="inline-flex items-center rounded-full px-2 py-1 text-xs font-medium">
                                                    {packageType.bundle ?
                                                        <CheckCircle className="w-4 h-4 text-green-600" /> :
                                                        <XCircle className="w-4 h-4 text-red-600" />
                                                    }
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {packageType.price}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {packageType.nutritionistRate}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${packageType.active
                                                    ? 'bg-green-100 text-green-700'
                                                    : 'bg-gray-100 text-gray-700'
                                                    }`}>
                                                    {packageType.active ? 'Activo' : 'Inactivo'}
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-sm">
                                                <Button variant="ghost" onClick={() => handleOpenDialog(packageType)}>
                                                    <Pencil className="w-4 h-4 text-gray-600" />
                                                </Button>
                                            </td>
                                        </tr>
                                    ))) : (<tr>
                                        <td className="px-4 py-8 text-center text-gray-500" colSpan={5}>
                                            No se han encontrado paquetes con tu búsqueda
                                        </td>
                                    </tr>)}
                            </tbody>
                        </table>
                    </div>
                </CardContent>
            </Card>

            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{selectedPackageType ? 'Editar Paquete' : 'Agregar Paquete'}</DialogTitle>
                    </DialogHeader>
                    <PackageTypeForm
                        packageType={selectedPackageType}
                        onClose={() => setDialogOpen(false)}
                        onSuccess={loadPackageTypes}
                        onSubmitStart={handleFormSubmitStart}
                        onSubmitEnd={handleFormSubmitEnd}
                        isSubmitting={formSubmitting}
                    />
                </DialogContent>
            </Dialog>
        </>
    );
};

export default PackageTypeTable;