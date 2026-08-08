-- Debt settlement obligations, payment review flow, and immutable audit records.
-- Transactional payment reservation and immutability triggers are added later.

CREATE TABLE debts (
    id uuid PRIMARY KEY,
    settlement_id uuid NOT NULL,
    group_id uuid NOT NULL,
    debtor_membership_id uuid NOT NULL,
    creditor_membership_id uuid NOT NULL,
    original_amount numeric(19, 4) NOT NULL,
    outstanding_amount numeric(19, 4) NOT NULL,
    currency_code varchar(3) NOT NULL,
    status debt_status NOT NULL DEFAULT 'OUTSTANDING',
    paid_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_debts_settlement
        FOREIGN KEY (settlement_id, group_id)
        REFERENCES settlements (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_debts_debtor_membership
        FOREIGN KEY (debtor_membership_id, group_id)
        REFERENCES memberships (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_debts_creditor_membership
        FOREIGN KEY (creditor_membership_id, group_id)
        REFERENCES memberships (id, group_id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_debts_group_currency
        FOREIGN KEY (group_id, currency_code)
        REFERENCES groups (id, currency_code)
        ON DELETE RESTRICT,

    CONSTRAINT uq_debts_payment_integrity
        UNIQUE (id, group_id, debtor_membership_id, currency_code),

    CONSTRAINT chk_debts_original_amount_positive
        CHECK (original_amount > 0),

    CONSTRAINT chk_debts_outstanding_amount_range
        CHECK (
            outstanding_amount >= 0
            AND outstanding_amount <= original_amount
        ),

    CONSTRAINT chk_debts_distinct_parties
        CHECK (debtor_membership_id <> creditor_membership_id),

    CONSTRAINT chk_debts_lifecycle
        CHECK (
            (status = 'OUTSTANDING'
                AND outstanding_amount > 0
                AND paid_at IS NULL)
            OR
            (status = 'PAID'
                AND outstanding_amount = 0
                AND paid_at IS NOT NULL)
        )
);

CREATE INDEX idx_debts_settlement
    ON debts (settlement_id);

CREATE INDEX idx_debts_debtor_status
    ON debts (debtor_membership_id, status);

CREATE INDEX idx_debts_creditor_status
    ON debts (creditor_membership_id, status);

CREATE INDEX idx_debts_group_status
    ON debts (group_id, status);

CREATE TABLE payments (
    id uuid PRIMARY KEY,
    debt_id uuid NOT NULL,
    group_id uuid NOT NULL,
    debtor_membership_id uuid NOT NULL,
    amount numeric(19, 4) NOT NULL,
    currency_code varchar(3) NOT NULL,
    status payment_status NOT NULL DEFAULT 'PENDING',
    submitted_at timestamptz NOT NULL DEFAULT now(),
    reviewed_at timestamptz,
    created_at timestamptz NOT NULL DEFAULT now(),
    updated_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_payments_debt
        FOREIGN KEY (debt_id, group_id, debtor_membership_id, currency_code)
        REFERENCES debts (id, group_id, debtor_membership_id, currency_code)
        ON DELETE RESTRICT,

    CONSTRAINT chk_payments_amount_positive
        CHECK (amount > 0),

    CONSTRAINT chk_payments_lifecycle
        CHECK (
            (status = 'PENDING' AND reviewed_at IS NULL)
            OR
            (status IN ('CONFIRMED', 'REJECTED') AND reviewed_at IS NOT NULL)
        )
);

CREATE INDEX idx_payments_debt_status
    ON payments (debt_id, status);

CREATE INDEX idx_payments_debtor_status
    ON payments (debtor_membership_id, status);

CREATE INDEX idx_payments_group_status
    ON payments (group_id, status);

CREATE TABLE activity_logs (
    id uuid PRIMARY KEY,
    group_id uuid NOT NULL,
    actor_user_id uuid NOT NULL,
    actor_membership_id uuid,
    action_type varchar(100) NOT NULL,
    entity_type varchar(100) NOT NULL,
    entity_id uuid,
    metadata jsonb NOT NULL DEFAULT '{}'::jsonb,
    occurred_at timestamptz NOT NULL DEFAULT now(),

    CONSTRAINT fk_activity_logs_group
        FOREIGN KEY (group_id)
        REFERENCES groups (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_activity_logs_actor_user
        FOREIGN KEY (actor_user_id)
        REFERENCES users (id)
        ON DELETE RESTRICT,

    CONSTRAINT fk_activity_logs_actor_membership
        FOREIGN KEY (actor_membership_id, group_id)
        REFERENCES memberships (id, group_id)
        ON DELETE RESTRICT
);

CREATE INDEX idx_activity_logs_group_occurred_at
    ON activity_logs (group_id, occurred_at);

CREATE INDEX idx_activity_logs_actor_user_occurred_at
    ON activity_logs (actor_user_id, occurred_at);

CREATE INDEX idx_activity_logs_entity_occurred_at
    ON activity_logs (entity_type, entity_id, occurred_at);