import { fetchActivePatients } from '@/app/lib/data';
import PatientTable from '@/app/ui/patients/active-patients';

export default async function Page() {
    const patients = await fetchActivePatients();
    return (
        <main>
            <PatientTable patients={patients} />
        </main>
    )
}