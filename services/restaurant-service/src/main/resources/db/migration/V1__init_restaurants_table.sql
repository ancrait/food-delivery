CREATE TABLE restaurants(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(256) NOT NULL,
    description TEXT NOT NULL,
    address VARCHAR(256) NOT NULL,
    phone VARCHAR(256) NOT NULL,
    rating DECIMAL(3,1) NOT NULL DEFAULT 0.0,
    logo_url VARCHAR(256),
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),


    CONSTRAINT pk_restaurants PRIMARY KEY (id)

);

CREATE TABLE categories(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(256) NOT NULL,

    CONSTRAINT pk_categories PRIMARY KEY (id)
);

CREATE TABLE menu_items(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    restaurant_id UUID NOT NULL,
    category_id UUID NOT NULL,
    name VARCHAR(256) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    image_url VARCHAR(256),
    is_available BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_menu_items PRIMARY KEY (id),
    CONSTRAINT fk_restaurants FOREIGN KEY (restaurant_id) REFERENCES restaurants(id) ON DELETE CASCADE,
    CONSTRAINT fk_categories FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE
)