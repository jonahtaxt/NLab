'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import useAuth from '@/app/hooks/useAuth';

interface ProtectedRouteProps {
  children: React.ReactNode;
}

/**
 * A wrapper component to protect routes that require authentication
 */
export default function ProtectedRoute({ children }: ProtectedRouteProps) {
  const { isAuthenticated, isLoading } = useAuth();
  const router = useRouter();

  useEffect(() => {
    if (!isLoading && !isAuthenticated) {
      // Redirect to login page if not authenticated
      router.push('/login');
    }
  }, [isAuthenticated, isLoading, router]);

  if (isLoading) {
    // You could replace this with a loading spinner component
    return (
      <div className="flex min-h-screen items-center justify-center">
        <p className="text-lg">Loading...</p>
      </div>
    );
  }

  // Only render children if authenticated
  return isAuthenticated ? <>{children}</> : null;
}