CREATE TABLE users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    booking_count INT NOT NULL,
    vip_level VARCHAR(20) NOT NULL,
    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uk_users_email UNIQUE (email)
);

CREATE TABLE room_types (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    base_price DECIMAL(14,2) NOT NULL,
    max_guests INT NOT NULL,
    description VARCHAR(300) NULL,
    CONSTRAINT pk_room_types PRIMARY KEY (id),
    CONSTRAINT uk_room_types_name UNIQUE (name)
);

CREATE TABLE rooms (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(10) NOT NULL,
    floor_number INT NOT NULL,
    room_type_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT pk_rooms PRIMARY KEY (id),
    CONSTRAINT uk_rooms_code UNIQUE (code),
    CONSTRAINT fk_rooms_room_type FOREIGN KEY (room_type_id) REFERENCES room_types (id)
);

CREATE INDEX idx_rooms_room_type_id ON rooms (room_type_id);

CREATE TABLE bookings (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    room_id BIGINT NOT NULL,
    check_in_date DATE NOT NULL,
    check_out_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    total_amount DECIMAL(14,2) NOT NULL,
    hold_expires_at DATETIME(6) NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_bookings PRIMARY KEY (id),
    CONSTRAINT fk_bookings_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT fk_bookings_room FOREIGN KEY (room_id) REFERENCES rooms (id)
);

CREATE INDEX idx_bookings_user_id ON bookings (user_id);
CREATE INDEX idx_bookings_room_id ON bookings (room_id);
CREATE INDEX idx_bookings_status ON bookings (status);
CREATE INDEX idx_bookings_hold_expires_at ON bookings (hold_expires_at);
CREATE INDEX idx_bookings_date_range ON bookings (room_id, check_in_date, check_out_date);

CREATE TABLE payment_transactions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    booking_id BIGINT NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    transaction_code VARCHAR(40) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_payment_transactions PRIMARY KEY (id),
    CONSTRAINT fk_payment_transactions_booking FOREIGN KEY (booking_id) REFERENCES bookings (id),
    CONSTRAINT uk_payment_transactions_code UNIQUE (transaction_code)
);

CREATE INDEX idx_payment_transactions_booking_id ON payment_transactions (booking_id);

CREATE TABLE reviews (
    id BIGINT NOT NULL AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    rating INT NOT NULL,
    comment VARCHAR(500) NOT NULL,
    created_at DATETIME(6) NOT NULL,
    CONSTRAINT pk_reviews PRIMARY KEY (id),
    CONSTRAINT fk_reviews_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_reviews_user_id ON reviews (user_id);
CREATE INDEX idx_reviews_created_at ON reviews (created_at);

CREATE TABLE password_reset_tokens (
    id BIGINT NOT NULL AUTO_INCREMENT,
    code VARCHAR(8) NOT NULL,
    user_id BIGINT NOT NULL,
    expires_at DATETIME(6) NOT NULL,
    used BIT(1) NOT NULL,
    CONSTRAINT pk_password_reset_tokens PRIMARY KEY (id),
    CONSTRAINT uk_password_reset_tokens_code UNIQUE (code),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens (expires_at);
