CREATE TABLE [dbo].[Nutritionist] (
    [Id]        INT            IDENTITY (1, 1) NOT NULL,
    [FirstName] NVARCHAR (50)  NOT NULL,
    [LastName]  NVARCHAR (50)  NOT NULL,
    [Email]     NVARCHAR (100) NOT NULL,
    [Phone]     NVARCHAR (20)  NULL,
    [CreatedAt] DATETIME2 (7)  DEFAULT (getdate()) NULL,
    [UpdatedAt] DATETIME2 (7)  DEFAULT (getdate()) NULL,
    [IsActive]  BIT            DEFAULT ((1)) NULL,
    PRIMARY KEY CLUSTERED ([Id] ASC),
    UNIQUE NONCLUSTERED ([Email] ASC)
);


GO

