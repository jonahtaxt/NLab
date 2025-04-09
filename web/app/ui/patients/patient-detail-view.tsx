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
import PatientPaymentForm from "@/app/ui/patients/patient-payment-form";
import PatientPackageDetail from "@/app/ui/patients/patient-package-detail";

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
  const [addPatientPaymentDialogOpen, setAddPatientPaymentDialogOpen] = useState(false);
  const [selectedPurchasedPackage, setSelectedPurchasedPackage] = useState<PurchasedPackage>();
  const [patientPackageDetailDialogOpen, setPatientPackageDetailDialogOpen] = useState(false);
  const [purchasedPackageId, setPurchasedPackageId] = useState<number | undefined>(undefined);

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
          <div className="flex items-center justify-center space-x-2">
            <Button variant="ghost" onClick={() => handleViewPackage(pkg.id)} className="h-8 w-8 p-1" title="Ver pagos">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z" />
                <circle cx="12" cy="12" r="3" />
              </svg>
            </Button>
            <Button variant="ghost" onClick={() => handlePayment(pkg)} className="h-8 w-8 p-1" title="Registrar pago">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <circle cx="12" cy="12" r="10" />
                <path d="M16 8h-6a2 2 0 1 0 0 4h4a2 2 0 1 1 0 4H8" />
                <path d="M12 18V6" />
              </svg>
            </Button>
            <Button variant="ghost" onClick={() => handleBookAppointment(pkg.id)} className="h-8 w-8 p-1" title="Agendar cita">
              <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                <rect width="18" height="18" x="3" y="4" rx="2" ry="2" />
                <line x1="16" x2="16" y1="2" y2="6" />
                <line x1="8" x2="8" y1="2" y2="6" />
                <line x1="3" x2="21" y1="10" y2="10" />
                <path d="M8 14h.01" />
                <path d="M12 14h.01" />
                <path d="M16 14h.01" />
                <path d="M8 18h.01" />
                <path d="M12 18h.01" />
                <path d="M16 18h.01" />
              </svg>
            </Button>
          </div>
        </td>
      </tr>
    ));
  };

  const handleViewPackage = (packageId: number) => {
    setPurchasedPackageId(packageId);
    setPatientPackageDetailDialogOpen(true);
  };

  const handlePayment = (purchasedPackage: PurchasedPackage) => {
    setAddPatientPaymentDialogOpen(true);
    setSelectedPurchasedPackage(purchasedPackage);
  };

  const handleBookAppointment = (packageId: number) => {
    // Implement appointment booking
    console.log("Book appointment for package:", packageId);
    alert("Función de agenda de cita no implementada");
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
                    onClick={handleAddAppointment}
                    className="w-full">Agendar Cita</Button>
                </CardFooter>
              </Card>
            </div>

            <div className="md:col-span-3">
              {/* Wrap both the CardTable and Pagination in a Container */}
              <div className="flex flex-col h-full">
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
                    <div className="flex items-center gap-2">
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
                      <Button
                        size="sm"
                        onClick={handleAddPackageButtonClick}
                        className="ml-2"
                      >
                        Agregar Paquete
                      </Button>
                    </div>
                  }
                />

                {/* Pagination is now contained within the same column as the CardTable */}
                {packageData.totalElements > 0 && (
                  <div className="mt-4 mb-6">
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
          </div>

          {/* This was moved down to avoid overlapping with the pagination */}
          <div className="grid grid-cols-1 gap-4 mt-4">
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

      <Dialog open={addPatientPaymentDialogOpen} onOpenChange={setAddPatientPaymentDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Registrar pago</DialogTitle>
          </DialogHeader>
          <PatientPaymentForm
            purchasedPackage={selectedPurchasedPackage}
            closeDialog={() => setAddPatientPaymentDialogOpen(false)}
            savePayment={() => setAddPatientPaymentDialogOpen(false)}
            onPaymentSuccess={() => {
              loadPackages(currentPage);
            }} />
        </DialogContent>
      </Dialog>

      <Dialog open={patientPackageDetailDialogOpen} onOpenChange={setPatientPackageDetailDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Detalles del Paquete</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <PatientPackageDetail packageId={purchasedPackageId} />
          </div>
          <DialogFooter>
            <Button variant="outline" onClick={() => setPatientPackageDetailDialogOpen(false)}>
              Cerrar
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default PatientDetailView;