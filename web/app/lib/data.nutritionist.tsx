import { Nutritionist, NutritionistDTO, PaginatedResponse } from '@/app/lib/definitions';
import { authDelete, authGet, authPost, authPut } from '@/app/lib/auth';

export async function fetchPaginatedNutritionists(
  page: number = 0,
  size: number = 10,
  sortBy: string = 'lastName',
  sortDirection: string = 'ASC',
  searchTerm?: string,
  active?: boolean
): Promise<PaginatedResponse<Nutritionist>> {
  try {
    let url = `/api/nutritionists?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`;

    if (searchTerm) {
      url += `&searchTerm=${encodeURIComponent(searchTerm)}`;
    }

    if (active !== undefined) {
      url += `&active=${active}`;
    }

    return await authGet<PaginatedResponse<Nutritionist>>(url);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Failed to fetch patient data');
  }
}

export async function insertNutritionist(nutritionist: NutritionistDTO): Promise<Nutritionist> {
  try {
    const result = await authPost<Nutritionist>('/api/nutritionists', nutritionist);
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

    throw new Error('Error al insertar nutriólogo');
  }
}

export async function updateNutritionist(nutritionist: NutritionistDTO): Promise<Nutritionist> {
  try {
    const result = await authPut<Nutritionist>(`/api/nutritionists/${nutritionist.id}`, nutritionist);
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

    throw new Error('Error al actualizar nutriólogo');
  }
}

export async function fetchActiveNutritionists(): Promise<Nutritionist[]> {
  try {
    return await authGet<Nutritionist[]>('/api/nutritionists/active');
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al obtener nutriólogos activos');
  }
}