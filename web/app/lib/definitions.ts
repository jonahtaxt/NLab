export type Nutritionist = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  createdAt: Date;
  updatedAt: Date;
  active: boolean;
}

export type NutritionistDTO = {
  id: number | null;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  active: boolean;
}

export type Patient = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  createdAt: Date;
  updatedAt: Date;
  active: boolean;
}

export type PatientDTO = {
  id: number | null;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  active: boolean;
}

export type Jwt = {
  access_token: string;
  expires_in: number;
  refresh_expires_in: number;
  refresh_token: string;
  token_type: string;
  "not-before-policy": number;
  session_state: string;
  scope: string;
}

export type LoginResponseResult = {
  jwt: Jwt;
  ok: boolean;
}

export type PaymentMethod = {
  id: number;
  name: string;
  description: string;
  displayOrder: number;
}

export type CardPaymentType = {
  id: number;
  name: string;
  description: string;
  bankFeePercentage: string; // BigDecimal from Java
  numberOfInstallments: number;
  active: boolean;
}

export type PackageType = {
  id: number;
  name: string;
  description: string;
  numberOfAppointments: number;
  bundle: boolean;
  price: string;
  nutritionistRate: string;
  active: boolean;
}

export type PackageTypeDTO = {
  id: number;
  name: string;
  description: string;
  numberOfAppointments: number;
  bundle: boolean;
  price: string;
  nutritionistRate: string;
  active: boolean;
}

export type PackageTypeSelectDTO = {
  id: number;
  name: string;
  numberOfAppointments: number;
}

export type PaginatedResponse<T> = {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}

export type PurchasedPackage = {
  id: number;
  patient: Patient;
  packageType: PackageType;
  purchaseDate: string;
  paidInFull: boolean;
  remainingAppointments: number;
  expirationDate: string;
}

export type PurchasedPackageDTO = {
  id?: number;
  patientId: number;
  packageTypeId: number;
  remainingAppointments?: number;
  expirationDate?: string;
}

export type PatientPackagePaymentsDTO = {
  id: number;
  paymentMethodName: string;
  cardPaymentTypeName: string;
  paymentDate: Date;
  totalPaid: string;
}

export type PatientPurchasedPackageDTO = {
  purchasedPackage: PurchasedPackage;
  patientPayments: PatientPackagePaymentsDTO[];
}

export type PatientPaymentDTO = {
  id: number;
  purchasedPackageId: number;
  paymentMethodId: number;
  cardPaymentTypeId: number | null;
  totalPaid: string;
}

export type PatientPayment = {
  id: number;
  purchasedPackage: PurchasedPackage;
  paymentMethod: PaymentMethod;
  cardPaymentType: CardPaymentType;
  paymentDate: Date;
  totalPaid: string;
}