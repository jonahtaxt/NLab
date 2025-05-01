import { insertAppointmentNotes } from "@/app/lib/data.appointment";
import { Button } from "@/components/ui/button";
import { Dialog, DialogContent, DialogFooter, DialogHeader, DialogTitle } from "@/components/ui/dialog";
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { Textarea } from "@/components/ui/textarea";
import { zodResolver } from "@hookform/resolvers/zod";
import { Loader2 } from "lucide-react";
import { useEffect, useState } from "react";
import { useForm } from "react-hook-form";
import { z } from "zod";

interface AppointmentNotesProps {
    appointmentId: number | undefined;
    dialogOpen: boolean;
    setDialogOpen: (open: boolean) => void;
    closeDialog: () => void;
}

const AppointmentNotes = ({ appointmentId, dialogOpen, setDialogOpen, closeDialog }: AppointmentNotesProps) => {
    const [serverError, setServerError] = useState<string | null>(null);
    const [isSubmitting, setIsSubmitting] = useState(false);
    const formSchema = z.object({
        weight: z.string()
            .min(0, { message: "El peso es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El peso debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El peso debe ser mayor a 0"
            }),
        totalFat: z.string()
            .min(0, { message: "La grasa total es requerida" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "La grasa total debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "La grasa total debe ser mayor a 0"
            }),
        upperFat: z.string()
            .min(0, { message: "La grasa superior es requerida" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "La grasa superior debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "La grasa superior debe ser mayor a 0"
            }),
        lowerFat: z.string()
            .min(0, { message: "La grasa inferior es requerida" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "La grasa inferior debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "La grasa inferior debe ser mayor a 0"
            }),
        visceralFat: z.string()
            .min(0, { message: "La grasa visceral es requerida" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "La grasa visceral debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "La grasa visceral debe ser mayor a 0"
            }),
        muscleMass: z.string()
            .min(0, { message: "La masa muscular es requerida" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "La masa muscular debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "La masa muscular debe ser mayor a 0"
            }),
        boneMass: z.string()
            .min(0, { message: "El peso óseo es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El peso óseo debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El peso óseo debe ser mayor a 0"
            }),
        metabolicAge: z.number()
            .min(0, { message: "La edad metabólica es requerida" })
            .refine(val => !isNaN(val), {
                message: "La edad metabólica debe ser un número válido"
            })
            .refine(val => val > 0, {
                message: "La edad metabólica debe ser mayor a 0"
            }),
        skinfoldSubscapular: z.string()
            .min(0, { message: "El pliegue subescapular es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El pliegue subescapular debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El pliegue subescapular debe ser mayor a 0"
            }),
        skinfoldTriceps: z.string()
            .min(0, { message: "El pliegue en triceps es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El pliegue en triceps debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El pliegue en triceps debe ser mayor a 0"
            }),
        skinfoldBiceps: z.string()
            .min(0, { message: "El pliegue en biceps es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El pliegue en biceps debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El pliegue en biceps debe ser mayor a 0"
            }),
        skinfoldIliacCrest: z.string()
            .min(0, { message: "El pliegue en cresta ilíaca es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El pliegue en cresta ilíaca debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El pliegue en cresta ilíaca debe ser mayor a 0"
            }),
        skinfoldSuprailiac: z.string()
            .min(0, { message: "El pliegue supraespinal es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El pliegue supraespinal debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El pliegue supraespinal debe ser mayor a 0"
            }),
        skinfoldAbdominal: z.string()
            .min(0, { message: "El pliegue abdominal es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El pliegue abdominal debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El pliegue abdominal debe ser mayor a 0"
            }),
        circumferenceMidArmRelaxed: z.string()
            .min(0, { message: "El perímetro de mitad del brazo relajado es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El perímetro de mitad del brazo relajado debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El perímetro de mitad del brazo relajado debe ser mayor a 0"
            }),
        circumferenceMidArmFlexed: z.string()
            .min(0, { message: "El perímetro de mitad del brazo contraído es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El perímetro de mitad del brazo contraído debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El perímetro de mitad del brazo contraído debe ser mayor a 0"
            }),
        circumferenceUmbilical: z.string()
            .min(0, { message: "El perímetro umbilical es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El perímetro umbilical debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El perímetro umbilical debe ser mayor a 0"
            }),
        circumferenceWaist: z.string()
            .min(0, { message: "El perímetro de cintura es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El perímetro de cintura debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El perímetro de cintura debe ser mayor a 0"
            }),
        circumferenceHip: z.string()
            .min(0, { message: "El perímetro de cadera es requerido" })
            .refine(val => !isNaN(parseFloat(val)), {
                message: "El perímetro de cadera debe ser un número válido"
            })
            .refine(val => parseFloat(val) > 0, {
                message: "El perímetro de cadera debe ser mayor a 0"
            }),
        notes: z.string().min(0)
    });

    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            weight: "",
            totalFat: "",
            upperFat: "",
            lowerFat: "",
            visceralFat: "",
            muscleMass: "",
            boneMass: "",
            metabolicAge: 0,
            skinfoldSubscapular: "",
            skinfoldTriceps: "",
            skinfoldBiceps: "",
            skinfoldIliacCrest: "",
            skinfoldSuprailiac: "",
            skinfoldAbdominal: "",
            circumferenceMidArmRelaxed: "",
            circumferenceMidArmFlexed: "",
            circumferenceUmbilical: "",
            circumferenceWaist: "",
            circumferenceHip: "",
            notes: ""
        }
    });

    const addAppointmentNotes = async (values: z.infer<typeof formSchema>) => {
        if (!appointmentId) {
            setServerError("No se encontró el ID de la cita");
            return;
        }

        let appointmentNotesDto = {
            id: 0,
            appointmentId: appointmentId,
            weight: parseFloat(values.weight),
            totalFat: parseFloat(values.totalFat),
            upperFat: parseFloat(values.upperFat),
            lowerFat: parseFloat(values.lowerFat),
            visceralFat: parseFloat(values.visceralFat),
            muscleMass: parseFloat(values.muscleMass),
            boneMass: parseFloat(values.boneMass),
            metabolicAge: values.metabolicAge,
            skinfoldSubscapular: parseFloat(values.skinfoldSubscapular),
            skinfoldTriceps: parseFloat(values.skinfoldTriceps),
            skinfoldBiceps: parseFloat(values.skinfoldBiceps),
            skinfoldIliacCrest: parseFloat(values.skinfoldIliacCrest),
            skinfoldSuprailiac: parseFloat(values.skinfoldSuprailiac),
            skinfoldAbdominal: parseFloat(values.skinfoldAbdominal),
            circumferenceMidArmRelaxed: parseFloat(values.circumferenceMidArmRelaxed),
            circumferenceMidArmFlexed: parseFloat(values.circumferenceMidArmFlexed),
            circumferenceUmbilical: parseFloat(values.circumferenceUmbilical),
            circumferenceWaist: parseFloat(values.circumferenceWaist),
            circumferenceHip: parseFloat(values.circumferenceHip),
            notes: values.notes
        };

        try {
            setIsSubmitting(true);
            await insertAppointmentNotes(appointmentNotesDto);
            closeDialog();
        } catch (error) {
            console.error("Error creating appointment notes:", error);
            setServerError("Error al crear las notas de la cita. Intente nuevamente.");
        } finally {
            setIsSubmitting(false);
        }
    };

    useEffect(() => {
        if (dialogOpen) {
            form.reset();
        }
    }, [dialogOpen]);

    return (
        <Dialog open={dialogOpen} onOpenChange={setDialogOpen}>
            <DialogContent className="max-w-4xl">
                <DialogHeader>
                    <DialogTitle>Notas de Cita</DialogTitle>
                </DialogHeader>
                <Form {...form}>
                    <form onSubmit={form.handleSubmit(addAppointmentNotes)} className="space-y-4">
                        {serverError && (
                            <div className="p-3 bg-red-100 text-red-700 rounded-md- test-sm">
                                {serverError}
                            </div>
                        )}
                        <div className="grid grid-cols-1 gap-4 font-bold bg-nlab-coral rounded-md p-2 text-white">
                            Bioimpedancia
                        </div>
                        <div className="grid grid-cols-4 gap-4 mb-4">
                            <FormField
                                control={form.control}
                                name="weight"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Peso</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="totalFat"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Grasa total</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="upperFat"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Grasa superior</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="lowerFat"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Grasa inferior</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        <div className="grid grid-cols-4 gap-4 mb-4">
                            <FormField
                                control={form.control}
                                name="visceralFat"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Grasa visceral</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="muscleMass"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Masa muscular</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="boneMass"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Peso óseo</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="metabolicAge"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Edad metabólica</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                type="number"
                                                step="1"
                                                min="1"
                                                placeholder="0"
                                                value={field.value || ''}
                                                onChange={(e) => field.onChange(Number(e.target.value))}
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        <div className="grid grid-cols-1 gap-4 font-bold bg-nlab-coral rounded-md p-2 text-white">
                            Pliegues
                        </div>
                        <div className="grid grid-cols-4 gap-4 mb-4">
                            <FormField
                                control={form.control}
                                name="skinfoldSubscapular"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Subescapular</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="skinfoldTriceps"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Triceps</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="skinfoldBiceps"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Biceps</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="skinfoldIliacCrest"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Cresta ilíaca</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        <div className="grid grid-cols-4 gap-4 mb-4">
                            <FormField
                                control={form.control}
                                name="skinfoldSuprailiac"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Supraespinal</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="skinfoldAbdominal"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Abdominal</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        <div className="grid grid-cols-1 gap-4 font-bold bg-nlab-coral rounded-md p-2 text-white">
                            Perímetros
                        </div>
                        <div className="grid grid-cols-4 gap-4 mb-4">
                            <FormField
                                control={form.control}
                                name="circumferenceMidArmRelaxed"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Mitad del brazo relajado</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="circumferenceMidArmFlexed"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Mitad del brazo contraído</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="circumferenceUmbilical"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Umbilical</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                            <FormField
                                control={form.control}
                                name="circumferenceWaist"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Cintura</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        <div className="grid grid-cols-4 gap-4 mb-4">
                            <FormField
                                control={form.control}
                                name="circumferenceHip"
                                render={({ field }) => (
                                    <FormItem>
                                        <FormLabel>Cadera</FormLabel>
                                        <FormControl>
                                            <Input
                                                {...field}
                                                step="0.01"
                                                min="0.01"
                                                placeholder="0.00"
                                                disabled={isSubmitting} />
                                        </FormControl>
                                        <FormMessage />
                                    </FormItem>
                                )}
                            />
                        </div>
                        <div className="grid grid-cols-1 gap-4 font-bold bg-nlab-coral rounded-md p-2 text-white">
                            Notas
                        </div>
                        <FormField
                            control={form.control}
                            name="notes"
                            render={({ field }) => (
                                <FormItem>
                                    <FormLabel>Notas</FormLabel>
                                    <FormControl>
                                        <Textarea
                                            {...field}
                                            placeholder="Notas"
                                            disabled={isSubmitting} />
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
            </DialogContent>
        </Dialog>

    );
}

export default AppointmentNotes;