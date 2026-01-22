CREATE VIEW [dbo].[vPatientAppointments]
  AS 
  SELECT a.Id AS AppointmentId,
    b.PatientID AS PatientId,
    c.Id AS NutritionistId,
    CONCAT(c.FirstName, ' ', c.LastName) AS NutritionistName,
    d.Name AS PackageName,
    CAST(a.AppointmentDateTime AS DATE) AS AppointmentDate,
    CAST(a.AppointmentDateTime AS TIME) AS AppointmentTime,
    a.Status,
    a.CreatedAt,
    a.UpdatedAt
FROM dbo.Appointment a
INNER JOIN dbo.PurchasedPackage b
  ON b.Id = a.PurchasedPackageID
INNER JOIN dbo.Nutritionist c
  ON c.Id = a.NutritionistID
INNER JOIN dbo.PackageType d
  ON d.Id = b.PackageTypeID
