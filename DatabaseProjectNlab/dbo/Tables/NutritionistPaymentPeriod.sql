CREATE TABLE [dbo].[NutritionistPaymentPeriod] (
    [Id]                INT             IDENTITY (1, 1) NOT NULL,
    [NutritionistID]    INT             NOT NULL,
    [PeriodStartDate]   DATE            NOT NULL,
    [PeriodEndDate]     DATE            NOT NULL,
    [TotalAppointments] INT             NOT NULL,
    [TotalAmount]       DECIMAL (10, 2) NOT NULL,
    [PaymentStatus]     NVARCHAR (20)   NOT NULL,
    [ProcessedDate]     DATETIME2 (7)   NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC),
    FOREIGN KEY ([NutritionistID]) REFERENCES [dbo].[Nutritionist] ([Id])
);


GO

