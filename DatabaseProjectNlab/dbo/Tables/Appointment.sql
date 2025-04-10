CREATE TABLE [dbo].[Appointment] (
    [Id]                  INT            IDENTITY (1, 1) NOT NULL,
    [PurchasedPackageID]  INT            NOT NULL,
    [NutritionistID]      INT            NOT NULL,
    [AppointmentDateTime] DATETIME2 (7)  NOT NULL,
    [Status]              NVARCHAR (20)  NOT NULL,
    [Notes]               NVARCHAR (MAX) NULL,
    [CreatedAt]           DATETIME2 (7)  DEFAULT (getdate()) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC),
    FOREIGN KEY ([NutritionistID]) REFERENCES [dbo].[Nutritionist] ([Id]),
    FOREIGN KEY ([PurchasedPackageID]) REFERENCES [dbo].[PurchasedPackage] ([Id])
);


GO

