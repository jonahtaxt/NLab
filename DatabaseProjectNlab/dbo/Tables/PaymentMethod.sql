CREATE TABLE [dbo].[PaymentMethod] (
    [Id]          INT            IDENTITY (1, 1) NOT NULL,
    [Name]        NVARCHAR (50)  NOT NULL,
    [Description] NVARCHAR (200) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC)
);


GO

