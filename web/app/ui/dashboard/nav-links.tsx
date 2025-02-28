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

// Map of links to display in the side navigation.
// Depending on the size of the application, this would be stored in a database.
const links = [{ 
    name: 'Inicio',
    href: '/dashboard',
    icon: HomeIcon
  }, {
    name: 'Nutriólogos',
    href: '/dashboard/nutritionists',
    icon: UserIcon,
  }, { name: 'Pacientes', 
    href: '/dashboard/patients', 
    icon: UserGroupIcon 
  }, {
    name: 'Paquetes',
    href: '/dashboard/packages',
    icon: CubeIcon,
  }, {
    name: 'Configuración',
    href: '/dashboard/settings',
    icon: CogIcon,
  }, 
];

export default function NavLinks() {
  const pathname = usePathname();
  return (
    <>
      {links.map((link) => {
        const LinkIcon = link.icon;
        return (
          <Link
            key={link.name}
            href={link.href}
            className={clsx(
            'flex h-[48px] grow items-center justify-center gap-2 rounded-md bg-gray-50 p-3 text-sm font-medium hover:bg-nlab-coral hover:text-white md:flex-none md:justify-start md:p-2 md:px-3',
            {
              'bg-nlab-coral text-white': pathname == link.href
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
