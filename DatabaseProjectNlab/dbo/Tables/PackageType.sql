CREATE TABLE [dbo].[PackageType] (
    [Id]                   INT             IDENTITY (1, 1) NOT NULL,
    [Name]                 NVARCHAR (50)   NOT NULL,
    [Description]          NVARCHAR (200)  NULL,
    [NumberOfAppointments] INT             NOT NULL,
    [IsBundle]             BIT             DEFAULT ((0)) NULL,
    [Price]                DECIMAL (10, 2) NOT NULL,
    [NutritionistRate]     DECIMAL (10, 2) NOT NULL,
    [Active]             BIT             DEFAULT ((1)) NULL,
    [CreatedAt] DATETIME2 (7)  DEFAULT (getdate()) NULL,
    [UpdatedAt] DATETIME2 (7)  DEFAULT (getdate()) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC)
);


GO

