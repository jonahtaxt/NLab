'use client';

import { PackageType } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { CheckCircle, Pencil, Plus, XCircle } from "lucide-react";
import { useEffect, useState } from "react";
import PackageTypeForm from "@/app/ui/settings/package-type-form";
import { Dialog, DialogContent, DialogTitle } from "@radix-ui/react-dialog";
import { DialogHeader } from "@/components/ui/dialog";
import { showToast } from "@/lib/toaster-util";
import { fetchAllPackageTypes } from "@/app/lib/data.settings";

const PackageTypeTable = ({ packageTypes } : { packageTypes: PackageType[] }) => {
    const[packageTypesList, setPackageTypesList] = useState<PackageType[]>([]);
    const [dialogOpen, setDialogOpen] = useState(false);
    const [selectedPackageType, setSelectedPackageType] = useState<PackageType | null>(null);
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [isRefreshing, setIsRefreshing] = useState(false);

    useEffect(() => {
        setPackageTypesList(packageTypes);
    }, [packageTypes]);

    const refreshPackageTypes = async () => {
        setIsRefreshing(true);
        try {
            const refreshedPackageTypes = await fetchAllPackageTypes();
            setPackageTypesList(refreshedPackageTypes);
            if (isRefreshing) {
                showToast.info('Lista de Paquetes actualizada');
            }
        } catch (err) {
            console.error('Failed to load package types:', err);
            showToast.error('Error al cargar los paquetes. Por favor, inténtalo de nuevo más tarde.');
        } finally {
            setIsRefreshing(false);
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
                                {packageTypesList.length > 0 ? (
                                    packageTypesList.map((packageType) => (
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
                                            <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                                                    packageType.isBundle
                                                        ? 'bg-green-100 text-green-700' 
                                                        : 'bg-gray-100 text-gray-700'
                                                    }`}>
                                                    {packageType.isBundle ? <CheckCircle /> : <XCircle />}
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {packageType.price}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {packageType.nutritionistRate}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                                                    packageType.active
                                                        ? 'bg-green-100 text-green-700' 
                                                        : 'bg-gray-100 text-gray-700'
                                                    }`}>
                                                    {packageType.active ? 'Activo' : 'Inactivo'}
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-sm">
                                                <Button variant="ghost" onClick={() => alert('Edit')}>
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
                        onSuccess={refreshPackageTypes}
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