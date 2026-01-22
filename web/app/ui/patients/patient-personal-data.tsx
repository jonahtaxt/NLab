import { Patient } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";

interface PatientPersonalDataProps {
    patient: Patient;
    handleAddAppointment: () => void;
}

const PatientPersonalData = ({
    patient,
    handleAddAppointment 
}: PatientPersonalDataProps) => {
    return (
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
                    onClick={handleAddAppointment}
                    className="w-full">Agendar Cita</Button>
                </CardFooter>
              </Card>
    );
};

export default PatientPersonalData;