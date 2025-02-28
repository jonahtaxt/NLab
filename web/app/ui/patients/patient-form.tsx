'use client';

import { useState } from 'react';
import { DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { Patient, PatientDTO } from '@/app/lib/definitions';
import { insertPatient, updatePatient } from '@/app/lib/data.patient';
import { Loader2 } from 'lucide-react';
import { showToast } from '@/lib/toaster-util';

interface PatientFormProps {
    patient?: Patient | null;
    onClose: () => void;
    onSuccess?: () => void;
    onSubmitStart?: () => void;
    onSubmitEnd?: () => void;
    isSubmitting?: boolean;
}

const PatientForm = ({ 
    patient, 
    onClose, 
    onSuccess,
    onSubmitStart,
    onSubmitEnd,
    isSubmitting = false
}: PatientFormProps) => {
    const [formData, setFormData] = useState({
        firstName: patient?.firstName || '',
        lastName: patient?.lastName || '',
        email: patient?.email || '',
        phone: patient?.phone || '',
        active: patient?.active ?? true,
    });
    
    const [errors, setErrors] = useState<Record<string, string>>({});

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {};
        
        if (!formData.firstName.trim()) {
            newErrors.firstName = 'El nombre es requerido';
        }
        
        if (!formData.lastName.trim()) {
            newErrors.lastName = 'El apellido es requerido';
        }
        
        if (!formData.email.trim()) {
            newErrors.email = 'El correo electrónico es requerido';
        } else if (!/\S+@\S+\.\S+/.test(formData.email)) {
            newErrors.email = 'El correo electrónico no es válido';
        }
        
        if (!formData.phone.trim()) {
            newErrors.phone = 'El teléfono es requerido';
        } else if (!/^\d{10}$/.test(formData.phone)) {
            newErrors.phone = 'El teléfono debe tener 10 dígitos';
        }
        
        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;
        setFormData((prev) => ({
            ...prev,
            [name]: type === 'checkbox' ? checked : value,
        }));
        
        // Clear error when user starts typing
        if (errors[name]) {
            setErrors(prev => ({ ...prev, [name]: '' }));
        }
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        
        if (!validateForm()) {
            return;
        }
        
        try {
            // Signal that submission is starting
            if (onSubmitStart) {
                onSubmitStart();
            }
            
            const patientDTO: PatientDTO = {
                id: patient?.id || 0,
                firstName: formData.firstName,
                lastName: formData.lastName,
                email: formData.email,
                phone: formData.phone,
                active: formData.active
            }
            
            if(patient) {
                const updatedPatient = await updatePatient(patientDTO);
            } else {
                const newPatient = await insertPatient(patientDTO);
            }
            
            // Show success notification
            showToast.success(patient?.id ? 'Paciente actualizado correctamente' : 'Paciente creado correctamente');
            
            // Call the success callback to trigger refresh in parent
            if (onSuccess) {
                onSuccess();
            }
            
            onClose();
        } catch (error) {
            console.error('Error saving patient:', error);
            showToast.error('Error al guardar el paciente');
            setErrors({
                submit: 'Ocurrió un error al guardar el paciente. Por favor, inténtalo de nuevo.'
            });
        } finally {
            // Signal that submission has ended
            if (onSubmitEnd) {
                onSubmitEnd();
            }
        }
    };

    return (
        <form onSubmit={handleSubmit} className="space-y-4">
            {errors.submit && (
                <div className="p-3 bg-red-100 text-red-700 rounded-md text-sm">
                    {errors.submit}
                </div>
            )}
            
            <div>
                <Label htmlFor="firstName" className="flex justify-between">
                    <span>Nombre</span>
                    {errors.firstName && <span className="text-red-500 text-xs">{errors.firstName}</span>}
                </Label>
                <Input 
                    id="firstName" 
                    name="firstName" 
                    value={formData.firstName} 
                    onChange={handleChange} 
                    className={errors.firstName ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>
            
            <div>
                <Label htmlFor="lastName" className="flex justify-between">
                    <span>Apellido</span>
                    {errors.lastName && <span className="text-red-500 text-xs">{errors.lastName}</span>}
                </Label>
                <Input 
                    id="lastName" 
                    name="lastName" 
                    value={formData.lastName} 
                    onChange={handleChange}
                    className={errors.lastName ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>
            
            <div>
                <Label htmlFor="email" className="flex justify-between">
                    <span>Correo Electrónico</span>
                    {errors.email && <span className="text-red-500 text-xs">{errors.email}</span>}
                </Label>
                <Input 
                    id="email" 
                    name="email" 
                    type="email" 
                    value={formData.email} 
                    onChange={handleChange}
                    className={errors.email ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>
            
            <div>
                <Label htmlFor="phone" className="flex justify-between">
                    <span>Teléfono</span>
                    {errors.phone && <span className="text-red-500 text-xs">{errors.phone}</span>}
                </Label>
                <Input 
                    id="phone" 
                    name="phone" 
                    value={formData.phone} 
                    onChange={handleChange}
                    className={errors.phone ? "border-red-500" : ""}
                    disabled={isSubmitting}
                    maxLength={10}
                />
            </div>
            
            <div className="flex items-center gap-2">
                <input 
                    id="active" 
                    name="active" 
                    type="checkbox" 
                    checked={formData.active} 
                    onChange={handleChange}
                    disabled={isSubmitting}
                />
                <Label htmlFor="isActive">Activo</Label>
            </div>
            
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
    );
};

export default PatientForm;