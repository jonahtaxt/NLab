import { fetchAllCardPaymentTypes, fetchAllPaymentMethods } from "@/app/lib/data.settings";
import { CardPaymentType, Patient, PaymentMethod, PurchasedPackage } from "@/app/lib/definitions";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { zodResolver } from "@hookform/resolvers/zod";
import { useEffect, useState } from "react";
import { set, useForm } from "react-hook-form";
import { z } from "zod";

interface PatientPaymentFormProps {
    purchasedPackage: PurchasedPackage | undefined;
}

const PatientPaymentForm = ({
    purchasedPackage
}: PatientPaymentFormProps) => {
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const [paymentMethods, setPaymentMethods] = useState<PaymentMethod[]>([]);
    const [cardPaymentTypes, setCardPaymentTypes] = useState<CardPaymentType[]>([]);
    const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<string>('');
    const [selectedCardPaymentType, setSelectedCardPaymentType] = useState<string>('');
    const [isSubmitting, setIsSubmitting] = useState(false);

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
        totalPaid: z.number().refine(tp => tp.toString().split('.')),
        paymentMethod: z.string().min(1, { message: 'El método de pago es requerido' }),
        cardPaymentType: z.string()
    });

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            totalPaid: 0,
        }
    });

    return (
        <>
            <Form {...form}>
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

            </Form>
        </>
    )
}

export default PatientPaymentForm;