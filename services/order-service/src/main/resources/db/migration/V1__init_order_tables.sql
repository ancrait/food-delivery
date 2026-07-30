CREATE TABLE orders(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    restaurant_id UUID NOT NULL,
    status VARCHAR(19) NOT NULL CHECK (status in('CREATED', 'CONFIRMED', 'PREPARING',
                                             'READY', 'DELIVERING', 'DELIVERED', 'CANCELLED' )),
    total_price DECIMAL(10,2) NOT NULL,
    delivery_address VARCHAR(256) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    notes TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    updated_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_orders PRIMARY KEY (id)

);


CREATE TABLE order_items(
    id UUID NOT NULL DEFAULT gen_random_uuid(),
    order_id UUID NOT NULL,
    menu_item_id UUID NOT NULL,
    name VARCHAR(256) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT NOT NULL,

    CONSTRAINT pk_order_items PRIMARY KEY (id),
    CONSTRAINT fk_orders FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE

);