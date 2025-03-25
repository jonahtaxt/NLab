CREATE TABLE [dbo].[PurchasedPackage]
(
    [Id] INT IDENTITY (1, 1) NOT NULL,
    [PatientID] INT NOT NULL,
    [PackageTypeID] INT NOT NULL,
    [PurchaseDate] DATETIME2 (7) DEFAULT (getdate()) NULL,
    [PaidInFull] BIT DEFAULT(0),
    [RemainingAppointments] INT NOT NULL,
    [ExpirationDate] DATETIME2 (7) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC),
    FOREIGN KEY ([PackageTypeID]) REFERENCES [dbo].[PackageType] ([Id]),
    FOREIGN KEY ([PatientID]) REFERENCES [dbo].[Patient] ([Id])
);


GO

