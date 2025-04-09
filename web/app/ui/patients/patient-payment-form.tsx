import { fetchPatientPackagePayments } from "@/app/lib/data.patient";
import { insertPatientPayment } from "@/app/lib/data.patient-payment";
import { fetchAllCardPaymentTypes, fetchAllPaymentMethods } from "@/app/lib/data.settings";
import { CardPaymentType, PatientPurchasedPackageDTO, PaymentMethod, PurchasedPackage } from "@/app/lib/definitions";
import { Button } from "@/components/ui/button";
import { DialogFooter } from "@/components/ui/dialog";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { showToast } from "@/lib/toaster-util";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
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
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [serverError, setServerError] = useState<string | null>(null);
    const [packagePaidTotal, setPackagePaidTotal] = useState<number>(0);

    // Define our form schema using Zod
    const formSchema = z.object({
        totalPaid: z.string()
            .min(1, { message: "El monto pagado es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El monto debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El monto debe ser mayor a cero"
            }),
        paymentMethod: z.string({
            required_error: "El método de pago es requerido"
        }),
        cardPaymentType: z.string().optional()
            .refine(val => {
                // Value is required only when payment method is credit card (id: 3)
                // This check is done in the form's submit handler
                return true;
            }, {
                message: "El tipo de pago con tarjeta es requerido"
            })
    });

    // Get payment methods and card payment types
    const loadPaymentMethodData = async () => {
        try {
            setIsLoading(true);
            setError(null);
            const [pMethods, cTypes, pPatientPayments] = await Promise.all([
                fetchAllPaymentMethods(),
                fetchAllCardPaymentTypes(),
                fetchPatientPackagePayments(purchasedPackage?.id || 0)
            ]);
            setCardPaymentTypes(cTypes);
            setPaymentMethods(pMethods);

            if(pPatientPayments) {
                setPackagePaidTotal(parseFloat(pPatientPayments.packagePaidTotal));
            }
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

    // Initialize the form with react-hook-form
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            totalPaid: "",
            paymentMethod: "",
            cardPaymentType: ""
        }
    });

    // Helper function to check if credit card payment method is selected
    const isCreditCardSelected = () => {
        return form.watch("paymentMethod") === "3";
    };

    // Handle form submission
    const handleAddPayment = async (values: z.infer<typeof formSchema>) => {
        setServerError(null);

        if(values.paymentMethod === "") {
            form.setError("paymentMethod", {
                type: "manual",
                message: "El método de pago es requerido"
            });
            return;
        }

        // Validate cardPaymentType when credit card is selected
        if (isCreditCardSelected() && !values.cardPaymentType) {
            form.setError("cardPaymentType", {
                type: "manual",
                message: "El tipo de pago con tarjeta es requerido"
            });
            return;
        }

        let packagePrice = purchasedPackage?.packageType?.price;

        if (!packagePrice) {
            let totalPaidFloat = parseFloat(values.totalPaid);
            if (packagePaidTotal > 0) {
                totalPaidFloat += packagePaidTotal;
            }
            let packagePriceFloat = packagePrice ? parseFloat(String(packagePrice)) : 0;
            if (totalPaidFloat > packagePriceFloat) {
                form.setError("totalPaid", {
                    type: "manual",
                    message: "El monto total pagado no puede ser mayor al precio del paquete"
                });
                return;
            }
        }

        try {
            if (!purchasedPackage?.id) {
                throw new Error('No se encontró el paquete para procesar el pago');
            }

            const patientPaymentDTO = {
                id: 0,
                purchasedPackageId: purchasedPackage.id,
                paymentMethodId: parseInt(values.paymentMethod),
                cardPaymentTypeId: values.cardPaymentType ? parseInt(values.cardPaymentType) : null,
                totalPaid: values.totalPaid
            };

            setIsSubmitting(true);
            await insertPatientPayment(patientPaymentDTO);
            showToast.success("Pago registrado exitosamente");
            savePayment();
            closeDialog();
        } catch (error) {
            console.error('Error saving payment:', error);
            if (error instanceof Error) {
                setServerError(error.message || 'Error al guardar el pago');
            } else {
                setServerError('Error al guardar el pago');
            }
            showToast.error("Error al registrar el pago");
        } finally {
            setIsSubmitting(false);
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

                    {/* Payment Method field */}
                    <FormField
                        control={form.control}
                        name="paymentMethod"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Método de pago</FormLabel>
                                <FormControl>
                                    <Select
                                        disabled={isLoading || isSubmitting}
                                        onValueChange={field.onChange}
                                        value={field.value}
                                    >
                                        <SelectTrigger className="w-full">
                                            <SelectValue placeholder="Selecciona un medio de pago" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Medios de pago</SelectLabel>
                                                {paymentMethods.map((paymentMethod) => (
                                                    <SelectItem
                                                        key={paymentMethod.id}
                                                        value={paymentMethod.id.toString()}
                                                    >
                                                        {paymentMethod.name}
                                                    </SelectItem>
                                                ))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    {/* Card Payment Type field - only required for credit card payments */}
                    <FormField
                        control={form.control}
                        name="cardPaymentType"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Pagos Diferidos</FormLabel>
                                <FormControl>
                                    <Select
                                        disabled={!isCreditCardSelected() || isSubmitting}
                                        onValueChange={field.onChange}
                                        value={field.value}
                                    >
                                        <SelectTrigger className="w-full">
                                            <SelectValue placeholder="Selecciona pagos diferidos" />
                                        </SelectTrigger>
                                        <SelectContent>
                                            <SelectGroup>
                                                <SelectLabel>Tipos de tarjeta</SelectLabel>
                                                {cardPaymentTypes.map((cardPaymentType) => (
                                                    <SelectItem
                                                        key={cardPaymentType.id}
                                                        value={cardPaymentType.id.toString()}
                                                    >
                                                        {cardPaymentType.name}
                                                    </SelectItem>
                                                ))}
                                            </SelectGroup>
                                        </SelectContent>
                                    </Select>
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    {/* Total Paid field */}
                    <FormField
                        control={form.control}
                        name="totalPaid"
                        render={({ field }) => (
                            <FormItem>
                                <FormLabel>Total pagado</FormLabel>
                                <FormControl>
                                    <Input
                                        {...field}
                                        type="number"
                                        step="0.01"
                                        min="0.01"
                                        placeholder="0.00"
                                        disabled={isSubmitting}
                                    />
                                </FormControl>
                                <FormMessage />
                            </FormItem>
                        )}
                    />

                    <DialogFooter>
                        <Button
                            variant="outline"
                            onClick={closeDialog}
                            type="button"
                            disabled={isSubmitting}
                        >
                            Cancelar
                        </Button>
                        <Button
                            type="submit"
                            disabled={isSubmitting}
                            className="min-w-24"
                        >
                            {isSubmitting ? (
                                <>
                                    <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                                    Guardando...
                                </>
                            ) : (
                                'Guardar'
                            )}
                        </Button>
                    </DialogFooter>
                </form>
            </Form>
        </>
    );
};

export default PatientPaymentForm;