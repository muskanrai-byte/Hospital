-- Create the Hospitals table first, as other tables depend on it
CREATE TABLE Hospitals (
    Hospital_ID INT AUTO_INCREMENT PRIMARY KEY,
    Name VARCHAR(255) NOT NULL,
    Location_Lat DECIMAL(10,8) NOT NULL,
    Location_Long DECIMAL(11,8) NOT NULL,
    Facility_Type VARCHAR(50) NOT NULL,
    Truth_Score DECIMAL(5,2) DEFAULT 100.00,
    Budget_Audit_Safe BOOLEAN DEFAULT TRUE,
    
    -- Ensure facility type matches the scenario's key actors
    CONSTRAINT chk_facility_type CHECK (Facility_Type IN ('Government', 'Private')),
    -- Trust score operates on a 0-100 scale
    CONSTRAINT chk_truth_score CHECK (Truth_Score BETWEEN 0.00 AND 100.00)
);

-- Create the Doctors table
CREATE TABLE Doctors (
    Doctor_ID INT AUTO_INCREMENT PRIMARY KEY,
    Full_Name VARCHAR(255) NOT NULL,
    Specialty VARCHAR(100) NOT NULL,
    Primary_Govt_ID INT, -- Nullable, as private-only doctors won't have a government ID
    
    -- If a hospital is deleted, keep the doctor but nullify their government assignment
    CONSTRAINT fk_primary_govt 
        FOREIGN KEY (Primary_Govt_ID) 
        REFERENCES Hospitals(Hospital_ID) 
        ON DELETE SET NULL
);

-- Create the Doctor_Schedules table (Junction Table)
CREATE TABLE Doctor_Schedules (
    Schedule_ID INT AUTO_INCREMENT PRIMARY KEY,
    Doctor_ID INT NOT NULL,
    Hospital_ID INT NOT NULL,
    Shift_Start DATETIME NOT NULL,
    Shift_End DATETIME NOT NULL,
    System_Clocked_In BOOLEAN DEFAULT FALSE,
    Self_Reported_Status VARCHAR(50) NOT NULL,
    
    CONSTRAINT fk_schedule_doctor 
        FOREIGN KEY (Doctor_ID) 
        REFERENCES Doctors(Doctor_ID) 
        ON DELETE CASCADE,
        
    CONSTRAINT fk_schedule_hospital 
        FOREIGN KEY (Hospital_ID) 
        REFERENCES Hospitals(Hospital_ID) 
        ON DELETE CASCADE,
        
    -- Logical constraint: A shift must end after it begins
    CONSTRAINT chk_shift_time CHECK (Shift_End > Shift_Start),
    -- Captures the specific excuse doctors use to redirect patients[cite: 1]
    CONSTRAINT chk_self_status CHECK (Self_Reported_Status IN ('Available', 'Unavailable'))
);

-- Create the Equipment_Status table
CREATE TABLE Equipment_Status (
    Equip_Status_ID INT AUTO_INCREMENT PRIMARY KEY,
    Hospital_ID INT NOT NULL,
    Equipment_Type VARCHAR(100) NOT NULL,
    Total_Inventory INT NOT NULL DEFAULT 0,
    Verified_Functional INT NOT NULL DEFAULT 0,
    Currently_In_Use INT NOT NULL DEFAULT 0,
    Last_Sensor_Ping TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_equip_hospital 
        FOREIGN KEY (Hospital_ID) 
        REFERENCES Hospitals(Hospital_ID) 
        ON DELETE CASCADE,
        
    -- Physical reality constraints to prevent impossible data states
    CONSTRAINT chk_positive_inv CHECK (Total_Inventory >= 0),
    CONSTRAINT chk_functional_inv CHECK (Verified_Functional <= Total_Inventory),
    CONSTRAINT chk_in_use_func CHECK (Currently_In_Use <= Verified_Functional)
);

-- Create the Live_Capacity table
CREATE TABLE Live_Capacity (
    Capacity_ID INT AUTO_INCREMENT PRIMARY KEY,
    Hospital_ID INT NOT NULL,
    Ward_Type VARCHAR(100) NOT NULL,
    Paper_Reported_Full BOOLEAN DEFAULT FALSE, -- Captures the "full on paper" lie[cite: 1]
    Physical_Beds_Free INT NOT NULL DEFAULT 0,
    Corridor_Beds_Active INT NOT NULL DEFAULT 0, -- Captures the "bed in the corridor" reality[cite: 1]
    Last_Updated TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_capacity_hospital 
        FOREIGN KEY (Hospital_ID) 
        REFERENCES Hospitals(Hospital_ID) 
        ON DELETE CASCADE,
        
    -- Prevent negative bed counts
    CONSTRAINT chk_physical_beds CHECK (Physical_Beds_Free >= 0),
    CONSTRAINT chk_corridor_beds CHECK (Corridor_Beds_Active >= 0)
);

-- Run this after creating the tables from the previous step
INSERT INTO Hospitals (Name, Location_Lat, Location_Long, Facility_Type, Truth_Score, Budget_Audit_Safe) VALUES
('City General (Gov)', 24.8607, 67.0011, 'Government', 45.5, TRUE),
('MediCare Private', 24.8700, 67.0200, 'Private', 92.0, FALSE),
('Trust Charity Hospital', 24.8500, 66.9900, 'Private', 88.5, TRUE);

INSERT INTO Live_Capacity (Hospital_ID, Ward_Type, Paper_Reported_Full, Physical_Beds_Free, Corridor_Beds_Active) VALUES
(1, 'ER', TRUE, 2, 5),  -- Staff claims full, but 2 beds are physically free
(2, 'ER', FALSE, 0, 0), -- Claims open, but 0 beds actually free
(3, 'ER', FALSE, 4, 0);

INSERT INTO Equipment_Status (Hospital_ID, Equipment_Type, Total_Inventory, Verified_Functional, Currently_In_Use) VALUES
(1, 'Cardiac Monitor', 10, 8, 8), -- All functional monitors in use (0 available)
(2, 'Cardiac Monitor', 5, 5, 2),  -- 3 available
(3, 'Cardiac Monitor', 15, 12, 10); -- 2 available




