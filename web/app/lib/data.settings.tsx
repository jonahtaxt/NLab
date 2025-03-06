import { authGet } from "@/app/lib/auth";
import { CardPaymentType, PaymentMethod } from "@/app/lib/definitions";

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