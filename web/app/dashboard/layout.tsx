import SideNav from "@/app/ui/dashboard/sidenav";
import ProtectedRoute from "@/components/protected-route";

export default function Layout({children}: {children: React.ReactNode}) {
    return (
        <ProtectedRoute>
            <div className="flex h-screen flex-col md:flex-row md:overflow-hidden">
                <div className="w-full flex-none md:w-64">
                    <SideNav />
                </div>
                <div className="flex-grow p-6 md:overfloe-y-auto md:p-12">{children}</div>
            </div>
        </ProtectedRoute>
    )
}