CREATE TABLE notifications(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    type VARCHAR(19) NOT NULL CHECK (type in ('EMAIL_VERIFICATION', 'ORDER_STATUS', 'PASSWORD_RESET')),
    recipient VARCHAR(255) NOT NULL,
    subject VARCHAR(255),
    body TEXT NOT NULL,
    status VARCHAR(7) NOT NULL CHECK (status in ('SENT','FAILED')),
    error_message VARCHAR(255),
    send_at TIMESTAMP DEFAULT now(),
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_notifications PRIMARY KEY (id)
);