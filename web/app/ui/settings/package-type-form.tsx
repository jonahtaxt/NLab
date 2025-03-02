'use client';

import { useState } from 'react';
import { DialogFooter } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Label } from '@/components/ui/label';
import { PackageType, PackageTypeDTO } from '@/app/lib/definitions';
import { Loader2 } from 'lucide-react';
import { showToast } from '@/lib/toaster-util';
import { insertPackageType, updatePackageType } from '@/app/lib/data.settings';

interface PackageTypeFormProps {
    packageType?: PackageType | null;
    onClose: () => void;
    onSuccess?: () => void;
    onSubmitStart?: () => void;
    onSubmitEnd?: () => void;
    isSubmitting?: boolean;
}

const PackageTypeForm = ({
    packageType,
    onClose,
    onSuccess,
    onSubmitStart,
    onSubmitEnd,
    isSubmitting = false
}: PackageTypeFormProps) => {
    const [formData, setFormData] = useState({
        name: packageType?.name || '',
        description: packageType?.description || '',
        numberOfAppointments: packageType?.numberOfAppointments || '',
        bundle: packageType?.bundle ?? false,
        price: packageType?.price || '',
        nutritionistRate: packageType?.nutritionistRate || '',
        active: packageType?.active ?? true,
    });

    const [errors, setErrors] = useState<Record<string, string>>({});

    const validateForm = (): boolean => {
        const newErrors: Record<string, string> = {};

        if (!formData.name.trim()) {
            newErrors.name = 'El nombre es requerido';
        }

        if (!formData.description.trim()) {
            newErrors.description = 'La descripción es requerida';
        }

        if (!formData.numberOfAppointments) {
            newErrors.numberOfAppointments = 'El número de citas es requerido';
        } else if (!/^\d+$/.test(String(formData.numberOfAppointments))) {
            newErrors.numberOfAppointments = 'El número de citas debe ser un número entero';
        } else if (parseInt(String(formData.numberOfAppointments)) <= 0) {
            newErrors.numberOfAppointments = 'El número de citas debe ser mayor a 0';
        }

        if (!formData.price) {
            newErrors.price = 'El precio es requerido';
        } else if (!/^\d+(\.\d{1,2})?$/.test(String(formData.price))) {
            newErrors.price = 'El precio debe ser un número con máximo 2 decimales';
        } else if (parseFloat(String(formData.price)) <= 0) {
            newErrors.price = 'El precio debe ser mayor a 0';
        }

        if (!formData.nutritionistRate) {
            newErrors.nutritionistRate = 'La tarifa de nutricionista es requerida';
        } else if (!/^0?\.\d{1,2}$/.test(String(formData.nutritionistRate))) {
            newErrors.nutritionistRate = 'La tarifa debe ser un decimal entre 0 y 1 (ejemplo: 0.70)';
        } else {
            const rate = parseFloat(String(formData.nutritionistRate));
            if (rate <= 0 || rate >= 1) {
                newErrors.nutritionistRate = 'La tarifa debe ser mayor a 0 y menor a 1';
            }
        }

        setErrors(newErrors);
        return Object.keys(newErrors).length === 0;
    };

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const { name, value, type, checked } = e.target;

        // Using a callback form of setState to ensure we're working with the latest state
        setFormData(prev => ({
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

            const packageTypeDTO: PackageTypeDTO = {
                id: packageType?.id || 0,
                name: formData.name,
                description: formData.description,
                numberOfAppointments: parseInt(String(formData.numberOfAppointments)),
                bundle: formData.bundle, // Correctly pass the boolean value
                price: String(formData.price),
                nutritionistRate: String(formData.nutritionistRate),
                active: formData.active
            };

            console.log("Submitting package type:", packageTypeDTO);

            if (packageType) {
                const updatedPackageType = await updatePackageType(packageTypeDTO);
                showToast.success('Paquete actualizado correctamente');
            } else {
                const newPackageType = await insertPackageType(packageTypeDTO);
                showToast.success('Paquete creado correctamente');
            }

            // Call the success callback to trigger refresh in parent
            if (onSuccess) {
                onSuccess();
            }

            onClose();
        } catch (error) {
            console.error('Error saving package type:', error);
            showToast.error('Error al guardar el paquete');
            setErrors({
                submit: 'Ocurrió un error al guardar el paquete. Por favor, inténtalo de nuevo.'
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
                <Label htmlFor="name" className="flex justify-between">
                    <span>Nombre</span>
                    {errors.name && <span className="text-red-500 text-xs">{errors.name}</span>}
                </Label>
                <Input
                    id="name"
                    name="name"
                    value={formData.name}
                    onChange={handleChange}
                    className={errors.name ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>

            <div>
                <Label htmlFor="description" className="flex justify-between">
                    <span>Descripción</span>
                    {errors.description && <span className="text-red-500 text-xs">{errors.description}</span>}
                </Label>
                <Input
                    id="description"
                    name="description"
                    value={formData.description}
                    onChange={handleChange}
                    className={errors.description ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>

            <div>
                <Label htmlFor="numberOfAppointments" className="flex justify-between">
                    <span>Total de Citas</span>
                    {errors.numberOfAppointments && <span className="text-red-500 text-xs">{errors.numberOfAppointments}</span>}
                </Label>
                <Input
                    id="numberOfAppointments"
                    name="numberOfAppointments"
                    type="number"
                    min="1"
                    value={formData.numberOfAppointments}
                    onChange={handleChange}
                    className={errors.numberOfAppointments ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>

            <div className="flex items-center gap-2">
                <input
                    id="bundle"
                    name="bundle"
                    type="checkbox"
                    checked={formData.bundle}
                    onChange={handleChange}
                    disabled={isSubmitting}
                />
                <Label htmlFor="bundle">Es Paquete</Label>
            </div>

            <div>
                <Label htmlFor="price" className="flex justify-between">
                    <span>Precio</span>
                    {errors.price && <span className="text-red-500 text-xs">{errors.price}</span>}
                </Label>
                <Input
                    id="price"
                    name="price"
                    type="number"
                    step="0.01"
                    min="0.01"
                    value={formData.price}
                    onChange={handleChange}
                    className={errors.price ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
            </div>

            <div>
                <Label htmlFor="nutritionistRate" className="flex justify-between">
                    <span>Tarifa del Nutriólogo (0.01-0.99)</span>
                    {errors.nutritionistRate && <span className="text-red-500 text-xs">{errors.nutritionistRate}</span>}
                </Label>
                <Input
                    id="nutritionistRate"
                    name="nutritionistRate"
                    type="number"
                    step="0.01"
                    min="0.01"
                    max="0.99"
                    placeholder="Ejemplo: 0.70 (70%)"
                    value={formData.nutritionistRate}
                    onChange={handleChange}
                    className={errors.nutritionistRate ? "border-red-500" : ""}
                    disabled={isSubmitting}
                />
                <p className="text-xs text-gray-500 mt-1">
                    Porcentaje expresado como decimal (ej: 0.70 = 70%)
                </p>
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
                <Label htmlFor="active">Activo</Label>
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

export default PackageTypeForm;