CREATE TABLE rider_locations(
    id UUID NOT NULL DEFAULT pg_catalog.gen_random_uuid(),
    rider_id UUID NOT NULL UNIQUE,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_rider_locations PRIMARY KEY (id)

);