import '@/ui/global.css';
import {inter} from '@/app/ui/fonts';
import { Toaster } from 'sonner';
import { ToastProvider } from '@/components/toaster';

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="en">
      <body className={`${inter.className} antialiased h-screen overflow-hidden`}>
        {children}
        <ToastProvider />
      </body>
    </html>
  );
}
