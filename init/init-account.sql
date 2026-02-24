-- =============================================================
-- init-account.sql
-- Base de datos: account_db
-- Solo tablas, índices y datos iniciales
-- La DB es creada automáticamente por Docker (POSTGRES_DB)
-- =============================================================

CREATE TABLE IF NOT EXISTS client_ref (
    id              BIGSERIAL    PRIMARY KEY,
    client_id       VARCHAR(50)  NOT NULL UNIQUE,
    name            VARCHAR(100) NOT NULL,
    identification  VARCHAR(20)  NOT NULL,
    status          BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMP    NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(20)  NOT NULL
    );

CREATE INDEX IF NOT EXISTS idx_client_ref_client_id ON client_ref(client_id);

-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS account (
    id                BIGSERIAL     PRIMARY KEY,
    account_number    VARCHAR(20)   NOT NULL UNIQUE,
    account_type      VARCHAR(20)   NOT NULL CHECK (account_type IN ('SAVINGS', 'CHECKING')),
    initial_balance   NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    available_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status            BOOLEAN       NOT NULL DEFAULT TRUE,
    client_id         VARCHAR(50)   NOT NULL,
    created_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    created_by        VARCHAR(20)   NOT NULL,
    updated_at        TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by        VARCHAR(20),

    CONSTRAINT fk_account_client FOREIGN KEY (client_id)
    REFERENCES client_ref(client_id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT,

    CONSTRAINT chk_available_balance_positive
    CHECK (available_balance >= 0)
    );

CREATE INDEX IF NOT EXISTS idx_account_number    ON account(account_number);
CREATE INDEX IF NOT EXISTS idx_account_client_id ON account(client_id);
CREATE INDEX IF NOT EXISTS idx_account_status    ON account(status);

-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS movement (
    id              BIGSERIAL     PRIMARY KEY,
    date_movement   TIMESTAMP     NOT NULL DEFAULT NOW(),
    type_movement   VARCHAR(20)   NOT NULL CHECK (type_movement IN ('DEPOSIT', 'WITHDRAWAL')),
    value_mov       NUMERIC(15,2) NOT NULL,
    balance         NUMERIC(15,2) NOT NULL,
    account_id      BIGINT        NOT NULL,
    created_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(20)   NOT NULL,
    updated_at      TIMESTAMP     NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(20),

    CONSTRAINT fk_movement_account FOREIGN KEY (account_id)
    REFERENCES account(id)
    ON UPDATE CASCADE
    ON DELETE RESTRICT
    );

CREATE INDEX IF NOT EXISTS idx_movement_account_id   ON movement(account_id);
CREATE INDEX IF NOT EXISTS idx_movement_date         ON movement(date_movement);
CREATE INDEX IF NOT EXISTS idx_movement_account_date ON movement(account_id, date_movement);

-- =============================================================
-- Datos iniciales
-- =============================================================

INSERT INTO client_ref (client_id, name, identification, status, created_by)
VALUES
    ('joselema',      'Jose Lema',           '1001234567', TRUE, 'system'),
    ('marianelamont', 'Marianela Montalvo',   '1002345678', TRUE, 'system'),
    ('juanosorio',    'Juan Osorio',          '1003456789', TRUE, 'system')
    ON CONFLICT (client_id) DO NOTHING;

INSERT INTO account (account_number, account_type, initial_balance, available_balance, status, client_id, created_by)
VALUES
    ('478758', 'SAVINGS',    2000.00, 2000.00, TRUE, 'joselema',      'system'),
    ('225487', 'CHECKING',   100.00,  100.00, TRUE, 'marianelamont', 'system'),
    ('495878', 'SAVINGS',       0.00,    0.00, TRUE, 'juanosorio',    'system'),
    ('496825', 'SAVINGS',     540.00,  540.00, TRUE, 'marianelamont', 'system'),
    ('585545', 'CHECKING',  1000.00, 1000.00, TRUE, 'joselema',      'system')
    ON CONFLICT (account_number) DO NOTHING;

INSERT INTO movement (date_movement, type_movement, value_mov, balance, account_id, created_by)
VALUES
    ('2022-02-08 10:00:00', 'WITHDRAWAL', -575.00, 1425.00, (SELECT id FROM account WHERE account_number = '478758'), 'system'),
    ('2022-02-10 11:00:00', 'DEPOSIT',     600.00,  700.00, (SELECT id FROM account WHERE account_number = '225487'), 'system'),
    ('2022-02-09 09:00:00', 'DEPOSIT',     150.00,  150.00, (SELECT id FROM account WHERE account_number = '495878'), 'system'),
    ('2022-02-08 14:00:00', 'WITHDRAWAL', -540.00,    0.00, (SELECT id FROM account WHERE account_number = '496825'), 'system');

UPDATE account SET available_balance = 1425.00 WHERE account_number = '478758';
UPDATE account SET available_balance =  700.00 WHERE account_number = '225487';
UPDATE account SET available_balance =  150.00 WHERE account_number = '495878';
UPDATE account SET available_balance =    0.00 WHERE account_number = '496825';