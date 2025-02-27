import { login as authLogin } from './auth';

export async function login(
  clientId: string,
  username: string,
  password: string,
  clientSecret: string,
  authUrl: string,
  realm: string
) {
  return authLogin(username, password, clientId, clientSecret, authUrl, realm);
}