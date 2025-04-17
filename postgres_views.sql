-- Create view for patient appointments
CREATE OR REPLACE VIEW nlab.v_patient_appointments AS
SELECT 
    a.id AS appointment_id,
    b.patient_id AS patient_id,
    c.id AS nutritionist_id,
    CONCAT(c.first_name, ' ', c.last_name) AS nutritionist_name,
    d.name AS package_name,
    a.appointment_date_time::date AS appointment_date,
    a.appointment_date_time::time AS appointment_time,
    a.status,
    a.created_at,
    a.updated_at
FROM nlab.appointment a
INNER JOIN nlab.purchased_package b
    ON b.id = a.purchased_package_id
INNER JOIN nlab.nutritionist c
    ON c.id = a.nutritionist_id
INNER JOIN nlab.package_type d
    ON d.id = b.package_type_id; 