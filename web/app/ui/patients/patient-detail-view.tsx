'use client';

import { PackageTypeSelectDTO, Patient, PaginatedResponse, PurchasedPackage, PurchasedPackageDTO } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardFooter, CardHeader, CardTitle } from "@/components/ui/card";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { useEffect, useState } from "react";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { fetchSelectPackageTypes } from "@/app/lib/data.package-type";
import { fetchPatientPackages, createPurchasedPackage } from "@/app/lib/data.purchased-package";
import { Pagination } from "@/app/ui/pagination";
import CardTable from "@/components/ui/card-table";
import { Loader2, RefreshCcw } from "lucide-react";
import { showToast } from "@/lib/toaster-util";

interface PatientDetailViewProps {
  patient: Patient;
  onBack?: () => void; // Made optional as we'll use router navigation in the page component
}

const PatientDetailView = ({ patient, onBack }: PatientDetailViewProps) => {
  if (!patient) return null;

  // Dialog state
  const [addPackageDialogOpen, setAddPackageDialogOpen] = useState(false);
  const [packageTypes, setPackageTypes] = useState<PackageTypeSelectDTO[]>([]);
  const [selectedPackageType, setSelectedPackageType] = useState<string>("");

  // Packages table state
  const [currentPage, setCurrentPage] = useState(0);
  const [pageSize, setPageSize] = useState(5);
  const [packageData, setPackageData] = useState<PaginatedResponse<PurchasedPackage>>({
    content: [],
    pageNumber: 0,
    pageSize: 5,
    totalElements: 0,
    totalPages: 0,
    first: true,
    last: true
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  // Function to load packages data (moved outside useEffect for reusability)
  const loadPackages = async (page = currentPage) => {
    try {
      setIsLoading(true);
      setError(null);
      const data = await fetchPatientPackages(patient.id, page, pageSize);
      setPackageData(data);
    } catch (err) {
      console.error("Error loading packages:", err);
      setError("Error al cargar los paquetes. Intente nuevamente.");
    } finally {
      setIsLoading(false);
    }
  };

  // Load packages data when component mounts or dependencies change
  useEffect(() => {
    loadPackages();
  }, [patient.id, currentPage, pageSize]);

  // Handle pagination changes
  const handlePageChange = (page: number) => {
    setCurrentPage(page);
  };

  const handlePageSizeChange = (size: number) => {
    setPageSize(size);
    setCurrentPage(0); // Reset to first page when changing page size
  };

  // Handle add package dialog
  const handleAddPackageButtonClick = async () => {
    try {
      setAddPackageDialogOpen(true);
      const types = await fetchSelectPackageTypes();
      setPackageTypes(types);
    } catch (error) {
      console.error("Error fetching package types:", error);
    }
  };

  const handleAddAppointment = () => {
    alert("Función de agendar cita no implementada");
  };

  // Function to render package table rows
  const renderPackageRows = () => {
    if (packageData.content.length === 0) {
      return null;
    }

    return packageData.content.map((pkg) => (
      <tr key={pkg.id} className="border-b hover:bg-gray-50">
        <td className="px-4 py-3 text-sm">
          {pkg.packageType.name}
        </td>
        <td className="px-4 py-3 text-sm text-center">
          {new Date(pkg.purchaseDate).toLocaleDateString('es-MX')}
        </td>
        <td className="px-4 py-3 text-sm text-center font-medium">
          {pkg.remainingAppointments}
        </td>
        <td className="px-4 py-3 text-sm text-center">
          {new Date(pkg.expirationDate).toLocaleDateString('es-MX')}
        </td>
        <td className="px-4 py-3 text-sm">
          <span className={`inline-flex items-center rounded-full px-2 py-1 text-xs font-medium ${pkg.paidInFull
            ? 'bg-green-100 text-green-700'
            : 'bg-yellow-100 text-yellow-700'
            }`}>
            {pkg.paidInFull ? 'Pagado' : 'Pendiente'}
          </span>
        </td>
        <td className="px-4 py-3 text-sm text-center">
          <Button variant="ghost" onClick={() => handleViewPackage(pkg.id)} className="text-sm">
            Ver
          </Button>
        </td>
      </tr>
    ));
  };

  const handleViewPackage = (packageId: number) => {
    // Implement view package details
    console.log("View package:", packageId);
  };

  // Empty state for packages table
  const emptyPackagesState = (
    <tr>
      <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
        No existen paquetes asignados a este paciente.
      </td>
    </tr>
  );

  // Handle form submission
  const handleAddPackage = async () => {
    if (!selectedPackageType) {
      alert("Por favor seleccione un paquete");
      return;
    }

    try {
      const saveBtn = document.querySelector('[data-save-package]') as HTMLButtonElement;
      if (saveBtn) saveBtn.disabled = true;

      // Create package DTO
      const packageDTO: PurchasedPackageDTO = {
        patientId: patient.id,
        packageTypeId: parseInt(selectedPackageType)
      };

      // Call API to create package
      await createPurchasedPackage(packageDTO);

      // Close dialog
      setAddPackageDialogOpen(false);
      setSelectedPackageType("");

      // Reload packages data with reset to first page
      setCurrentPage(0);
      await loadPackages(0);

      // Display success message
      showToast.success("Paquete agregado exitosamente");
    } catch (error) {
      console.error("Error adding package:", error);
      showToast.error("Error al agregar el paquete");
    } finally {
      const saveBtn = document.querySelector('[data-save-package]') as HTMLButtonElement;
      if (saveBtn) saveBtn.disabled = false;
    }
  };

  return (
    <>
      <div className="w-full h-full">
        <div className="flex flex-col gap-4">
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="md:col-span-1">
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
                    onClick={handleAddPackageButtonClick}
                    className="w-full sm:w-auto">Agregar Paquete</Button>
                  <Button
                    onClick={handleAddAppointment}
                    className="w-full sm:w-auto">Agendar Cita</Button>
                </CardFooter>
              </Card>
            </div>

            <div className="md:col-span-3">
              <CardTable
                cardTitle="Paquetes"
                headers={[
                  'Nombre del Paquete',
                  'Fecha de Compra',
                  'Citas Restantes',
                  'Fecha de Expiración',
                  'Estado de Pago',
                  'Acciones'
                ]}
                loadRows={renderPackageRows}
                isLoading={isLoading}
                error={error}
                emptyState={emptyPackagesState}
                onRetry={() => loadPackages(0)}
                customCardHeader={
                  <Button
                    variant="ghost"
                    size="sm"
                    className="h-8 w-8 p-0"
                    onClick={() => loadPackages(currentPage)}
                    disabled={isLoading}
                  >
                    <RefreshCcw className="h-4 w-4" />
                    <span className="sr-only">Refrescar</span>
                  </Button>
                }
              />

              {packageData.totalElements > 0 && (
                <div className="mt-4">
                  <Pagination
                    currentPage={packageData.pageNumber}
                    pageSize={packageData.pageSize}
                    totalPages={packageData.totalPages}
                    totalElements={packageData.totalElements}
                    onPageChange={handlePageChange}
                    onPageSizeChange={handlePageSizeChange}
                    isFirstPage={packageData.first}
                    isLastPage={packageData.last}
                    pageSizeOptions={[5, 10, 20]}
                  />
                </div>
              )}
            </div>
          </div>
          <div className="grid grid-cols-1 gap-4">
            <div className="col-span-full">
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
          </div>
        </div>
      </div>

      <Dialog open={addPackageDialogOpen} onOpenChange={setAddPackageDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Agregar Paquete a {patient.firstName}</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <p className="mb-4">Seleccione un paquete para asignar al paciente:</p>
            <Select value={selectedPackageType} onValueChange={setSelectedPackageType}>
              <SelectTrigger className="w-full">
                <SelectValue placeholder="Selecciona un paquete" />
              </SelectTrigger>
              <SelectContent>
                <SelectGroup>
                  <SelectLabel>Paquetes disponibles</SelectLabel>
                  {packageTypes.map((packageType) => (
                    <SelectItem key={packageType.id} value={packageType.id.toString()}>
                      {packageType.name} (Citas: {packageType.numberOfAppointments})
                    </SelectItem>
                  ))}
                </SelectGroup>
              </SelectContent>
            </Select>
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setAddPackageDialogOpen(false)}>
              Cancelar
            </Button>
            <Button onClick={handleAddPackage} data-save-package>
              Guardar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default PatientDetailView;