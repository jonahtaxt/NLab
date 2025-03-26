'use client';

import { PackageTypeSelectDTO, Patient } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { useState } from "react";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { fetchSelectPackageTypes } from "@/app/lib/data.package-type";

interface PatientDetailViewProps {
  patient: Patient;
  onBack?: () => void; // Made optional as we'll use router navigation in the page component
}

const PatientDetailView = ({ patient, onBack }: PatientDetailViewProps) => {
  if (!patient) return null;

  const [addPackageDialogOpen, setAddPackageDialogOpen] = useState(false);
  const [packageTypes, setPackageTypes] = useState<PackageTypeSelectDTO[]>([]);

  const handleAddPackageButtonClick = async (patientId: number) => {
    setAddPackageDialogOpen(true);
    setPackageTypes(await fetchSelectPackageTypes());
  };

  const handleAddAppointment = (patientId: number) => {
    alert(1);
  }

  return (
    <>
      <div className="w-full h-full">
        <div className="flex flex-col md:flex-row gap-4">
          <div className="w-full md:w-1/3">
            <Card>
              <CardHeader className="bg-gray-50 rounded-t-xl">
                <CardTitle className="flex items-center gap-2 text-nlab-black">
                  Información Personal
                </CardTitle>
              </CardHeader>
              <CardContent className="p-6">
                <div className="mb-4">
                  <p className="text-sm text-gray-500">Nombre Completo</p>
                  <p className="font-medium">{patient.firstName} {patient.lastName}</p>
                </div>
                <div className="mb-4">
                  <p className="text-sm text-gray-500">Correo Electrónico</p>
                  <p className="font-medium">{patient.email}</p>
                </div>
                <div className="mb-4">
                  <p className="text-sm text-gray-500">Teléfono</p>
                  <p className="font-medium">{patient.phone}</p>
                </div>
                <div className="mb-4">
                  <p className="text-sm text-gray-500">Estado</p>
                  <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${patient.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                    }`}>
                    {patient.active ? 'Activo' : 'Inactivo'}
                  </span>
                </div>
                <div className="mb-4">
                  <p className="text-sm text-gray-500">Fecha de Creación</p>
                  <p className="font-medium">
                    {new Date(patient.createdAt).toLocaleDateString('es-MX', {
                      day: '2-digit',
                      month: '2-digit',
                      year: 'numeric'
                    })}
                  </p>
                </div>
                {patient.updatedAt && (
                  <div className="mb-4">
                    <p className="text-sm text-gray-500">Última Actualización</p>
                    <p className="font-medium">
                      {new Date(patient.updatedAt).toLocaleDateString('es-MX', {
                        day: '2-digit',
                        month: '2-digit',
                        year: 'numeric'
                      })}
                    </p>
                  </div>
                )}
              </CardContent>
              <CardFooter className="flex flex-col sm:flex-row gap-2 justify-between p-6">
                <Button
                  onClick={() => handleAddPackageButtonClick(patient.id)}
                  className="w-full sm:w-auto">Agregar Paquete</Button>
                <Button
                  onClick={() => handleAddAppointment(patient.id)}
                  className="w-full sm:w-auto">Agendar Cita</Button>
              </CardFooter>
            </Card>
          </div>
          <div className="w-full md:w-2/3">
            <Card>
              <CardHeader className="bg-gray-50 rounded-t-xl">
                <CardTitle className="flex items-center gap-2 text-nlab-black">
                  Historial de Citas
                </CardTitle>
              </CardHeader>
              <CardContent className="p-6">
                <div className="text-center text-gray-500 py-8">
                  No hay citas registradas para este paciente.
                </div>
              </CardContent>
            </Card>
          </div>
          <div className="w-full md:w-2/3">
            <Card>
              <CardHeader className="bg-gray-50 rounded-t-xl">
                <CardTitle className="flex items-center gap-2 text-nlab-black">
                  Paquetes
                </CardTitle>
              </CardHeader>
              <CardContent className="p-6">
                <div className="text-center text-gray-500 py-8">
                  No existen paquetes asignados a este paciente.
                </div>
              </CardContent>
            </Card>
          </div>
        </div>
      </div>

      <Dialog open={addPackageDialogOpen} onOpenChange={setAddPackageDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Agregar Paquete a {patient.firstName}</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <p>Seleccione un paquete para asignar al paciente.</p>
            <form>
              <Select>
                <SelectTrigger className="w-[180px]">
                  <SelectValue placeholder="Selecciona un paquete" />
                </SelectTrigger>
                <SelectContent>
                  <SelectGroup>
                    <SelectLabel>Paquetes</SelectLabel>
                    {packageTypes.map((packageType) => (
                      <SelectItem value={packageType.id.toString()}>{packageType.name + ' (Citas: ' + packageType.numberOfAppointments + ')'} </SelectItem>
                    ))}
                  </SelectGroup>
                </SelectContent>
              </Select>
            </form>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAddPackageDialogOpen(false)}>
              Cancelar
            </Button>
            <Button>
              Guardar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default PatientDetailView;