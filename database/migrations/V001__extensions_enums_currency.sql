-- SmartShareExpenseHub: PostgreSQL foundation.
-- Core domain tables are introduced by later migrations.

CREATE EXTENSION IF NOT EXISTS citext;

CREATE TYPE user_system_role AS ENUM (
    'USER',
    'ADMIN'
);

CREATE TYPE group_status AS ENUM (
    'ACTIVE',
    'ARCHIVED'
);

CREATE TYPE membership_role AS ENUM (
    'OWNER',
    'MEMBER'
);

CREATE TYPE membership_status AS ENUM (
    'ACTIVE',
    'LEFT',
    'REMOVED'
);

CREATE TYPE expense_status AS ENUM (
    'ACTIVE',
    'SETTLED',
    'DELETED'
);

CREATE TYPE split_method AS ENUM (
    'EQUAL',
    'PERCENTAGE',
    'CUSTOM'
);

CREATE TYPE settlement_status AS ENUM (
    'GENERATED',
    'COMPLETED'
);

CREATE TYPE debt_status AS ENUM (
    'OUTSTANDING',
    'PAID'
);

CREATE TYPE payment_status AS ENUM (
    'PENDING',
    'CONFIRMED',
    'REJECTED'
);

CREATE TABLE currency_codes (
    code varchar(3) PRIMARY KEY,
    CONSTRAINT chk_currency_codes_code_format
        CHECK (code ~ '^[A-Z]{3}$')
);

COMMENT ON TABLE currency_codes IS
    'Immutable ISO 4217 alphabetic currency-code reference data. Seeded in a later migration.';