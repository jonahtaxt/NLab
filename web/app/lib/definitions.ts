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

export type PaginatedResponse<T> = {
  content: T[];
  pageNumber: number;
  pageSize: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}