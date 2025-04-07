'use client';

import { useState } from 'react';
import { DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Patient, PatientDTO } from '@/app/lib/definitions';
import { insertPatient, updatePatient } from '@/app/lib/data.patient';
import { Loader2 } from 'lucide-react';
import { showToast } from '@/lib/toaster-util';
import { zodResolver } from '@hookform/resolvers/zod';
import { useForm } from 'react-hook-form';
import { z } from 'zod';
import {
    Form,
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form';
import { Input } from '@/components/ui/input';
import { Checkbox } from '@/components/ui/checkbox';

interface PatientFormProps {
    patient?: Patient | null;
    onClose: () => void;
    onSuccess?: () => void;
    onSubmitStart?: () => void;
    onSubmitEnd?: () => void;
    isSubmitting?: boolean;
}

// Define our form schema using Zod
const formSchema = z.object({
    firstName: z.string().min(1, { message: 'El nombre es requerido' }),
    lastName: z.string().min(1, { message: 'El apellido es requerido' }),
    email: z.string().email({ message: 'El correo electrónico no es válido' }),
    phone: z.string().regex(/^\d{10}$/, { message: 'El teléfono debe tener 10 dígitos' }),
    active: z.boolean().default(true),
});

const PatientForm = ({
    patient,
    onClose,
    onSuccess,
    onSubmitStart,
    onSubmitEnd,
    isSubmitting = false
}: PatientFormProps) => {
    const [serverError, setServerError] = useState<string | null>(null);

    // Initialize form with react-hook-form and zod validation
    const form = useForm<z.infer<typeof formSchema>>({
        resolver: zodResolver(formSchema),
        defaultValues: {
            firstName: patient?.firstName || '',
            lastName: patient?.lastName || '',
            email: patient?.email || '',
            phone: patient?.phone || '',
            active: patient?.active ?? true,
        },
    });

    const handleSubmit = async (values: z.infer<typeof formSchema>) => {
        setServerError(null);

        try {
            // Signal that submission is starting
            if (onSubmitStart) {
                onSubmitStart();
            }

            const patientDTO: PatientDTO = {
                id: patient?.id || null,
                firstName: values.firstName,
                lastName: values.lastName,
                email: values.email,
                phone: values.phone,
                active: values.active
            };

            if (patient) {
                await updatePatient(patientDTO);
                showToast.success('Paciente actualizado correctamente');
            } else {
                await insertPatient(patientDTO);
                showToast.success('Paciente creado correctamente');
            }

            // Call the success callback to trigger refresh in parent
            if (onSuccess) {
                onSuccess();
            }

            onClose();
        } catch (error) {
            console.error('Error saving patient:', error);

            if (error instanceof Error) {
                setServerError(error.message || 'Error al guardar el paciente');
            } else {
                setServerError('Error al guardar el paciente');
            }

            showToast.error('Error al guardar el paciente');
        } finally {
            // Signal that submission has ended
            if (onSubmitEnd) {
                onSubmitEnd();
            }
        }
    };

    return (
        <Form {...form}>
            <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
                {serverError && (
                    <div className="p-3 bg-red-100 text-red-700 rounded-md text-sm">
                        {serverError}
                    </div>
                )}

                <FormField
                    control={form.control}
                    name="firstName"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Nombre</FormLabel>
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

                <FormField
                    control={form.control}
                    name="lastName"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Apellido</FormLabel>
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

                <FormField
                    control={form.control}
                    name="email"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Correo Electrónico</FormLabel>
                            <FormControl>
                                <Input
                                    {...field}
                                    type="email"
                                    disabled={isSubmitting}
                                />
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                <FormField
                    control={form.control}
                    name="phone"
                    render={({ field }) => (
                        <FormItem>
                            <FormLabel>Teléfono</FormLabel>
                            <FormControl>
                                <Input
                                    {...field}
                                    maxLength={10}
                                    disabled={isSubmitting}
                                />
                            </FormControl>
                            <FormMessage />
                        </FormItem>
                    )}
                />

                <FormField
                    control={form.control}
                    name="active"
                    render={({ field }) => (
                        <FormItem className="flex flex-row items-start space-x-3 space-y-0 rounded-md">
                            <FormControl>
                                <Checkbox
                                    checked={field.value}
                                    onCheckedChange={field.onChange}
                                    disabled={isSubmitting}
                                />
                            </FormControl>
                            <div className="space-y-1 leading-none">
                                <FormLabel>Activo</FormLabel>
                            </div>
                        </FormItem>
                    )}
                />

                <DialogFooter>
                    <Button
                        type="button"
                        variant="outline"
                        onClick={onClose}
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

export default PatientForm;