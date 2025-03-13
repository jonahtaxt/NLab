'use client';

import { Patient } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogFooter, DialogHeader } from "@/components/ui/dialog";
import { DialogContent, DialogTitle } from "@radix-ui/react-dialog";
import { ArrowLeft } from "lucide-react";
import { useState } from "react";

interface PatientDetailViewProps {
  patient: Patient;
  onBack: () => void;
}

const PatientDetailView = ({ patient, onBack }: PatientDetailViewProps) => {
  if (!patient) return null;

  const [addPackageDialogOpen, setAddPackageDialogOpen] = useState(false);

  const handleAddPackageButtonClick = (patientId: number) => {
    setAddPackageDialogOpen(true);
    alert(patientId);
  }

  return (
    <>
      <div className="w-full h-full relative">
        {/* Floating circular back button */}
        <button
          onClick={onBack}
          className="absolute -left-4 top-1 z-10 flex items-center justify-center w-12 h-12 rounded-full bg-white shadow-md hover:bg-gray-50 transition-colors"
          aria-label="Volver"
        >
          <ArrowLeft className="h-6 w-6 text-nlab-coral" />
        </button>
        <div className="flex flex-col md:flex-row gap-4">
          <div className="w-full md:w-1/4">
            <Card>
              <CardHeader className="bg-gray-50 rounded-t-xl">
                <CardTitle className="flex items-center gap-2 text-nlab ml-6">
                  Información Personal
                </CardTitle>
              </CardHeader>
              <CardContent>
                <div>
                  <p className="text-sm text-gray-500">Nombre Completo</p>
                  <p className="font-medium">{patient.firstName} {patient.lastName}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Correo Electrónico</p>
                  <p className="font-medium">{patient.email}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Teléfono</p>
                  <p className="font-medium">{patient.phone}</p>
                </div>
                <div>
                  <p className="text-sm text-gray-500">Estado</p>
                  <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${patient.active ? 'bg-green-100 text-green-700' : 'bg-gray-100 text-gray-700'
                    }`}>
                    {patient.active ? 'Activo' : 'Inactivo'}
                  </span>
                </div>
                <div>
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
                  <div>
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
              <CardFooter className="flex justify-between">
                <Button onClick={() => handleAddPackageButtonClick(patient.id)}>Agregar Paquete</Button>
                <Button>Agendar Cita</Button>
              </CardFooter>
            </Card>
          </div>
          <div className="w-full md:w-3/4">
            {/* Second card component goes here */}
            <Card>
              <CardHeader className="bg-gray-50 rounded-t-xl">
                <CardTitle className="flex items-center gap-2 text-nlab ml-6">
                  Citas
                </CardTitle>
              </CardHeader>
              <CardContent>
                Second card content
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
          Agreguele
          <DialogFooter></DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default PatientDetailView;