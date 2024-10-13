
CREATE TABLE profiles (
    id SERIAL PRIMARY KEY,
	uuid UUID NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    phone_number VARCHAR(20),
	organisation_id VARCHAR(20),
    date_of_birth DATE,
    gender VARCHAR(20),
    bio TEXT,
	status boolean default true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_profiles_id ON profiles(id);

CREATE TABLE addresses (
     id SERIAL PRIMARY KEY,
   	uuid UUID,
	organisation_id VARCHAR(20),
	type VARCHAR(100) NOT NULL,
    address_line1 VARCHAR(100) NOT NULL,
    address_line2 VARCHAR(100),
	street VARCHAR(50) NOT NULL,
    city VARCHAR(50) NOT NULL,
    state VARCHAR(50),
    zip VARCHAR(20),
    country VARCHAR(50) NOT NULL,
	status boolean default true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
CREATE INDEX idx_addresses_id ON addresses(id);