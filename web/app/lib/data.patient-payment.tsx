import { PatientPayment, PatientPaymentDTO } from "@/app/lib/definitions";
import { authPost } from "./auth";

export async function insertPatientPayment(patientPayment: PatientPaymentDTO): Promise<PatientPayment> {
    try {
        const result = await authPost<PatientPayment>('/api/patient-payment', patientPayment);
        return result;
    } catch (err) {
        console.error('API error:', err);

        // Check if the error has a specific message from the API
        if (err instanceof Error) {
            // Try to parse the error to see if it contains a JSON response
            try {
                const errorObj = JSON.parse(err.message);
                if (errorObj.message) {
                    throw new Error(errorObj.message);
                }
            } catch {
                // If we can't parse the error, just use the original message
            }
        }
        throw new Error('Error al insertar pago del paciente');
    }
}