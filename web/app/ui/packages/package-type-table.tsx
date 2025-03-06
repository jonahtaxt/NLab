'use client';

import { useState } from 'react';
import { PackageType } from "@/app/lib/definitions";
import { Search, Plus, Loader2, CheckCircle, XCircle } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { authDelete } from '@/app/lib/auth';
import { showToast } from '@/lib/toaster-util';
import CardTable from '@/components/ui/card-table';
import PackageTypeForm from './package-type-form';

interface PackageTypeTableProps {
    packageTypes: PackageType[];
    onSearch: (term: string) => void;
    onSort: (column: string) => void;
    sortBy: string;
    sortDirection: string;
    isLoading: boolean;
    onRefresh?: () => void;
    error?: string | null;
}

const PackageTypeTable = ({
    packageTypes: packageTypes,
    onSearch,
    onSort,
    sortBy,
    sortDirection,
    isLoading,
    onRefresh,
    error = null
}: PackageTypeTableProps) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [deactivateDialogOpen, setDeactivateDialogOpen] = useState(false);
    const [selectedPackageType, setSelectedPackageType] = useState<PackageType | null>(null);
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [isDeactivating, setIsDeactivating] = useState(false);

    const handleSearchInput = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    }

    const handleSearchSubmit = () => {
        onSearch(searchTerm);
    };

    const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            handleSearchSubmit();
        }
    };

    const handleOpenDialog = (packageType?: PackageType) => {
        setSelectedPackageType(packageType || null);
        setDialogOpen(true);
    };

    const handleDeactivateDialogOpen = (packageType: PackageType) => {
        setSelectedPackageType(packageType);
        setDeactivateDialogOpen(true);
    };

    const deactivatePackage = async () => {
        if (!selectedPackageType) return;

        try {
            setIsDeactivating(true);
            // Deactivate patient
            await authDelete(`/api/package-types/${selectedPackageType.id}`);
            // Show success toast
            showToast.success('Paquete desactivado');
            setDeactivateDialogOpen(false);
            // Refresh data
            if (onRefresh) {
                onRefresh();
            }
        } catch (error) {
            console.error('Failed to deactivate package type:', error);
            showToast.error('Error al desactivar paquete');
        } finally {
            setIsDeactivating(false);
        }
    };

    const handleFormSubmitStart = () => {
        setFormSubmitting(true);
    };

    const handleFormSubmitEnd = () => {
        setFormSubmitting(false);
    };

    // Custom header component with search and add button
    const customCardHeader = (
        <div className="flex gap-4 items-center">
            <div className="relative">
                <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-500" />
                <input
                    type="text"
                    placeholder="Buscar paquetes..."
                    value={searchTerm}
                    onChange={handleSearchInput}
                    onKeyDown={handleSearchKeyDown}
                    className="pl-8 pr-4 py-2 border rounded-md w-full md:w-64 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                />
                <Button
                    variant="ghost"
                    size="sm"
                    onClick={handleSearchSubmit}
                    className="absolute right-1 top-1"
                    disabled={isLoading} // Disable button during loading
                >
                    Buscar
                </Button>
            </div>
            <Button onClick={() => handleOpenDialog()} className="flex items-center gap-2">
                <Plus className="w-4 h-4" /> Agregar
            </Button>
        </div>
    );

    // Function to render rows
    const renderRows = () => {
        if (packageTypes.length === 0) {
            return null; // Let CardTable handle empty state
        }

        return packageTypes.map((packageType) => (
            <tr key={packageType.id} className="border-b hover:bg-gray-50">
                <td className="px-4 py-3 text-sm">
                    {packageType.name}
                </td>
                <td className="px-4 py-3 text-sm">
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
                <td className="px-4 py-3 text-sm">
                    <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${packageType.active
                        ? 'bg-green-100 text-green-700'
                        : 'bg-gray-100 text-gray-700'
                        }`}>
                        {packageType.active ? 'Activo' : 'Inactivo'}
                    </span>
                </td>
                <td className="px-4 py-3 text-sm">
                    <Button variant="ghost" onClick={() => handleOpenDialog(packageType)}>
                        <span className="sr-only">Edit</span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-4 h-4 text-gray-600">
                            <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                            <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                        </svg>
                    </Button>
                    <Button variant="ghost" disabled={!packageType.active} onClick={() => handleDeactivateDialogOpen(packageType)}>
                        <span className="sr-only">Delete</span>
                        <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="w-4 h-4 text-gray-600">
                            <path d="M3 6h18"></path>
                            <path d="M19 6v14c0 1-1 2-2 2H7c-1 0-2-1-2-2V6"></path>
                            <path d="M8 6V4c0-1 1-2 2-2h4c1 0 2 1 2 2v2"></path>
                        </svg>
                    </Button>
                </td>
            </tr>
        ));
    };

    // EmptyState component
    const emptyState = (
        <tr>
            <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
                No se han encontrado paquetes con tu búsqueda
            </td>
        </tr>
    );

    // Function to get sort indicator for column headers
    const getSortIndicator = (column: string) => {
        if (sortBy !== column) return null;

        return sortDirection === 'ASC'
            ? <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="ml-1 h-4 w-4">
                <path d="m18 15-6-6-6 6" />
            </svg>
            : <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" className="ml-1 h-4 w-4">
                <path d="m6 9 6 6 6-6" />
            </svg>;
    };

    // Create table headers with sort functionality
    const headers = [
        {
            label: 'Nombre',
            onClick: () => handleSortClick('name'),
            sortIndicator: getSortIndicator('name')
        },
        {
            label: 'Descripción',
            onClick: () => handleSortClick('description'),
            sortIndicator: getSortIndicator('description')
        },
        { label: 'Total Citas' },
        { label: 'Paquete' },
        { label: 'Costo' },
        { label: 'Comisión Nutriólogo' },
        { label: 'Estatus' },
        { label: 'Acciones' }
    ];

    const handleSortClick = (column: string) => {
        onSort(column);
    };

    return (
        <>
            <CardTable
                cardTitle="Paquetes"
                headers={headers.map(header => (
                    header.onClick ? (
                        <div
                            className="flex items-center cursor-pointer"
                            onClick={header.onClick}
                        >
                            {header.label}
                            {header.sortIndicator}
                        </div>
                    ) : header.label
                ))}
                loadRows={renderRows}
                isLoading={isLoading}
                emptyState={emptyState}
                customCardHeader={customCardHeader}
                error={error}
                onRetry={onRefresh}
            />

            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{selectedPackageType ? 'Editar Paciente' : 'Agregar Paciente'}</DialogTitle>
                    </DialogHeader>
                    <PackageTypeForm
                        packageType={selectedPackageType}
                        onClose={() => setDialogOpen(false)}
                        onSuccess={onRefresh}
                        onSubmitStart={handleFormSubmitStart}
                        onSubmitEnd={handleFormSubmitEnd}
                        isSubmitting={formSubmitting}
                    />
                </DialogContent>
            </Dialog>

            <Dialog open={deactivateDialogOpen} onOpenChange={setDeactivateDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>Desactivar Paquete</DialogTitle>
                    </DialogHeader>
                    <div className="p-4">
                        <p>¿Estás seguro que deseas desactivar este paquete?</p>
                        <p className="text-sm text-gray-500 mt-2">
                            Nombre: {selectedPackageType?.name} {selectedPackageType?.description}
                        </p>
                    </div>
                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={() => setDeactivateDialogOpen(false)}
                            disabled={isDeactivating}
                        >
                            Cancelar
                        </Button>
                        <Button
                            variant="destructive"
                            onClick={deactivatePackage}
                            disabled={isDeactivating}
                        >
                            {isDeactivating ? (
                                <>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                    Desactivando...
                                </>
                            ) : (
                                'Desactivar'
                            )}
                        </Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </>
    );
};

export default PackageTypeTable;