import { authGet } from "./auth";
import { PaymentMethod } from "./definitions";

export async function fetchAllPaymentMethods(): Promise<PaymentMethod[]> {
    try {
          return await authGet<PaymentMethod[]>('/api/payment-methods');
        } catch (err) {
          console.error('API error:', err);
          throw new Error('Error al obtener métodos de pago');
        }
}