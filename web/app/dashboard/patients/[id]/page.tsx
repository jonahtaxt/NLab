'use client';

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { fetchPatientById } from '@/app/lib/data.patient';
import { Patient } from '@/app/lib/definitions';
import PatientDetailView from '@/app/ui/patients/patient-detail-view';
import { Button } from '@/components/ui/button';
import { ArrowLeft } from 'lucide-react';
import { use } from 'react';

// This is a dynamic page that displays details for a single patient
export default function PatientDetail({ params }: { params: Promise<{ id: string }> }) {
  // Unwrap the params object
  const resolvedParams = use(params);
  const [patient, setPatient] = useState<Patient | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const router = useRouter();

  useEffect(() => {
    const fetchPatient = async () => {
      setIsLoading(true);
      try {
        const patientId = parseInt(resolvedParams.id);
        if (isNaN(patientId)) {
          throw new Error('Invalid patient ID');
        }
        
        const patientData = await fetchPatientById(patientId);
        setPatient(patientData);
      } catch (err) {
        console.error('Error fetching patient:', err);
        setError('Error loading patient details. Please try again.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchPatient();
  }, [resolvedParams.id]);

  const handleBack = () => {
    router.back();
  };

  if (isLoading) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-nlab-coral"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="p-8 text-center">
        <div className="text-red-500 mb-4">{error}</div>
        <Button onClick={handleBack}>Regresar</Button>
      </div>
    );
  }

  if (!patient) {
    return (
      <div className="p-8 text-center">
        <div className="text-gray-500 mb-4">No se ha encontrado al paciente</div>
        <Button onClick={handleBack}>Regresar</Button>
      </div>
    );
  }

  return (
    <div className="w-full">
      <div className="flex items-center gap-3 mb-4">
        <button
          onClick={handleBack}
          className="w-10 h-10 rounded-full bg-white border border-gray-200 shadow-sm flex items-center justify-center hover:bg-gray-50 transition-colors"
          aria-label="Go back"
        >
          <ArrowLeft className="h-5 w-5 text-nlab-coral" />
        </button>
        <span className="text-sm font-medium text-gray-600">Regresar</span>
      </div>
      
      <PatientDetailView patient={patient} onBack={handleBack} />
    </div>
  );
}