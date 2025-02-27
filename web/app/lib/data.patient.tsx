import { authGet } from "./auth";
import { Patient } from "./definitions";

export async function fetchActivePatients(): Promise<Patient[]> {
    try {
      return await authGet<Patient[]>('/patients/active');
    } catch (err) {
      console.error('API error:', err);
      throw new Error('Failed to fetch patient data');
    }
  }

export async function fetchAllPatients(): Promise<Patient[]> {
    try {
      return await authGet<Patient[]>('/api/patients');
    } catch (err) {
      console.error('API error:', err);
      throw new Error('Failed to fetch patient data');
    }
  }