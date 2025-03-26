import { authGet, authPost, authPut } from "@/app/lib/auth";
import { PaginatedResponse, PurchasedPackage, PurchasedPackageDTO } from "@/app/lib/definitions";

export async function fetchPatientPackages(
  patientId: number,
  page: number = 0,
  size: number = 5,
  sortBy: string = 'purchaseDate',
  sortDirection: string = 'DESC'
): Promise<PaginatedResponse<PurchasedPackage>> {
  try {
    const url = `/api/purchased-packages/patient/${patientId}?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`;
    return await authGet<PaginatedResponse<PurchasedPackage>>(url);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Failed to fetch patient packages');
  }
}

export async function createPurchasedPackage(packageData: PurchasedPackageDTO): Promise<PurchasedPackage> {
  try {
    return await authPost<PurchasedPackage>('/api/purchased-packages', packageData);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al crear el paquete');
  }
}

export async function getPurchasedPackageById(id: number): Promise<PurchasedPackage> {
  try {
    return await authGet<PurchasedPackage>(`/api/purchased-packages/${id}`);
  } catch (err) {
    console.error('API error:', err);
    throw new Error(`Error al obtener el paquete con ID ${id}`);
  }
}

export async function updatePurchasedPackage(id: number, packageData: PurchasedPackageDTO): Promise<PurchasedPackage> {
  try {
    return await authPut<PurchasedPackage>(`/api/purchased-packages/${id}`, packageData);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al actualizar el paquete');
  }
}

export async function isPackageValid(id: number): Promise<boolean> {
  try {
    return await authGet<boolean>(`/api/purchased-packages/${id}/valid`);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al verificar validez del paquete');
  }
}