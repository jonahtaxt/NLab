/*
Post-Deployment Script
----------------------
*/

IF NOT EXISTS (SELECT 1 FROM [dbo].[PaymentMethod])
BEGIN
    INSERT INTO [dbo].[PaymentMethod] ([Name], [Description], [DisplayOrder])
    VALUES 
        ('Efectivo', 'Pago en Efectivo', 1),
        ('Débito', 'Pago con Tarjeta de Débito', 2),
        ('Crédito', 'Pago con Tarjeta de Crédito', 3);
END

IF NOT EXISTS (SELECT 1 FROM [dbo].[CardPaymentType])
BEGIN
    INSERT INTO [dbo].[CardPaymentType] ([Name], [Description], [BankFeePercentage], [NumberOfInstallments])
    VALUES 
        ('Pago Regular', 'Pago Regular', 2.5, 1),
        ('3 meses', '3 Meses Sin Intereses', 3.5, 3),
        ('6 meses', '6 Meses Sin Intereses', 4.5, 6);
END