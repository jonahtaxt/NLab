import { fetchPatientPackagePayments } from "@/app/lib/data.patient";
import { PatientPurchasedPackageDTO } from "@/app/lib/definitions";
import CardTable from "@/components/ui/card-table";
import { useEffect, useState } from "react";

interface PatientPackageDetailProps {
  packageId: number | undefined;
}

const patientPackageDetail = ({ packageId }: PatientPackageDetailProps) => {

  const [patientPackageDetail, setPatientPackageDetail] = useState<PatientPurchasedPackageDTO | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);

  const loadPatientPackageDetail = async () => {
    if (!packageId) return;

    try {
      setIsLoading(true);
      const response = await fetchPatientPackagePayments(packageId);
      setPatientPackageDetail(response);
      setIsLoading(false);
    } catch (err) {
      console.error("Error loading patient package detail:", err);
    }
  };

  useEffect(() => {
    loadPatientPackageDetail();
  }, [packageId]);

  const emptyPackagesState = (
    <tr>
      <td colSpan={6} className="px-4 py-8 text-center text-gray-500">
        No existen pagos para este paquete.
      </td>
    </tr>
  );

  const renderPaymentRows = () => {
    if (patientPackageDetail?.patientPayments?.length === 0) {
      return <tr><td colSpan={4}>No hay pagos registrados</td></tr>;
    }
    return patientPackageDetail?.patientPayments?.map((payment) => (
      <tr key={payment.id} className="border-b hover:bg-gray-50">
        <td className="px-4 py-3 text-sm">{payment.paymentMethodName}</td>
        <td className="px-4 py-3 text-sm">{payment.cardPaymentTypeName}</td>
        <td className="px-4 py-3 text-sm">{new Date(payment.paymentDate).toLocaleDateString()}</td>
        <td className="px-4 py-3 text-sm">{payment.totalPaid}</td>
      </tr>
    ));
  }

  return (
    <>
      <p>{patientPackageDetail?.purchasedPackage.packageType.name}</p>
      <br />
      <div>Costo: ${patientPackageDetail?.purchasedPackage.packageType.price}</div>
      <br />
      <div>
        <CardTable
          cardTitle="Pagos"
          headers={[
            'Método de pago',
            'Tipo de pago',
            'Fecha de pago',
            'Total pagado'
          ]}
          loadRows={renderPaymentRows}
          isLoading={isLoading}
          error={error}
          emptyState={emptyPackagesState}
          onRetry={() => loadPatientPackageDetail()}
        />
      </div>
    </>
  );
}
export default patientPackageDetail;