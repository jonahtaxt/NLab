CREATE TABLE [dbo].[CardPaymentType] (
    [Id]                   INT            IDENTITY (1, 1) NOT NULL,
    [Name]                 NVARCHAR (50)  NOT NULL,
    [Description]          NVARCHAR (200) NULL,
    [BankFeePercentage]    DECIMAL (5, 2) NOT NULL,
    [NumberOfInstallments] INT            DEFAULT ((1)) NULL,
    [Active]             BIT            DEFAULT ((1)) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC)
);


GO

