CREATE TABLE payments(
    id UUID DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL UNIQUE,
    user_id UUID NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    currency VARCHAR(3) NOT NULL DEFAULT 'UAH',
    paymentStatus VARCHAR(10) NOT NULL CHECK (paymentStatus IN ('PENDING', 'SUCCESS', 'FAILED', 'REFUNDED', 'CANCELLED')),
    stripe_payment_id VARCHAR(255),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_payments PRIMARY KEY (id)

);

