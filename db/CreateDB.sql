-- Create Users tables
CREATE TABLE Nutritionists (
    NutritionistID INT PRIMARY KEY IDENTITY(1,1),
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Phone NVARCHAR(20),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1
);

CREATE TABLE Patients (
    PatientID INT PRIMARY KEY IDENTITY(1,1),
    FirstName NVARCHAR(50) NOT NULL,
    LastName NVARCHAR(50) NOT NULL,
    Email NVARCHAR(100) UNIQUE NOT NULL,
    Phone NVARCHAR(20),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    IsActive BIT DEFAULT 1
);

-- Package related tables
CREATE TABLE PackageTypes (
    PackageTypeID INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(50) NOT NULL,
    Description NVARCHAR(200),
    NumberOfAppointments INT NOT NULL,
    IsBundle BIT DEFAULT 0,
    Price DECIMAL(10,2) NOT NULL,
    NutritionistRate DECIMAL(10,2) NOT NULL,
    IsActive BIT DEFAULT 1
);

CREATE TABLE PaymentMethods (
    PaymentMethodID INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(50) NOT NULL,
    Description NVARCHAR(200)
);

CREATE TABLE CardPaymentTypes (
    CardPaymentTypeID INT PRIMARY KEY IDENTITY(1,1),
    Name NVARCHAR(50) NOT NULL,
    Description NVARCHAR(200),
    BankFeePercentage DECIMAL(5,2) NOT NULL,
    NumberOfInstallments INT DEFAULT 1,
    IsActive BIT DEFAULT 1
);

-- Purchased packages and appointments
CREATE TABLE PurchasedPackages (
    PurchasedPackageID INT PRIMARY KEY IDENTITY(1,1),
    PatientID INT NOT NULL,
    PackageTypeID INT NOT NULL,
    PaymentMethodID INT NOT NULL,
    CardPaymentTypeID INT NULL,
    PurchaseDate DATETIME2 DEFAULT GETDATE(),
    TotalAmount DECIMAL(10,2) NOT NULL,
    RemainingAppointments INT NOT NULL,
    ExpirationDate DATETIME2,
    FOREIGN KEY (PatientID) REFERENCES Patients(PatientID),
    FOREIGN KEY (PackageTypeID) REFERENCES PackageTypes(PackageTypeID),
    FOREIGN KEY (PaymentMethodID) REFERENCES PaymentMethods(PaymentMethodID),
    FOREIGN KEY (CardPaymentTypeID) REFERENCES CardPaymentTypes(CardPaymentTypeID)
);

CREATE TABLE Appointments (
    AppointmentID INT PRIMARY KEY IDENTITY(1,1),
    PurchasedPackageID INT NOT NULL,
    NutritionistID INT NOT NULL,
    AppointmentDateTime DATETIME2 NOT NULL,
    Status NVARCHAR(20) NOT NULL, -- Scheduled, Completed, Cancelled, etc.
    Notes NVARCHAR(500),
    CreatedAt DATETIME2 DEFAULT GETDATE(),
    FOREIGN KEY (PurchasedPackageID) REFERENCES PurchasedPackages(PurchasedPackageID),
    FOREIGN KEY (NutritionistID) REFERENCES Nutritionists(NutritionistID)
);

-- Nutritionist payments tracking
CREATE TABLE NutritionistPaymentPeriods (
    PaymentPeriodID INT PRIMARY KEY IDENTITY(1,1),
    NutritionistID INT NOT NULL,
    PeriodStartDate DATE NOT NULL,
    PeriodEndDate DATE NOT NULL,
    TotalAppointments INT NOT NULL,
    TotalAmount DECIMAL(10,2) NOT NULL,
    PaymentStatus NVARCHAR(20) NOT NULL, -- Pending, Paid, etc.
    ProcessedDate DATETIME2,
    FOREIGN KEY (NutritionistID) REFERENCES Nutritionists(NutritionistID)
);

-- Insert initial data for payment methods
INSERT INTO PaymentMethods (Name, Description)
VALUES 
    ('Cash', 'Pago en Efectivo'),
    ('Debit', 'Pago con Tarjeta de Débito'),
    ('Credit', 'Pago con Tarjeta de Crédito');

-- Insert sample card payment types
INSERT INTO CardPaymentTypes (Name, Description, BankFeePercentage, NumberOfInstallments)
VALUES 
    ('Pago Regular', 'Pago Regular', 2.5, 1),
    ('3 meses', '3 Meses Sin Intereses', 3.5, 3),
    ('6 meses', '6 Meses Sin Intereses', 4.5, 6);