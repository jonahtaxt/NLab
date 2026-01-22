import { fetchPaginatedPatientAppointments } from "@/app/lib/data.appointment";
import { PaginatedResponse, Patient, PatientAppointmentView } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import CardTable from "@/components/ui/card-table";
import { useEffect, useState } from "react";
import { Pagination } from "../pagination";

interface PatientAppointmentsProps {
    patient: Patient;
    rescheduleAppointment: (appointmentId: number) => void;
    addAppointmentNotes: (appointmentId: number) => void;
}

const PatientAppointments = ({ patient, rescheduleAppointment, addAppointmentNotes }: PatientAppointmentsProps) => {
    const [isLoadingPatientAppointments, setIsLoadingPatientAppointments] = useState(true);
    const [errorPatientAppointments, setErrorPatientAppointments] = useState<string | null>(null);
    const [currentPatientAppointmentsPage, setCurrentPatientAppointmentsPage] = useState(0);
    const [patientAppointmentsPageSize, setPatientAppointmentsPageSize] = useState(5);
    const [patientAppointmentsData, setPatientAppointmentsData] = useState<PaginatedResponse<PatientAppointmentView>>({
        content: [],
        pageNumber: 0,
        pageSize: 5,
        totalElements: 0,
        totalPages: 0,
        first: true,
        last: true
    });

    const loadPatientAppointments = async (page = currentPatientAppointmentsPage) => {
        try {
            setIsLoadingPatientAppointments(true);
            setErrorPatientAppointments(null);
            const data = await fetchPaginatedPatientAppointments(patient.id, page, patientAppointmentsPageSize);
            setPatientAppointmentsData(data);
        } catch (err) {
            console.error("Error loading patient appointments:", err);
            setErrorPatientAppointments("Error al cargar las citas. Intente nuevamente.");
        } finally {
            setIsLoadingPatientAppointments(false);
        }
    };

    const emptyPatientAppointmentsState = (
        <tr>
            <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
                No existen citas registradas para este paciente.
            </td>
        </tr>
    );

    const renderPatientAppointmentsRows = () => {
        if (patientAppointmentsData.content.length === 0) {
            return null;
        }

        return patientAppointmentsData.content.map((appointment) => {
            // Convert the date to the client's timezone
            const appointmentDate = new Date(appointment.appointmentDate + 'T' + appointment.appointmentTime);
            const localDate = new Date(appointmentDate.getTime() - appointmentDate.getTimezoneOffset() * 60000);

            return (
                <tr key={appointment.appointmentId} className="border-b hover:bg-gray-50">
                    <td className="px-4 py-3 text-sm">
                        {appointment.nutritionistName}
                    </td>
                    <td className="px-4 py-3 text-sm">
                        {appointment.packageName}
                    </td>
                    <td className="px-4 py-3 text-sm">
                        {localDate.toLocaleDateString()}
                    </td>
                    <td className="px-4 py-3 text-sm">
                        {localDate.toLocaleTimeString()}
                    </td>
                    <td className="px-4 py-3 text-sm">
                        {appointment.status}
                    </td>
                    <td className="px-4 py-3 text-sm">
                        <div className="flex items-center justify-center space-x-2">
                            <Button variant="ghost" onClick={() => rescheduleAppointment(appointment.appointmentId)} className="h-8 w-8 p-1" title="Reagendar cita">
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
                            <Button variant="ghost" onClick={() => addAppointmentNotes(appointment.appointmentId)} className="h-8 w-8 p-1" title="Agregar notas">
                                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                                    <path d="M16 4h2a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h2" />
                                    <path d="M15 2H9a1 1 0 0 0-1 1v2a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V3a1 1 0 0 0-1-1Z" />
                                    <path d="M12 11v5" />
                                    <path d="M9 13h6" />
                                </svg>
                            </Button>
                        </div>
                    </td>
                </tr>
            );
        });
    };

    useEffect(() => {
        loadPatientAppointments(currentPatientAppointmentsPage);
    }, [patient.id, currentPatientAppointmentsPage, patientAppointmentsPageSize]);

    return <>
        <CardTable
            cardTitle="Citas"
            headers={['Nutriólogo', 'Paquete', 'Fecha', 'Hora', 'Estado', 'Acciones']}
            loadRows={renderPatientAppointmentsRows}
            isLoading={isLoadingPatientAppointments}
            error={errorPatientAppointments}
            emptyState={emptyPatientAppointmentsState}
            onRetry={() => loadPatientAppointments(0)}
        />

        {patientAppointmentsData.totalElements > 0 && (
            <div className="mt-4 mb-6">
            <Pagination
                currentPage={patientAppointmentsData.pageNumber}
                pageSize={patientAppointmentsPageSize}
                totalPages={patientAppointmentsData.totalPages}
                totalElements={patientAppointmentsData.totalElements}
                onPageChange={setCurrentPatientAppointmentsPage}
                onPageSizeChange={setPatientAppointmentsPageSize}
                isFirstPage={patientAppointmentsData.first}
                isLastPage={patientAppointmentsData.last}
                pageSizeOptions={[5, 10, 20]}
            />
            </div>
        )}
    </>
};

export default PatientAppointments;