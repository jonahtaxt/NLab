'use client';

import { toast } from 'sonner';

export const showToast = {
  success: (message: string) => {
    toast.success(message, {
      className: 'bg-green-50',
      duration: 3000,
    });
  },
  
  error: (message: string) => {
    toast.error(message, {
      className: 'bg-red-50',
      duration: 4000,
    });
  },
  
  info: (message: string) => {
    toast(message, {
      duration: 3000,
    });
  }
};