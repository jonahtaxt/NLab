'use client';

import { useState, useEffect } from 'react';
import { Patient } from "@/app/lib/definitions";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Search, Plus, Pencil, Loader2, Trash } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import PatientForm from '@/app/ui/patients/patient-form';
import { fetchAllPatients } from '@/app/lib/data.patient';
import { showToast } from '@/lib/toaster-util';
import { authDelete } from '@/app/lib/auth';

const PatientTable = ({ patients }: { patients: Patient[] }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [dialogOpen, setDialogOpen] = useState(false);
    const [deactivateDialogOpen, setDeactivateDialogOpen] = useState(false);
    const [selectedPatient, setSelectedPatient] = useState<Patient | null>(null);
    const [patientsList, setPatientsList] = useState<Patient[]>(patients);
    const [isRefreshing, setIsRefreshing] = useState(false);
    const [formSubmitting, setFormSubmitting] = useState(false);
    const [isDeactivating, setIsDeactivating] = useState(false);

    // Update local state when props change
    useEffect(() => {
        setPatientsList(patients);
    }, [patients]);
  
    // Refresh function with loading state
    const refreshPatients = async () => {
        setIsRefreshing(true);
        try {
            const refreshedPatients = await fetchAllPatients();
            setPatientsList(refreshedPatients);
            // Don't show a success toast when it's the initial load
            if (isRefreshing) {
                showToast.info('Lista de pacientes actualizada');
            }
        } catch (error) {
            console.error('Failed to refresh patients:', error);
            showToast.error('Error al cargar la lista de pacientes');
        } finally {
            setIsRefreshing(false);
        }
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
        try {
            setIsDeactivating(true);
            // Deactivate patient
            await authDelete(`/api/patients/${selectedPatient?.id}`);
            // Show success toast
            showToast.success('Paciente desactivado');
            setDeactivateDialogOpen(false);
            // Refresh patients
            refreshPatients();
        } catch (error) {
            console.error('Failed to deactivate patient:', error);
            showToast.error('Error al desactivar paciente');
        } finally {
            setIsDeactivating(false);
            refreshPatients();
        }
    };

    const handleFormSubmitStart = () => {
        setFormSubmitting(true);
    };

    const handleFormSubmitEnd = () => {
        setFormSubmitting(false);
    };
  
    const filteredPatients = patientsList.filter(patient => {
        const searchString = searchTerm.toLowerCase();
        const fullName = `${patient.firstName} ${patient.lastName}`.toLowerCase();
        return fullName.includes(searchString) ||
               patient.email.toLowerCase().includes(searchString) ||
               patient.phone.includes(searchString);
    });
  
    return (
        <>
            <Card className="w-full">
                <CardHeader className='bg-gray-50 rounded-t-xl'>
                    <div className="flex justify-between items-center">
                        <CardTitle className="flex items-center gap-2 bg-nl text-nlab-black">
                            Pacientes
                            {isRefreshing && <Loader2 className="animate-spin h-4 w-4" />}
                        </CardTitle>
                        <div className="flex gap-4 items-center">
                            <div className="relative">
                                <Search className="absolute left-2 top-2.5 h-4 w-4 text-gray-500" />
                                <input
                                    type="text"
                                    placeholder="Buscar pacientes..."
                                    value={searchTerm}
                                    onChange={(e) => setSearchTerm(e.target.value)}
                                    className="pl-8 pr-4 py-2 border rounded-md w-full md:w-64 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:border-transparent"
                                />
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
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Nombre</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Correo</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Teléfono</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Estatus</th>
                                    <th className="px-4 py-3 text-left text-sm font-medium text-gray-500">Acciones</th>
                                </tr>
                            </thead>
                            <tbody>
                                {isRefreshing && filteredPatients.length === 0 ? (
                                    <tr>
                                        <td className="px-4 py-8 text-center text-gray-500" colSpan={5}>
                                            <div className="flex justify-center items-center">
                                                <Loader2 className="animate-spin h-5 w-5 mr-2" />
                                                Cargando pacientes...
                                            </div>
                                        </td>
                                    </tr>
                                ) : filteredPatients.length > 0 ? (
                                    filteredPatients.map((patient) => (
                                        <tr key={patient.id} className="border-b hover:bg-gray-50">
                                            <td className="px-4 py-3 text-sm">
                                                {patient.firstName} {patient.lastName}
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
                                        <td className="px-4 py-8 text-center text-gray-500" colSpan={5}>
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
                        onSuccess={refreshPatients}
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
                    </div>
                    <DialogFooter>
                        <Button variant="outline" onClick={() => setDeactivateDialogOpen(false)}>Cancelar</Button>
                        <Button type="button"
                            variant="destructive"
                            onClick={() => deactivatePatient()}>
                                {isDeactivating ? (
                                    <>
                                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                        Desactivando...
                                    </>
                                ) : (
                                    'Desactivar'
                                )}</Button>
                    </DialogFooter>
                </DialogContent>
            </Dialog>
        </>
    );
};
  
export default PatientTable;