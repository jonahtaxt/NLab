import { authGet, authPost, authPut } from "@/app/lib/auth";
import { PackageType, PackageTypeDTO, PaginatedResponse } from "@/app/lib/definitions";

export async function fetchPaginatedPackageTypes(
  page: number = 0,
  size: number = 10,
  sortBy: string = 'name',
  sortDirection: string = 'ASC',
  searchTerm?: string,
  active?: boolean
): Promise<PaginatedResponse<PackageType>> {
  try {
    let url = `/api/package-types?page=${page}&size=${size}&sortBy=${sortBy}&sortDirection=${sortDirection}`;

    if (searchTerm) {
      url += `&searchTerm=${encodeURIComponent(searchTerm)}`;
    }

    if (active !== undefined) {
      url += `&active=${active}`;
    }

    return await authGet<PaginatedResponse<PackageType>>(url);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Failed to fetch package data');
  }
}

export async function fetchAllPackageTypes(): Promise<PackageType[]> {
  try {
    return await authGet<PackageType[]>('/api/package-types');
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al obtener Tipos de Paquete');
  }
}

export async function insertPackageType(packageType: PackageTypeDTO): Promise<PackageTypeDTO> {
  try {
    return await authPost<PackageTypeDTO>('/api/package-types', packageType);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al insertar paquete');
  }
}

export async function updatePackageType(packageType: PackageTypeDTO): Promise<PackageTypeDTO> {
  try {
    return await authPut<PackageTypeDTO>('/api/package-types/' + packageType.id, packageType);
  } catch (err) {
    console.error('API error:', err);
    throw new Error('Error al actualizar paquete');
  }
}