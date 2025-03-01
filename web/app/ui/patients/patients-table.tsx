'use client';

import { useState } from 'react';
import { Patient } from "@/app/lib/definitions";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Search, Plus, Pencil, ArrowUp, ArrowDown, Loader2, Trash } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import PatientForm from '@/app/ui/patients/patient-form';
import { authDelete } from '@/app/lib/auth';
import { showToast } from '@/lib/toaster-util';

interface PatientTableProps {
    patients: Patient[];
    onSearch: (term: string) => void;
    onSort: (column: string) => void;
    sortBy: string;
    sortDirection: string;
    loading: boolean;
    onRefresh?: () => void;
}

const PatientTable = ({
    patients,
    onSearch,
    onSort,
    sortBy,
    sortDirection,
    loading,
    onRefresh
}: PatientTableProps) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [deactivateDialogOpen, setDeactivateDialogOpen] = useState(false);
    const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [isDeactivating, setIsDeactivating] = useState(false);

    const handleSearchInput = (e: React.ChangeEvent<HTMLInputElement>) => {
        setSearchTerm(e.target.value);
    };

    const handleSearchSubmit = () => {
        onSearch(searchTerm);
    };

    const handleSearchKeyDown = (e: React.KeyboardEvent<HTMLInputElement>) => {
        if (e.key === 'Enter') {
            handleSearchSubmit();
        }
    };

    const handleSortClick = (column: string) => {
        onSort(column);
    };
  
    const handleOpenDialog = (patient?: Patient) => {
        setSelectedPatient(patient || null);
        setDialogOpen(true);
    };

    const handleDeactivateDialogOpen = (patient: Patient) => {
        setSelectedPatient(patient);
        setDeactivateDialogOpen(true);
    };

    const deactivatePatient = async () => {
        if (!selectedPatient) return;
        
        try {
            setIsDeactivating(true);
            // Deactivate patient
            await authDelete(`/api/patients/${selectedPatient.id}`);
            // Show success toast
            showToast.success('Paciente desactivado');
            setDeactivateDialogOpen(false);
            // Refresh data
            if (onRefresh) {
                onRefresh();
            }
        } catch (error) {
            console.error('Failed to deactivate patient:', error);
            showToast.error('Error al desactivar paciente');
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

    // Helper function to render sort indicator
    const getSortIndicator = (column: string) => {
        if (sortBy !== column) return null;
        
        return sortDirection === 'ASC' 
            ? <ArrowUp className="ml-1 h-4 w-4" />
            : <ArrowDown className="ml-1 h-4 w-4" />;
    };
  
    return (
        <>
            <Card className="w-full">
                <CardHeader className='bg-gray-50 rounded-t-xl'>
                    <div className="flex justify-between items-center">
                        <CardTitle className="flex items-center gap-2 bg-nl text-nlab-black">
                            Pacientes
                            {loading && <Loader2 className="animate-spin h-4 w-4" />}
                        </CardTitle>
                        <div className="flex gap-4 items-center">
                            <div className="relative">
                                <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-500" />
                                <input
                                    type="text"
                                    placeholder="Buscar pacientes..."
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
                                >
                                    Buscar
                                </Button>
                            </div>
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
                                    <th 
                                        className="px-4 py-3 text-left text-sm font-medium text-gray-500 cursor-pointer"
                                        onClick={() => handleSortClick('firstName')}
                                    >
                                        <div className="flex items-center">
                                            Nombre
                                            {getSortIndicator('firstName')}
                                        </div>
                                    </th>
                                    <th 
                                        className="px-4 py-3 text-left text-sm font-medium text-gray-500 cursor-pointer"
                                        onClick={() => handleSortClick('lastName')}
                                    >
                                        <div className="flex items-center">
                                            Apellido
                                            {getSortIndicator('lastName')}
                                        </div>
                                    </th>
                                    <th 
                                        className="px-4 py-3 text-left text-sm font-medium text-gray-500 cursor-pointer"
                                        onClick={() => handleSortClick('email')}
                                    >
                                        <div className="flex items-center">
                                            Correo
                                            {getSortIndicator('email')}
                                        </div>
                                    </th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Teléfono</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Estatus</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {loading && patients.length === 0 ? (
                                    <tr>
                                        <td className="px-4 py-8 text-center text-gray-500" colSpan={6}>
                                            <div className="flex justify-center items-center">
                                                <Loader2 className="animate-spin h-5 w-5 mr-2" />
                                                Cargando pacientes...
                                            </div>
                                        </td>
                                    </tr>
                                ) : patients.length > 0 ? (
                                    patients.map((patient) => (
                                        <tr key={patient.id} className="border-b hover:bg-gray-50">
                                            <td className="px-4 py-3 text-sm">
                                                {patient.firstName}
                                            </td>
                                            <td className="px-4 py-3 text-sm">
                                                {patient.lastName}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {patient.email}
                                            </td>
                                            <td className="px-4 py-3 text-sm text-gray-600">
                                                {patient.phone}
                                            </td>
                                            <td className="px-4 py-3 text-sm">
                                                <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${
                                                patient.active
                                                    ? 'bg-green-100 text-green-700' 
                                                    : 'bg-gray-100 text-gray-700'
                                                }`}>
                                                {patient.active ? 'Activo' : 'Inactivo'}
                                                </span>
                                            </td>
                                            <td className="px-4 py-3 text-sm">
                                                <Button variant="ghost" onClick={() => handleOpenDialog(patient)}>
                                                    <Pencil className="w-4 h-4 text-gray-600" />
                                                </Button>
                                                <Button variant="ghost" disabled={!patient.active} onClick={() => handleDeactivateDialogOpen(patient)}>
                                                    <Trash className="w-4 h-4 text-gray-600" />
                                                </Button>
                                            </td>
                                        </tr>
                                    ))
                                ) : (
                                    <tr>
                                        <td className="px-4 py-8 text-center text-gray-500" colSpan={6}>
                                            No se han encontrado pacientes con tu búsqueda
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </CardContent>
            </Card>
            
            <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
                <DialogContent>
                    <DialogHeader>
                        <DialogTitle>{selectedPatient ? 'Editar Paciente' : 'Agregar Paciente'}</DialogTitle>
                    </DialogHeader>
                    <PatientForm 
                        patient={selectedPatient} 
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
                        <DialogTitle>Desactivar Paciente</DialogTitle>
                    </DialogHeader>
                    <div className="p-4">
                        <p>¿Estás seguro que deseas desactivar a este paciente?</p>
                        <p className="text-sm text-gray-500 mt-2">
                            Nombre: {selectedPatient?.firstName} {selectedPatient?.lastName}
                        </p>
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setDeactivateDialogOpen(false)} disabled={isDeactivating}>
                            Cancelar
                        </Button>
                        <Button 
                            variant="destructive"
                            onClick={deactivatePatient}
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
  
export default PatientTable;