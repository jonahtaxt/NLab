CREATE TABLE [dbo].[PurchasedPackage]
(
    [Id] INT IDENTITY (1, 1) NOT NULL,
    [PatientID] INT NOT NULL,
    [PackageTypeID] INT NOT NULL,
    [PaymentMethodID] INT NOT NULL,
    [CardPaymentTypeID] INT NULL,
    [PurchaseDate] DATETIME2 (7) DEFAULT (getdate()) NULL,
    [RemainingAppointments] INT NOT NULL,
    [ExpirationDate] DATETIME2 (7) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC),
    FOREIGN KEY ([CardPaymentTypeID]) REFERENCES [dbo].[CardPaymentType] ([Id]),
    FOREIGN KEY ([PackageTypeID]) REFERENCES [dbo].[PackageType] ([Id]),
    FOREIGN KEY ([PatientID]) REFERENCES [dbo].[Patient] ([Id]),
    FOREIGN KEY ([PaymentMethodID]) REFERENCES [dbo].[PaymentMethod] ([Id])
);


GO

