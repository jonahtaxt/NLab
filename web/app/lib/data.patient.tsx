import { authGet, authPost } from "./auth";
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
      throw new Error('Failed to create patient');
    }
  }