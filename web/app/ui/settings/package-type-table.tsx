'use client';

import { PackageType } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { CheckCircle, Pencil, Plus, XCircle } from "lucide-react";
import { fetchAllPackageTypes } from "@/app/lib/data.settings";
import { showToast } from "@/lib/toaster-util";
import { useTableData } from "@/app/hooks/useTableData";
import CardTable from "@/components/ui/card-table";
import { useState } from "react";

const PackageTypeTable = () => {
    const [refreshTrigger, setRefreshTrigger] = useState(0);

    const {
        data: packageTypes,
        isLoading,
        error,
        refresh
    } = useTableData<PackageType[]>({
        fetchFunction: fetchAllPackageTypes,
        initialData: [],
        onError: (err) => {
            console.error("Error fetching package types:", err);
            showToast.error('Error al cargar los paquetes');
        },
        dependencies: [refreshTrigger]
    });

    const handleEdit = (packageType: PackageType) => {
        // Implementation for editing a package type
        alert(`Edit package type: ${packageType.name}`);
    };

    const renderRows = () => {
        if (!packageTypes || packageTypes.length === 0) {
            return null;
        }

        return packageTypes.map((packageType) => (
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
                    <Button variant="ghost" onClick={() => handleEdit(packageType)}>
                        <Pencil className="w-4 h-4 text-gray-600" />
                    </Button>
                </td>
            </tr>
        ));
    };

    const emptyState = (
        <tr>
            <td colSpan={8} className="px-4 py-8 text-center text-gray-500">
                No se han encontrado paquetes
            </td>
        </tr>
    );

    return (
        <CardTable
            cardTitle="Paquetes"
            headers={['Nombre', 'Descripción', 'No. de Citas', 'Paquete?', 'Precio', 'Comisión Nutriólogo', 'Activo', 'Acciones']}
            loadRows={renderRows}
            isLoading={isLoading}
            error={error}
            onRetry={refresh}
            emptyState={emptyState}
        />
    );
}

// return (


//         <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
//             <DialogContent>
//                 <DialogHeader>
//                     <DialogTitle>{selectedPackageType ? 'Editar Paquete' : 'Agregar Paquete'}</DialogTitle>
//                 </DialogHeader>
//                 <PackageTypeForm
//                     packageType={selectedPackageType}
//                     onClose={() => setDialogOpen(false)}
//                     onSuccess={loadPackageTypes}
//                     onSubmitStart={handleFormSubmitStart}
//                     onSubmitEnd={handleFormSubmitEnd}
//                     isSubmitting={formSubmitting}
//                 />
//             </DialogContent>
//         </Dialog>
//     </>
// );

export default PackageTypeTable;