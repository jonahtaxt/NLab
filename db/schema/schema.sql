-- Create schema
CREATE SCHEMA IF NOT EXISTS nlab;

-- Set search path
SET search_path TO nlab;

-- Create tables
CREATE TABLE patient (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE nutritionist (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    phone VARCHAR(20),
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE package_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    number_of_appointments INTEGER NOT NULL,
    bundle BOOLEAN DEFAULT FALSE,
    price DECIMAL(10, 2) NOT NULL,
    nutritionist_rate DECIMAL(10, 2) NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE purchased_package (
    id SERIAL PRIMARY KEY,
    patient_id INTEGER NOT NULL REFERENCES patient(id),
    package_type_id INTEGER NOT NULL REFERENCES package_type(id),
    purchase_date TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    paid_in_full BOOLEAN DEFAULT FALSE,
    remaining_appointments INTEGER NOT NULL,
    expiration_date TIMESTAMP WITH TIME ZONE
);

CREATE TABLE payment_method (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    display_order INTEGER DEFAULT 0 NOT NULL
);

CREATE TABLE card_payment_type (
    id SERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    description VARCHAR(200),
    bank_fee_percentage DECIMAL(5, 2) NOT NULL,
    number_of_installments INTEGER DEFAULT 1,
    active BOOLEAN DEFAULT TRUE
);

CREATE TABLE patient_payment (
    id SERIAL PRIMARY KEY,
    purchased_package_id INTEGER NOT NULL REFERENCES purchased_package(id),
    payment_method_id INTEGER NOT NULL REFERENCES payment_method(id),
    card_payment_type_id INTEGER REFERENCES card_payment_type(id),
    total_paid DECIMAL(10, 2) NOT NULL,
    payment_date TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE appointment (
    id SERIAL PRIMARY KEY,
    purchased_package_id INTEGER NOT NULL REFERENCES purchased_package(id),
    nutritionist_id INTEGER NOT NULL REFERENCES nutritionist(id),
    appointment_date_time TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE nutritionist_payment_period (
    id SERIAL PRIMARY KEY,
    nutritionist_id INTEGER NOT NULL REFERENCES nutritionist(id),
    period_start_date DATE NOT NULL,
    period_end_date DATE NOT NULL,
    total_appointments INTEGER NOT NULL,
    total_amount DECIMAL(10, 2) NOT NULL,
    payment_status VARCHAR(20) NOT NULL,
    processed_date TIMESTAMP WITH TIME ZONE
);

-- Create indexes
CREATE INDEX idx_patient_email ON patient(email);
CREATE INDEX idx_nutritionist_email ON nutritionist(email);
CREATE INDEX idx_appointment_date ON appointment(appointment_date_time);
CREATE INDEX idx_purchased_package_patient ON purchased_package(patient_id);
CREATE INDEX idx_purchased_package_expiration ON purchased_package(expiration_date);
CREATE INDEX idx_nutritionist_payment_period_dates ON nutritionist_payment_period(period_start_date, period_end_date);

-- Create function to update updated_at timestamp
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create triggers for updated_at
CREATE TRIGGER update_patient_updated_at
    BEFORE UPDATE ON patient
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_nutritionist_updated_at
    BEFORE UPDATE ON nutritionist
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_package_type_updated_at
    BEFORE UPDATE ON package_type
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_appointment_updated_at
    BEFORE UPDATE ON appointment
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();

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