import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form"
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useEffect, useState } from "react";
import { Loader2 } from "lucide-react"
import { Calendar } from "@/components/ui/calendar";
import { Button } from "@/components/ui/button";
import { zodResolver } from "@hookform/resolvers/zod";
import { PurchasedPackage, Nutritionist } from "@/app/lib/definitions";
import { Select, SelectContent, SelectGroup, SelectItem, SelectLabel, SelectTrigger, SelectValue } from "@/components/ui/select";
import { fetchActiveNutritionists } from "@/app/lib/data.nutritionist";
import { DialogFooter } from "@/components/ui/dialog";
import { format } from "date-fns";
import { fetchAppointmentById, insertAppointment, updateAppointment } from "@/app/lib/data.appointment";

interface AppointmentFormProps {
    purchasedPackage: PurchasedPackage | undefined;
    appointmentId: number | undefined;
    closeDialog: () => void;
    saveAppointment: () => void;
}

const AppointmentForm = ({
    purchasedPackage,
    closeDialog,
    saveAppointment,
    appointmentId
}: AppointmentFormProps) => {
    const [serverError, setServerError] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [nutritionists, setNutritionists] = useState<Nutritionist[]>([]);

    const formSchema = z.object({
        purchasedPackageId: z.number().min(1, { message: "Debe seleccionar un paquete" }),
        appointmentStatus: z.string({
            required_error: "Debe seleccionar el estado de la cita"
        }),
        nutritionistId: z.string({
            required_error: "Debe seleccionar un nutriólogo"
        }),
        appointmentDateTime: z.date({
            required_error: "Debe seleccionar una fecha y hora"
        }),
        notes: z.string().optional()
    });

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            notes: "",
            purchasedPackageId: purchasedPackage?.id || 0,
            appointmentDateTime: undefined
        }
    });

    const loadAppointmentData = async () => {
        try {
            setIsLoading(true);
            setError(null);

            const [pNutritionists, pAppointment] = await Promise.all([
                fetchActiveNutritionists(),
                appointmentId ? fetchAppointmentById(appointmentId) : null
            ]);

            setNutritionists(pNutritionists);

            if (pAppointment) {
                form.setValue("purchasedPackageId", pAppointment.purchasedPackage.id);
                const appointmentDate = new Date(pAppointment.appointmentDateTime);
                const localDate = new Date(appointmentDate.getTime() - appointmentDate.getTimezoneOffset() * 60000);
                form.setValue("appointmentDateTime", localDate);
                form.setValue("notes", pAppointment.notes || "");
                if (pAppointment.nutritionist) {
                    const nutritionistId = pAppointment.nutritionist.id.toString();
                    setTimeout(() => {
                        form.setValue("nutritionistId", nutritionistId, {
                            shouldValidate: true,
                            shouldDirty: true,
                            shouldTouch: true
                        });
                    }, 0);
                }
                form.setValue("appointmentStatus", pAppointment.status);
            } else {
                form.setValue("appointmentStatus", "AGENDADA");
            }
        } catch (err) {
            console.error("Error loading appointment data:", err);
            setError("Error al cargar los datos de la cita. Intente nuevamente.");
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadAppointmentData();
    }, []);

    const addAppointment = async (values: z.infer<typeof formSchema>) => {
        if (!values.appointmentDateTime) {
            form.setError("appointmentDateTime", {
                type: "manual",
                message: "Debe seleccionar una fecha y hora"
            });
            return;
        }

        if (!values.nutritionistId) {
            form.setError("nutritionistId", {
                type: "manual",
                message: "Debe seleccionar un nutriólogo"
            });
            return;
        }

        if (!values.appointmentStatus) {
            form.setError("appointmentStatus", {
                type: "manual",
                message: "Debe seleccionar un estado"
            });
            return;
        }

        try {
            setIsSubmitting(true);

            // Format the date to ISO string for the backend
            const appointmentDTO = {
                id: appointmentId || 0,
                purchasedPackageId: values.purchasedPackageId,
                status: values.appointmentStatus,
                nutritionistId: parseInt(values.nutritionistId),
                appointmentDateTime: values.appointmentDateTime,
                notes: ""
            };

            if (!appointmentId) {
                await insertAppointment(appointmentDTO);
            } else {
                await updateAppointment(appointmentDTO);
                
            }
            saveAppointment();
        } catch (error) {
            console.error("Error creating appointment:", error);
            setServerError("Error al crear la cita. Intente nuevamente.");
        } finally {
            setIsSubmitting(false);
        }
    };

    const generateTimeSlots = () => {
        const slots = [];
        for (let hour = 8; hour <= 17; hour++) {
            for (let minute = 0; minute < 60; minute += 30) {
                const time = new Date();
                time.setHours(hour, minute, 0, 0);
                slots.push(time);
            }
        }
        return slots;
    };

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(addAppointment)} className="space-y-4">
                {serverError && (
                    <div className="p-3 bg-red-100 text-red-700 rounded-md text-sm">
                        {serverError}
                    </div>
                )}
                {purchasedPackage && (
                    <div className="grid grid-cols-3 gap-4 mb-4">
                        <FormItem>
                            <FormLabel>Paciente</FormLabel>
                            <FormControl>
                                <div className="font-medium">
                                    {purchasedPackage.patient.firstName + ' ' + purchasedPackage.patient.lastName}
                                </div>
                            </FormControl>
                        </FormItem>
                        <FormItem>
                            <FormLabel>Paquete</FormLabel>
                            <FormControl>
                                <div className="font-medium">
                                    {purchasedPackage.packageType.name}
                                </div>
                            </FormControl>
                        </FormItem>
                        <FormItem>
                            <FormLabel>Citas restantes</FormLabel>
                            <FormControl>
                                <div className="font-medium">
                                    {purchasedPackage.remainingAppointments}
                                </div>
                            </FormControl>
                        </FormItem>
                    </div>
                )}
                <FormField
                    control={form.control}
                    name="nutritionistId"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Nutriólogo</FormLabel>
                            <FormControl>
                                <Select
                                    disabled={isLoading || isSubmitting}
                                    onValueChange={field.onChange}
                                    value={field.value}
                                >
                                    <SelectTrigger className="w-full">
                                        <SelectValue placeholder="Selecciona un Nutriólogo" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectGroup>
                                            <SelectLabel>Nutriólogos</SelectLabel>
                                            {nutritionists.map((nutritionist) => (
                                                <SelectItem
                                                    key={nutritionist.id}
                                                    value={nutritionist.id.toString()}
                                                >
                                                    {nutritionist.firstName + ' ' + nutritionist.lastName}
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
                <FormField
                    control={form.control}
                    name="appointmentStatus"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Estado</FormLabel>
                            <FormControl>
                                <Select
                                    disabled={isLoading || isSubmitting || (appointmentId === undefined)}
                                    onValueChange={field.onChange}
                                    value={field.value}
                                >
                                    <SelectTrigger className="w-full">
                                        <SelectValue placeholder="Selecciona un estado" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        <SelectItem value="AGENDADA">AGENDADA</SelectItem>
                                        <SelectItem value="CANCELADA">CANCELADA</SelectItem>
                                        <SelectItem value="COMPLETADA">COMPLETADA</SelectItem>
                                        <SelectItem value="REAGENDADA">REAGENDADA</SelectItem>
                                        <SelectItem value="NO_ASISTENCIA">NO ASISTENCIA</SelectItem>
                                    </SelectContent>
                                </Select>
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
                />
                <FormField
                    control={form.control}
                    name="appointmentDateTime"
                    render={({ field }) => (
                        <FormItem className="flex flex-col">
                            <FormLabel>Fecha y hora de la cita</FormLabel>
                            <div className="flex gap-4">
                                <div className="rounded-md border">
                                    <Calendar
                                        mode="single"
                                        selected={field.value}
                                        onSelect={(selectedDate) => {
                                            if (selectedDate) {
                                                const timeSlots = generateTimeSlots();
                                                const selectedTime = timeSlots[0]; // Default to first available time
                                                const newDateTime = new Date(selectedDate);
                                                newDateTime.setHours(selectedTime.getHours(), selectedTime.getMinutes());
                                                field.onChange(newDateTime);
                                            }
                                        }}
                                        disabled={(date) => date <= new Date()}
                                        initialFocus
                                    />
                                </div>
                                <div className="p-3 border rounded-md flex-1">
                                    <div className="grid grid-cols-3 gap-2">
                                        {generateTimeSlots().map((time) => (
                                            <Button
                                                key={time.toISOString()}
                                                variant={
                                                    field.value &&
                                                        field.value.getHours() === time.getHours() &&
                                                        field.value.getMinutes() === time.getMinutes()
                                                        ? "default"
                                                        : "outline"
                                                }
                                                className="h-8"
                                                type="button"
                                                onClick={() => {
                                                    const newDateTime = new Date(field.value || new Date());
                                                    newDateTime.setHours(time.getHours(), time.getMinutes());
                                                    field.onChange(newDateTime);
                                                }}
                                            >
                                                {format(time, "HH:mm")}
                                            </Button>
                                        ))}
                                    </div>
                                </div>
                            </div>
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
    );
};

export default AppointmentForm;