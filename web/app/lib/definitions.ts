export type Nutritionist = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  createdAt: string;
  updatedAt: string;
  active: boolean;
}

export type Patient = {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  createdAt: string;
  updatedAt: string;
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