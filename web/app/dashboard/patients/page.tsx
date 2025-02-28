'use client';

import { useState, useEffect } from 'react';
import { fetchAllPatients } from '@/app/lib/data.patient';
import PatientTable from '@/app/ui/patients/patients-table';
import { Patient } from '@/app/lib/definitions';

export default function Page() {
    const [patients, setPatients] = useState<Patient[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        async function loadPatients() {
            try {
                const data = await fetchAllPatients();
                setPatients(data);
                setLoading(false);
            } catch (err) {
                console.error('Failed to load patients:', err);
                setError('Failed to load patients. Please try again later.');
                setLoading(false);
            }
        }

        loadPatients();
    }, []);

    if (loading) {
        return <div className="flex justify-center items-center h-64">Loading...</div>;
    }

    if (error) {
        return <div className="text-red-500 p-4">{error}</div>;
    }

    return (
        <main>
            <PatientTable patients={patients} />
        </main>
    );
}