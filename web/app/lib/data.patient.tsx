import { authDelete, authGet, authPost, authPut } from "@/app/lib/auth";
import { PaginatedResponse, Patient, PatientDTO } from "@/app/lib/definitions";

export async function fetchPaginatedPatients(
  page: number = 0,
  size: number = 10,
  sortBy: string = 'lastName',
  sortDirection: string = 'ASC',
  searchTerm?: string,
  active?: boolean
): Promise<PaginatedResponse<Patient>> {
  try {
      let url = `/api/patients?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`;
      
      if (searchTerm) {
          url += `&searchTerm=${encodeURIComponent(searchTerm)}`;
      }
      
      if (active !== undefined) {
          url += `&active=${active}`;
      }
      
      return await authGet<PaginatedResponse<Patient>>(url);
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