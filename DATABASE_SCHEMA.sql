-- SATYA Portal Database Schema
-- This schema defines all tables needed for the SATYA Portal application

-- Users table
CREATE TABLE users (
    user_id VARCHAR(50) PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'USER', 'VIEWER') NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    department VARCHAR(100),
    is_active BOOLEAN DEFAULT TRUE,
    last_login TIMESTAMP NULL,
    created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Layouts table
CREATE TABLE layouts (
    file_number VARCHAR(50) PRIMARY KEY,
    layout_name VARCHAR(200) NOT NULL,
    status VARCHAR(50) NOT NULL,
    owner_name VARCHAR(100) NOT NULL,
    survey_number VARCHAR(100) NOT NULL,
    area_in_acres DECIMAL(10,2),
    application_date DATE,
    approval_date DATE NULL,
    latitude DECIMAL(10,6),
    longitude DECIMAL(10,6),
    remarks TEXT,
    total_plots INT,
    approved_by VARCHAR(100),
    document_path VARCHAR(500),
    has_court_case BOOLEAN DEFAULT FALSE,
    zone_classification VARCHAR(100),
    setback_compliance DECIMAL(5,2) DEFAULT 100.00
);

-- Court Cases table
CREATE TABLE court_cases (
    case_id VARCHAR(50) PRIMARY KEY,
    case_title VARCHAR(200) NOT NULL,
    case_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    filing_date DATE,
    hearing_date DATE NULL,
    judgment_date DATE NULL,
    description TEXT,
    related_layout_id VARCHAR(50),
    court_name VARCHAR(100),
    judge_name VARCHAR(100),
    petitioner VARCHAR(100),
    respondent VARCHAR(100),
    case_number VARCHAR(100),
    outcome TEXT,
    document_path VARCHAR(500),
    FOREIGN KEY (related_layout_id) REFERENCES layouts(file_number) ON DELETE CASCADE
);

-- Feedback table
CREATE TABLE feedback (
    feedback_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    message TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    submitted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_resolved BOOLEAN DEFAULT FALSE,
    response TEXT,
    category VARCHAR(100),
    rating INT CHECK (rating >= 1 AND rating <= 5),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Violations table
CREATE TABLE violations (
    violation_id VARCHAR(50) PRIMARY KEY,
    user_id VARCHAR(50),
    violation_type VARCHAR(100) NOT NULL,
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    document_id VARCHAR(50),
    description TEXT,
    ip_address VARCHAR(45),
    user_agent TEXT,
    is_blocked BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Documents table (for storing document metadata)
CREATE TABLE documents (
    document_id VARCHAR(50) PRIMARY KEY,
    layout_id VARCHAR(50) NOT NULL,
    document_name VARCHAR(200) NOT NULL,
    file_path VARCHAR(500),
    upload_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    document_type VARCHAR(50),
    file_size BIGINT,
    FOREIGN KEY (layout_id) REFERENCES layouts(file_number) ON DELETE CASCADE
);

-- Insert sample data for testing
-- Sample users
INSERT INTO users (user_id, username, password, role, full_name, email, department) VALUES
('U001', 'admin', 'admin123', 'ADMIN', 'Administrator', 'admin@satya.gov.in', 'IT Department'),
('U002', 'user', 'user123', 'USER', 'Regular User', 'user@satya.gov.in', 'Revenue Department'),
('U003', 'viewer', 'viewer123', 'VIEWER', 'Document Viewer', 'viewer@satya.gov.in', 'Public Services'),
('U004', 'surveyor', 'survey123', 'USER', 'Field Surveyor', 'surveyor@satya.gov.in', 'Survey Department'),
('U005', 'manager', 'manager123', 'ADMIN', 'Layout Manager', 'manager@satya.gov.in', 'Urban Planning');

-- Sample layouts
INSERT INTO layouts (file_number, layout_name, status, owner_name, survey_number, area_in_acres, application_date, latitude, longitude, remarks, total_plots) VALUES
('L001', 'Green Valley Layout', 'Approved', 'Rajesh Kumar', 'Sy.No. 45, Nellore', 2.5, '2023-01-15', 14.5234, 79.9876, 'All approvals completed', 150),
('L002', 'Sunrise Enclave', 'Pending', 'Sita Devi', 'Sy.No. 78, Kavali', 1.8, '2023-03-20', 14.9234, 79.8765, 'Awaiting NOC clearance', 120),
('L003', 'Metro Heights', 'Under Review', 'Anil Reddy', 'Sy.No. 92, Gudur', 3.2, '2023-02-10', 14.1456, 79.8432, 'Technical review in progress', 200),
('L004', 'Palm Gardens', 'Rejected', 'Venkat Rao', 'Sy.No. 156, Atmakur', 1.2, '2022-12-05', 14.2345, 79.7654, 'Violated setback norms', 80),
('L005', 'Royal Apartments', 'Unauthorized', 'Unknown', 'Sy.No. 203, Nellore', 0.8, '2023-04-01', 14.4567, 79.9123, 'Construction without approval', 60),
('L006', 'Tech City Phase 1', 'Approved', 'IT Corporation Ltd', 'Sy.No. 301-305, Nellore', 15.0, '2022-08-15', 14.4789, 79.9456, 'Special Economic Zone', 500),
('L007', 'Farmers Colony', 'Pending', 'Agricultural Society', 'Sy.No. 87, Sullurpeta', 4.5, '2023-05-10', 13.7654, 79.8901, 'Waiting for environmental clearance', 300),
('L008', 'Coastal View Villas', 'Under Review', 'Coastal Developers', 'Sy.No. 45, Kavali', 2.8, '2023-06-01', 14.9123, 79.8567, 'CRZ clearance verification', 180);

-- Sample court cases
INSERT INTO court_cases (case_id, case_title, case_type, status, filing_date, hearing_date, description, related_layout_id) VALUES
('C001', 'Land Dispute - Green Valley', 'Civil', 'Active', '2023-02-15', '2023-08-20', 'Boundary dispute resolution', 'L001'),
('C002', 'Unauthorized Construction', 'Criminal', 'Pending', '2023-04-10', NULL, 'Construction without proper approvals', 'L005'),
('C003', 'NOC Violation Case', 'Civil', 'Closed', '2022-11-05', '2023-01-15', 'Violation of No Objection Certificate terms', 'L004'),
('C004', 'Environmental Violation', 'Civil', 'Active', '2023-05-20', '2023-12-01', 'Violation of environmental norms', 'L007');