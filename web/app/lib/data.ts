import { LoginResponseResult } from "./definitions";

export async function fetchActivePatients() {
  try {
    const data = await fetch("http://localhost:8080/api/patients");
    const patients = await data.json();
    return patients;
  } catch (err) {
    console.error('API error: ', err);
    throw new Error('Failed to fetch patient data');
  }
}

export async function fetchActiveNutritionists() {
  try {
    const data = await fetch("http://localhost:8080/api/nutritionists/active");
    const patients = await data.json();
    return patients;
  } catch (err) {
    console.error('API error: ', err);
    throw new Error('Failed to fetch nutritionist data');
  }
}

export async function login(clientId: string,
  username: string,
  password: string,
  clientSecret: string,
  authUrl: string,
  realm: string
) {
  try {
    const formData = new URLSearchParams({
      client_id: clientId,
      grant_type: "password",
      username: username,
      password: password,
      client_secret: clientSecret,
    });

    const response = await fetch(`${authUrl}/realms/${realm}/protocol/openid-connect/token`, {
      method: 'POST',
      body: formData
    });

    const loginResponseResult: LoginResponseResult = {
      jwt: await response.json(),
      ok: response.ok
    };

    loginResponseResult.ok = response.ok;
    return loginResponseResult;
  } catch(err) {
    console.error('Login error: ', err);
    throw new Error('Failed to login');
  }
}