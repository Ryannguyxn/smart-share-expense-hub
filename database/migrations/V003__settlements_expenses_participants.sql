-- Settlement snapshots and expense allocation data.
-- Aggregate-level validation is added later with transaction/trigger logic.

CREATE TABLE settlements (
    id uuid PRIMARY KEY,
    group_id uuid NOT NULL,
    status settlement_status NOT NULL DEFAULT 'GENERATED',
    generated_at timestamptz NOT NULL DEFAULT now(),
    completed_at timestamptz,

    CONSTRAINT fk_settlements_group
        FOREIGN KEY (group_id)
        REFERENCES groups (id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_settlements_id_group
        UNIQUE (id, group_id),

    CONSTRAINT chk_settlements_lifecycle
        CHECK (
            (status = 'GENERATED' AND completed_at IS NULL)
            OR
            (status = 'COMPLETED' AND completed_at IS NOT NULL)
        )
);

CREATE INDEX idx_settlements_group_status_generated_at
    ON settlements (group_id, status, generated_at);

CREATE TABLE expenses (
    id uuid PRIMARY KEY,
    group_id uuid NOT NULL,
    payer_membership_id uuid NOT NULL,
    description varchar(255) NOT NULL,
    amount numeric(19, 4) NOT NULL,
    currency_code varchar(3) NOT NULL,
    expense_date date NOT NULL DEFAULT current_date,
    split_method split_method NOT NULL,
    status expense_status NOT NULL DEFAULT 'ACTIVE',
    settlement_id uuid,
    deleted_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_expenses_group_currency
        FOREIGN KEY (group_id, currency_code)
        REFERENCES groups (id, currency_code)
        ON DELETE RESTRICT,

    CONSTRAINT fk_expenses_payer_membership
        FOREIGN KEY (payer_membership_id, group_id)
        REFERENCES memberships (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_expenses_settlement
        FOREIGN KEY (settlement_id, group_id)
        REFERENCES settlements (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_expenses_id_group
        UNIQUE (id, group_id),

    CONSTRAINT chk_expenses_amount_positive
        CHECK (amount > 0),

    CONSTRAINT chk_expenses_lifecycle
        CHECK (
            (status = 'ACTIVE'
                AND deleted_at IS NULL
                AND settlement_id IS NULL)
            OR
            (status = 'DELETED'
                AND deleted_at IS NOT NULL
                AND settlement_id IS NULL)
            OR
            (status = 'SETTLED'
                AND deleted_at IS NULL
                AND settlement_id IS NOT NULL)
        )
);

CREATE INDEX idx_expenses_group_status_date
    ON expenses (group_id, status, expense_date);

CREATE INDEX idx_expenses_settlement
    ON expenses (settlement_id);

CREATE TABLE expense_participants (
    id uuid PRIMARY KEY,
    expense_id uuid NOT NULL,
    membership_id uuid NOT NULL,
    group_id uuid NOT NULL,
    allocated_amount numeric(19, 4) NOT NULL,
    percentage numeric(7, 4),
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_expense_participants_expense
        FOREIGN KEY (expense_id, group_id)
        REFERENCES expenses (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_expense_participants_membership
        FOREIGN KEY (membership_id, group_id)
        REFERENCES memberships (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT uq_expense_participants_expense_membership
        UNIQUE (expense_id, membership_id),

    CONSTRAINT chk_expense_participants_allocated_amount_positive
        CHECK (allocated_amount > 0),

    CONSTRAINT chk_expense_participants_percentage_range
        CHECK (
            percentage IS NULL
            OR (percentage > 0 AND percentage <= 100.0000)
        )
);

CREATE INDEX idx_expense_participants_membership
    ON expense_participants (membership_id);