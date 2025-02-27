'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import { login } from '@/app/lib/data';

const Login = () => {
    const [credentials, setCredentials] = useState({
        username: '', 
        password: '',
        authUrl: process.env.NEXT_PUBLIC_KEYCLOAK_URL,
        clientId: process.env.NEXT_PUBLIC_CLIENT_ID,
        clientSecret: process.env.NEXT_PUBLIC_CLIENT_SECRET,
        realm: process.env.NEXT_PUBLIC_REALM });
    const [error, setError] = useState('');
    const router = useRouter();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        
        try {
            if (!process.env.NEXT_PUBLIC_CLIENT_ID || 
                !process.env.NEXT_PUBLIC_CLIENT_SECRET || 
                !process.env.NEXT_PUBLIC_KEYCLOAK_URL || 
                !process.env.NEXT_PUBLIC_REALM) {
                throw new Error("Missing environment variables");
            }
            
            const data = await login(process.env.NEXT_PUBLIC_CLIENT_ID,
                credentials.username,
                credentials.password,
                process.env.NEXT_PUBLIC_CLIENT_SECRET,
                process.env.NEXT_PUBLIC_KEYCLOAK_URL,
                process.env.NEXT_PUBLIC_REALM
            );

            if (!data.ok) {
            throw new Error("Login failed");
            }
            localStorage.setItem('access_token', data.jwt.access_token);
            localStorage.setItem('refresh_token', data.jwt.refresh_token);
            router.push('/dashboard');
        } catch (err) {
            console.error("Login error:", err);
            setError('No se puede iniciar sesión. Por favor, verifique sus credenciales.');
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center">
            <form onSubmit={handleSubmit} className="p-6 border rounded-lg shadow-md w-80 space-y-4">
                <h2 className="text-xl font-semibold">Iniciar sesión</h2>
                {error && <p className="text-red-500 text-sm">{error}</p>}
                <div>
                    <Input name="username" placeholder="Usuario" onChange={handleChange} required />
                </div>
                <div>
                    <Input name="password" type="password" placeholder="Contraseña" onChange={handleChange} required />
                </div>
                <Button type="submit" className="w-full">Iniciar sesión</Button>
            </form>
        </div>
    );
};

export default Login;