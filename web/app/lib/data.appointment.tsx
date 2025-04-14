import { authPost } from "@/app/lib/auth";
import { Appointment, AppointmentDTO } from "@/app/lib/definitions";

export async function insertAppointment(appointment: AppointmentDTO): Promise<Appointment> {
    try {
        const result = await authPost<Appointment>('/api/appointments', appointment);
        return result;
    } catch (err) {
        console.error('API error:', err);
        throw new Error('Error al insertar cita');
    }
}
