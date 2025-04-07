import { insertPatientPayment } from "@/app/lib/data.patient-payment";
import { fetchAllCardPaymentTypes, fetchAllPaymentMethods } from "@/app/lib/data.settings";
import { CardPaymentType, Patient, PaymentMethod, PurchasedPackage } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { DialogFooter } from "@/components/ui/dialog";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { useEffect, useState } from "react";
import { set, useForm } from "react-hook-form";
import { z } from "zod";

interface PatientPaymentFormProps {
    purchasedPackage: PurchasedPackage | undefined;
    closeDialog: () => void;
    savePayment: () => void;
}

const PatientPaymentForm = ({
    purchasedPackage,
    closeDialog,
    savePayment
}: PatientPaymentFormProps) => {
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
    const [cardPaymentTypes, setCardPaymentTypes] = useState<CardPaymentType[]>([]);
    const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<string>('');
    const [selectedCardPaymentType, setSelectedCardPaymentType] = useState<string>('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [serverError, setServerError] = useState<string | null>(null);

    const loadPaymentMethodData = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const [pMethods, cTypes] = await Promise.all([
                fetchAllPaymentMethods(),
                fetchAllCardPaymentTypes()
            ]);
            setCardPaymentTypes(cTypes);
            setPaymentMethods(pMethods);
        } catch (err) {
            console.error("Error loading payment methods:", err);
            setError("Error al cargar los métodos de pago. Intente nuevamente.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadPaymentMethodData();
    }, []);

    const formSchema = z.object({
        totalPaid: z.string().refine(tp => tp.toString().split('.')),
        paymentMethod: z.string().min(1, { message: 'El método de pago es requerido' }),
        cardPaymentType: z.string()
    });

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            totalPaid: '0.00'
        }
    });

    const handleAddPayment = async () => {
        setServerError(null);
        try{
            if (!purchasedPackage?.id) {
                throw new Error('No se encontró el paquete para procesar el pago');
            }
            const patientPaymentDTO = {
                id: 0,
                purchasedPackageId: purchasedPackage.id,
                paymentMethodId: parseInt(selectedPaymentMethod),
                cardPaymentTypeId: selectedCardPaymentType ? parseInt(selectedCardPaymentType) : null,
                totalPaid: form.getValues('totalPaid')
            };
            setIsSubmitting(true);
            await insertPatientPayment(patientPaymentDTO);
            savePayment();
            closeDialog();
        } catch (error) {
            console.error('Error saving payment:', error);
            if (error instanceof Error) {
                setServerError(error.message || 'Error al guardar el pago');
            } else {
                setServerError('Error al guardar el pago');
            }
        }
    };

    return (
        <>
            <Form {...form}>
                <form onSubmit={form.handleSubmit(handleAddPayment)} className="space-y-4">
                    {serverError && (
                        <div className="p-3 bg-red-100 text-red-700 rounded-md text-sm">
                            {serverError}
                        </div>
                    )}
                    <FormField
                        control={form.control}
                        name="paymentMethod"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Método de pago</FormLabel>
                                <FormControl>
                                    <Select value={selectedPaymentMethod} onValueChange={setSelectedPaymentMethod}>
                                        <SelectTrigger className="w-full">
                                            <SelectValue placeholder="Selecciona un medio de pago" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Medios de pago</SelectLabel>
                                                {paymentMethods.map((paymentMethod) => (
                                                    <SelectItem key={paymentMethod.id} value={paymentMethod.id.toString()}>
                                                        {paymentMethod.name}
                                                    </SelectItem>
                                                ))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )} />
                    <FormField
                        control={form.control}
                        name="cardPaymentType"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Pagos Diferidos</FormLabel>
                                <FormControl>
                                    <Select disabled={(selectedPaymentMethod !== '3')} value={selectedCardPaymentType} onValueChange={setSelectedCardPaymentType}>
                                        <SelectTrigger className="w-full">
                                            <SelectValue placeholder="Selecciona pagos diferidos" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Tipos de tarjeta</SelectLabel>
                                                {cardPaymentTypes.map((cardPaymentType) => (
                                                    <SelectItem key={cardPaymentType.id} value={cardPaymentType.id.toString()}>
                                                        {cardPaymentType.name}
                                                    </SelectItem>
                                                ))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )} />
                    <FormField
                        control={form.control}
                        name="totalPaid"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Total pagado</FormLabel>
                                <FormControl>
                                    <Input
                                        {...field}
                                        disabled={isSubmitting}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />


                    <DialogFooter>
                        <Button variant="outline" onClick={() => closeDialog()}>
                            Cancelar
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting}
                            className="min-w-24">
                            {isSubmitting ? (
                                <>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                    Guardando...
                                </>) : ('Guardar')}
                        </Button>
                    </DialogFooter>
                </form>
            </Form>
        </>
    )
}

export default PatientPaymentForm;