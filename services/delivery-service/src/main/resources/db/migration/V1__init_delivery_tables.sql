CREATE TABLE riders(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    rider_status VARCHAR(19) NOT NULL CHECK ( rider_status in ('ONLINE', 'OFFLINE', 'BUSY')),
    rating DECIMAL(3,2) DEFAULT 0.0 NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_riders PRIMARY KEY (id)
);


CREATE TABLE deliveries(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL UNIQUE,
    rider_id UUID,
    delivery_status VARCHAR(15) NOT NULL CHECK (delivery_status in ('ASSIGNED', 'ACCEPTED', 'PICKED_UP', 'DELIVERED',
                                                   'CANCELLED', 'DECLINED')),
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    assigned_at TIMESTAMP,
    completed_at TIMESTAMP,

    CONSTRAINT pk_deliveries PRIMARY KEY (id),
    CONSTRAINT fk_riders FOREIGN KEY (rider_id) REFERENCES riders(id)
);