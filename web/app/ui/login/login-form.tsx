'use client';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { Input } from '@/components/ui/input';
import { Button } from '@/components/ui/button';
import useAuth from '@/app/hooks/useAuth';

const Login = () => {
    const [credentials, setCredentials] = useState({
        username: '',
        password: '',
    });
    const [error, setError] = useState('');
    const router = useRouter();
    const { login, isLoading } = useAuth();

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        setCredentials({ ...credentials, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError('');
        
        try {
            const success = await login(credentials.username, credentials.password);
            
            if (success) {
                router.push('/dashboard');
            } else {
                setError('Login failed. Please check your credentials.');
            }
        } catch (err) {
            console.error("Login error:", err);
            setError('Login failed. Please try again.');
        }
    };

    return (
        <div className="flex min-h-screen items-center justify-center">
            <form onSubmit={handleSubmit} className="p-6 border rounded-lg shadow-md w-80 space-y-4">
                <h2 className="text-xl font-semibold">Iniciar sesión</h2>
                {error && <p className="text-red-500 text-sm">{error}</p>}
                <div>
                    <Input 
                        name="username" 
                        placeholder="Usuario" 
                        onChange={handleChange} 
                        required 
                        disabled={isLoading}
                    />
                </div>
                <div>
                    <Input 
                        name="password" 
                        type="password" 
                        placeholder="Contraseña" 
                        onChange={handleChange} 
                        required 
                        disabled={isLoading}
                    />
                </div>
                <Button 
                    type="submit" 
                    className="w-full" 
                    disabled={isLoading}
                >
                    {isLoading ? 'Iniciando sesión...' : 'Iniciar sesión'}
                </Button>
            </form>
        </div>
    );
};

export default Login;