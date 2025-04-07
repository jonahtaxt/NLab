import { authDelete, authGet, authPost, authPut } from "@/app/lib/auth";
import { PaginatedResponse, Patient, PatientDTO, PatientPurchasedPackageDTO } from "@/app/lib/definitions";

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

export async function fetchPatientById(id: number): Promise<Patient> {
  try {
    return await authGet<Patient>(`/api/patients/${id}`);
  } catch (err) {
    console.error('API error:', err);
    throw new Error(`Error al obtener el paciente con ID ${id}`);
  }
}

export async function insertPatient(patient: PatientDTO): Promise<Patient> {
  try {
    const result = await authPost<Patient>('/api/patients', patient);
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

    throw new Error('Error al insertar paciente');
  }
}

export async function updatePatient(patient: PatientDTO): Promise<Patient> {
  try {
    const result = await authPut<Patient>(`/api/patients/${patient.id}`, patient);
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

    throw new Error('Error al actualizar paciente');
  }
}

export async function deletePatient(id: number): Promise<void> {
  try {
    await authDelete(`/api/patients/${id}`);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al eliminar paciente');
  }
}

export async function fetchActivePatients(): Promise<Patient[]> {
  try {
    return await authGet<Patient[]>('/api/patients/active');
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al obtener pacientes activos');
  }
}

export async function fetchPatientPackagePayments(packageId: number): Promise<PatientPurchasedPackageDTO> {
  try {
    return await authGet<PatientPurchasedPackageDTO>(`/api/purchased-packages/${packageId}`);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al obtener pagos del paquete');
  }
}