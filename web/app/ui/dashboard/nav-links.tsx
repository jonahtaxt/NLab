'use client';

import {
  UserGroupIcon,
  HomeIcon,
  UserIcon,
  CubeIcon
} from '@heroicons/react/24/outline';

import Link from 'next/link';
import { usePathname } from 'next/navigation';
import clsx from 'clsx';
import { CogIcon } from 'lucide-react';
import useAuth from '@/app/hooks/useAuth';

// Map of links to display in the side navigation with role-based access.
const links = [
  { 
    name: 'Inicio',
    href: '/dashboard',
    icon: HomeIcon,
    roles: ['ADMIN', 'NUTRITIONIST', 'PATIENT'] // Everyone can see this
  }, 
  {
    name: 'Nutriólogos',
    href: '/dashboard/nutritionists',
    icon: UserIcon,
    roles: ['ADMIN'] // Only admins can manage nutritionists
  }, 
  { 
    name: 'Pacientes', 
    href: '/dashboard/patients', 
    icon: UserGroupIcon,
    roles: ['ADMIN', 'NUTRITIONIST'] // Both admins and nutritionists can see patients
  }, 
  {
    name: 'Paquetes',
    href: '/dashboard/packages',
    icon: CubeIcon,
    roles: ['ADMIN', 'NUTRITIONIST'] // Both admins and nutritionists can see packages
  }, 
  {
    name: 'Configuración',
    href: '/dashboard/settings',
    icon: CogIcon,
    roles: ['ADMIN'] // Only admins can access settings
  }
];

export default function NavLinks() {
  const pathname = usePathname();
  const { userRoles } = useAuth();
  
  // Filter links based on user roles
  const authorizedLinks = links.filter(link => {
    // If no user roles are available, don't show any restricted links
    if (!userRoles || userRoles.length === 0) {
      return false;
    }
    
    // Check if user has at least one of the required roles
    return link.roles.some(role => userRoles.includes(role));
  });

  return (
    <>
      {authorizedLinks.map((link) => {
        const LinkIcon = link.icon;
        return (
          <Link
            key={link.name}
            href={link.href}
            className={clsx(
            'flex h-[48px] grow items-center justify-center gap-2 rounded-md bg-gray-50 p-3 text-sm font-medium hover:bg-nlab-coral hover:text-white md:flex-none md:justify-start md:p-2 md:px-3',
            {
              'bg-nlab-coral text-white': pathname === link.href
            },
            )}
          >
            <LinkIcon className="w-6" />
            <p className="hidden md:block">{link.name}</p>
          </Link>
        );
      })}
    </>
  );
}