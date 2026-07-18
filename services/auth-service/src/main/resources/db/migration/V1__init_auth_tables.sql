CREATE TABLE users (
    id          UUID NOT NULL DEFAULT gen_random_uuid(),
    email       VARCHAR(255) NOT NULL,
    password    VARCHAR(255) NOT NULL,
    first_name  VARCHAR(100),
    last_name   VARCHAR(100),
    phone       VARCHAR(20),
    is_enabled  BOOLEAN NOT NULL DEFAULT false,
    created_at  TIMESTAMP NOT NULL DEFAULT now(),
    updated_at  TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_users PRIMARY KEY (id),
    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE roles (
    id   UUID NOT NULL DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL,

    CONSTRAINT pk_roles PRIMARY KEY (id),
    CONSTRAINT uq_roles_name UNIQUE (name)
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL,
    role_id UUID NOT NULL,

    CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

CREATE TABLE refresh_tokens (
    id         UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL,
    token      VARCHAR(512) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_refresh_tokens PRIMARY KEY (id),
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE verification_tokens (
    id         UUID NOT NULL DEFAULT gen_random_uuid(),
    user_id    UUID NOT NULL,
    token      VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),

    CONSTRAINT pk_verification_tokens PRIMARY KEY (id),
    CONSTRAINT fk_verification_tokens_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uq_verification_tokens_token UNIQUE (token)
);

INSERT INTO roles (name) VALUES
    ('ROLE_CUSTOMER'),
    ('ROLE_RESTAURANT'),
    ('ROLE_RIDER'),
    ('ROLE_ADMIN');
