-- Core identity and group-membership model.
-- updated_at automation and cross-row business invariants are added later.

CREATE TABLE users (
    id uuid PRIMARY KEY,
    email citext NOT NULL,
    password_hash varchar(255) NOT NULL,
    system_role user_system_role NOT NULL DEFAULT 'USER',
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT uq_users_email UNIQUE (email)
);

CREATE TABLE groups (
    id uuid PRIMARY KEY,
    name varchar(100) NOT NULL,
    currency_code varchar(3) NOT NULL,
    status group_status NOT NULL DEFAULT 'ACTIVE',
    archived_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_groups_currency_code
        FOREIGN KEY (currency_code)
        REFERENCES currency_codes (code)
        ON DELETE RESTRICT,

    CONSTRAINT uq_groups_id_currency
        UNIQUE (id, currency_code),

    CONSTRAINT chk_groups_currency_code_format
        CHECK (currency_code ~ '^[A-Z]{3}$'),

    CONSTRAINT chk_groups_lifecycle
        CHECK (
            (status = 'ACTIVE' AND archived_at IS NULL)
            OR
            (status = 'ARCHIVED' AND archived_at IS NOT NULL)
        )
);

CREATE TABLE memberships (
    id uuid PRIMARY KEY,
    user_id uuid NOT NULL,
    group_id uuid NOT NULL,
    role membership_role NOT NULL DEFAULT 'MEMBER',
    status membership_status NOT NULL DEFAULT 'ACTIVE',
    joined_at timestamptz NOT NULL DEFAULT now(),
    ended_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_memberships_user
        FOREIGN KEY (user_id)
        REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_memberships_group
        FOREIGN KEY (group_id)
        REFERENCES groups (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_memberships_id_group
        UNIQUE (id, group_id),

    CONSTRAINT chk_memberships_lifecycle
        CHECK (
            (status = 'ACTIVE' AND ended_at IS NULL)
            OR
            (status IN ('LEFT', 'REMOVED') AND ended_at IS NOT NULL)
        )
);

CREATE INDEX idx_memberships_group_status
    ON memberships (group_id, status);

CREATE INDEX idx_memberships_user_status
    ON memberships (user_id, status);

CREATE INDEX idx_memberships_group_role_status
    ON memberships (group_id, role, status);

CREATE UNIQUE INDEX uq_memberships_user_group_active
    ON memberships (user_id, group_id)
    WHERE status = 'ACTIVE';