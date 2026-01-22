'use client';

import { Toaster } from 'sonner';

export function ToastProvider() {
  return (
    <Toaster 
      position="bottom-center"
      offset="20px" // Distance from the bottom of the screen
      expand={false} // Whether to expand toasts on hover
      toastOptions={{
        style: {
          background: 'white',
          color: 'black',
          border: '1px solid #e2e8f0',
        },
        className: 'border-border',
        duration: 4000, // Default duration for all toasts
      }}
    />
  );
}