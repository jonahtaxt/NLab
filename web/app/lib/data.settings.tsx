import { authGet, authPost, authPut } from "./auth";
import { CardPaymentType, PackageType, PackageTypeDTO, PaymentMethod } from "./definitions";

export async function fetchAllPaymentMethods(): Promise<PaymentMethod[]> {
    try {
          return await authGet<PaymentMethod[]>('/api/payment-methods');
        } catch (err) {
          console.error('API error:', err);
          throw new Error('Error al obtener métodos de pago');
        }
}

export async function fetchAllCardPaymentTypes(): Promise<CardPaymentType[]> {
    try {
          return await authGet<CardPaymentType[]>('/api/card-payment-types');
        } catch (err) {
          console.error('API error:', err);
          throw new Error('Error al obtener Tipos de Pago con Tarjeta');
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
      throw new Error('Error al actualizar paciente');
    }
  }