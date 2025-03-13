import { Patient } from "@/app/lib/definitions";
import { Card, CardHeader } from "@/components/ui/card";

interface PatientDataProps {
    patient: Patient | null
}

const PatientData = ({
    patient
}: PatientDataProps) => {

    return (
        <main>
            <Card className="w-full h-full">
                        <CardHeader className="bg-gray-50 rounded-t-xl">
                {patient?.firstName + ' ' + patient?.lastName}
                </CardHeader>
            </Card>
        </main>
    )
}

export default PatientData;