CREATE TABLE [dbo].[PatientPayment]
(
  [Id] INT IDENTITY (1, 1) NOT NULL,
  [PurchasedPackageId] INT NOT NULL,
  [PaymentMethodID] INT NOT NULL,
  [CardPaymentTypeID] INT NULL,
  [TotalPaid] DECIMAL(10, 2) NOT NULL,
  [PaymentDate] DATETIME2(7) NOT NULL,
  PRIMARY KEY CLUSTERED ([Id] ASC),
  FOREIGN KEY ([PurchasedPackageId]) REFERENCES [dbo].[PurchasedPackage] ([Id]),
  FOREIGN KEY ([CardPaymentTypeID]) REFERENCES [dbo].[CardPaymentType] ([Id]),
  FOREIGN KEY ([PaymentMethodID]) REFERENCES [dbo].[PaymentMethod] ([Id])
)
