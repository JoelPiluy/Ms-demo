-- =============================================================
-- init-client.sql
-- Base de datos: client_db
-- Solo tablas, índices y datos iniciales
-- La DB es creada automáticamente por Docker (POSTGRES_DB)
-- =============================================================

CREATE TABLE IF NOT EXISTS client (
                                      id              BIGSERIAL       PRIMARY KEY,
                                      name            VARCHAR(100)    NOT NULL,
    gender          VARCHAR(20)     NOT NULL,
    age             INTEGER         NOT NULL CHECK (age >= 0),
    identification  VARCHAR(20)     NOT NULL UNIQUE,
    address         VARCHAR(200)    NOT NULL,
    phone           VARCHAR(20)     NOT NULL,
    client_id       VARCHAR(50)     NOT NULL UNIQUE,
    password        VARCHAR(255)    NOT NULL,
    status          BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(20)     NOT NULL,
    updated_at      TIMESTAMP       NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(20)
    );

CREATE INDEX IF NOT EXISTS idx_client_client_id      ON client(client_id);
CREATE INDEX IF NOT EXISTS idx_client_identification ON client(identification);
CREATE INDEX IF NOT EXISTS idx_client_status         ON client(status);

-- Datos iniciales
INSERT INTO client (name, gender, age, identification, address, phone, client_id, password, status, created_by)
VALUES
    ('Jose Lema',          'Masculino', 35, '1001234567', 'Otavalo sn y director',     '098254785', 'joselema',      '$2a$10$7QJ8zV3Kx2Ld1mN9Pq0ROeW6sYtUvXwA4bCdEfGhIjKlMnOpQrSt', TRUE, 'system'),
    ('Marianela Montalvo', 'Femenino',  28, '1002345678', 'Amazonas y NNUU',           '097548965', 'marianelamont', '$2a$10$8RK9aW4Ly3Me2nO0Qr1SPfX7tZuVwYxB5cDeGhIjKlMnOpQrSt', TRUE, 'system'),
    ('Juan Osorio',        'Masculino', 42, '1003456789', '13 de junio y equinoccial', '098874587', 'juanosorio',    '$2a$10$9SL0bX5Mz4Nf3oP1Rs2TQgY8uAvWxZyC6dEfGhIjKlMnOpQrSt', TRUE, 'system')
    ON CONFLICT (client_id) DO NOTHING;