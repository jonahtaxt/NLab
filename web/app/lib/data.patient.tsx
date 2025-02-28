import { authDelete, authGet, authPost, authPut } from "./auth";
import { Patient, PatientDTO } from "./definitions";

export async function fetchAllPatients(): Promise<Patient[]> {
    try {
      return await authGet<Patient[]>('/api/patients');
    } catch (err) {
      console.error('API error:', err);
      throw new Error('Failed to fetch patient data');
    }
  }

export async function insertPatient(patient: PatientDTO): Promise<Patient> {
    try {
      return await authPost<Patient>('/api/patients', patient);
    } catch (err) {
      console.error('API error:', err);
      throw new Error('Error al insertar paciente');
    }
  }

export async function updatePatient(patient: PatientDTO): Promise<Patient> {
    try {
      return await authPut<Patient>('/api/patients/' + patient.id, patient);
    } catch (err) {
      console.error('API error:', err);
      throw new Error('Error al actualizar paciente');
    }
  }

export async function deletePatient(id: number): Promise<void> {
  try {
    return await authDelete('/api/patients/' + id);
  }
  catch (err) {
    console.error('API error:', err);
    throw new Error('Error al eliminar paciente');
  }
}