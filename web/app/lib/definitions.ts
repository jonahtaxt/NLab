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
  id: number;
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