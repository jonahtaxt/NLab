import { authGet, authPost, authPut } from "@/app/lib/auth";
import { Appointment, AppointmentDTO, AppointmentNotes, AppointmentNotesDTO, PaginatedResponse, PatientAppointmentView } from "@/app/lib/definitions";

export async function insertAppointment(appointment: AppointmentDTO): Promise<Appointment> {
    try {
        const result = await authPost<Appointment>('/api/appointments', appointment);
        return result;
    } catch (err) {
        console.error('API error:', err);
        throw new Error('Error al insertar cita');
    }
}

export async function updateAppointment(appointmentDto: AppointmentDTO): Promise<Appointment> {
    try {
        const result = await authPut<Appointment>('/api/appointments', appointmentDto);
        return result;
    } catch (err) {
        console.error('API error:', err);
        throw new Error('Error al actualizar cita');
    }
}

export async function fetchPaginatedPatientAppointments(
    patientId: number,
    page: number = 0,
    size: number = 10,
    sortBy: string = 'appointmentDate',
    sortDirection: string = 'ASC'): Promise<PaginatedResponse<PatientAppointmentView>> {
    try {
      let url = `/api/appointments/patient/${patientId}?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`;
  
      return await authGet<PaginatedResponse<PatientAppointmentView>>(url);
    } catch (err) {
      console.error('API error:', err);
      throw new Error('Failed to fetch patient data');
    }
  }

export async function fetchAppointmentById(appointmentId: number): Promise<Appointment> {
    try {
        const result = await authGet<Appointment>(`/api/appointments/${appointmentId}`);
        return result;
    } catch (err) {
        console.error('API error:', err);
        throw new Error('Failed to fetch appointment data');
    }
}

export async function insertAppointmentNotes(appointmentNotes: AppointmentNotesDTO): Promise<AppointmentNotes> {
    try {
        const result = await authPost<AppointmentNotes>('/api/appointment-notes', appointmentNotes);
        return result;
    } catch (err) {
        console.error('API error:', err);
        throw new Error('Failed to insert appointment notes');
    }
}