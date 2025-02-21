'use client';

import { Patient } from "@/app/lib/definitions";
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card";
import { Search } from 'lucide-react';
import { useState } from 'react';

const PatientTable = ({
    patients
}: {
    patients: Patient[]
}) => {
    const [searchTerm, setSearchTerm] = useState('');
  
    // Filter patients based on search term
    const filteredPatients = patients.filter(patient => {
      const searchString = searchTerm.toLowerCase();
      const fullName = `${patient.firstName} ${patient.lastName}`.toLowerCase();
      return fullName.includes(searchString) ||
             patient.email.toLowerCase().includes(searchString) ||
             patient.phone.includes(searchString);
    });
  
    return (
      <Card className="w-full">
        <CardHeader>
          <div className="flex flex-col space-y-4 md:flex-row md:items-center md:justify-between md:space-y-0">
            <CardTitle>Pacientes</CardTitle>
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
                </tr>
              </thead>
              <tbody>
                {filteredPatients.length > 0 ? (
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
                    </tr>
                  ))
                ) : (
                  <tr>
                    <td className="px-4 py-8 text-center text-gray-500">
                      No se han encontrado pacientes con tu búsqueda
                    </td>
                  </tr>
                )}
              </tbody>
            </table>
          </div>
        </CardContent>
      </Card>
    );
  };
  
export default PatientTable;