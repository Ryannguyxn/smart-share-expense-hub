-- Row-level integrity guardrails.
-- Cross-row and concurrency-sensitive business rules are introduced separately.

CREATE FUNCTION fn_set_updated_at()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    NEW.updated_at := now();
    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_10_users_set_updated_at
BEFORE UPDATE ON users
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_10_groups_set_updated_at
BEFORE UPDATE ON groups
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_10_memberships_set_updated_at
BEFORE UPDATE ON memberships
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_10_expenses_set_updated_at
BEFORE UPDATE ON expenses
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_10_expense_participants_set_updated_at
BEFORE UPDATE ON expense_participants
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_10_debts_set_updated_at
BEFORE UPDATE ON debts
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();

CREATE TRIGGER trg_10_payments_set_updated_at
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION fn_set_updated_at();


CREATE FUNCTION fn_prevent_currency_code_mutation()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION
        'currency_codes is immutable; add future codes through a reviewed migration'
        USING ERRCODE = '23514';
END;
$$;

CREATE TRIGGER trg_20_currency_codes_immutable
BEFORE UPDATE OR DELETE ON currency_codes
FOR EACH ROW EXECUTE FUNCTION fn_prevent_currency_code_mutation();


CREATE FUNCTION fn_guard_group_update()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.currency_code IS DISTINCT FROM NEW.currency_code THEN
        RAISE EXCEPTION 'A Group currency cannot be changed after creation'
            USING ERRCODE = '23514';
    END IF;

    IF OLD.status = 'ARCHIVED' THEN
        RAISE EXCEPTION 'An archived Group is terminal and cannot be modified'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_20_groups_guard_update
BEFORE UPDATE ON groups
FOR EACH ROW EXECUTE FUNCTION fn_guard_group_update();


CREATE FUNCTION fn_guard_membership_update()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status IN ('LEFT', 'REMOVED') THEN
        RAISE EXCEPTION 'A terminal Membership cannot be modified or reactivated'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_20_memberships_guard_update
BEFORE UPDATE ON memberships
FOR EACH ROW EXECUTE FUNCTION fn_guard_membership_update();


CREATE FUNCTION fn_guard_expense_update()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status IN ('SETTLED', 'DELETED') THEN
        RAISE EXCEPTION 'A terminal Expense cannot be modified or reactivated'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_20_expenses_guard_update
BEFORE UPDATE ON expenses
FOR EACH ROW EXECUTE FUNCTION fn_guard_expense_update();


CREATE FUNCTION fn_guard_settlement_update()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status = 'COMPLETED' THEN
        RAISE EXCEPTION 'A completed Settlement is terminal and cannot be modified'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_20_settlements_guard_update
BEFORE UPDATE ON settlements
FOR EACH ROW EXECUTE FUNCTION fn_guard_settlement_update();


CREATE FUNCTION fn_guard_debt_update()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status = 'PAID' THEN
        RAISE EXCEPTION 'A paid Debt is terminal and cannot be modified'
            USING ERRCODE = '23514';
    END IF;

    IF OLD.id IS DISTINCT FROM NEW.id
        OR OLD.settlement_id IS DISTINCT FROM NEW.settlement_id
        OR OLD.group_id IS DISTINCT FROM NEW.group_id
        OR OLD.debtor_membership_id IS DISTINCT FROM NEW.debtor_membership_id
        OR OLD.creditor_membership_id IS DISTINCT FROM NEW.creditor_membership_id
        OR OLD.original_amount IS DISTINCT FROM NEW.original_amount
        OR OLD.currency_code IS DISTINCT FROM NEW.currency_code
        OR OLD.created_at IS DISTINCT FROM NEW.created_at THEN
        RAISE EXCEPTION 'Debt snapshot fields are immutable after creation'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_20_debts_guard_update
BEFORE UPDATE ON debts
FOR EACH ROW EXECUTE FUNCTION fn_guard_debt_update();


CREATE FUNCTION fn_guard_payment_update()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    IF OLD.status IN ('CONFIRMED', 'REJECTED') THEN
        RAISE EXCEPTION 'A reviewed Payment is terminal and cannot be modified'
            USING ERRCODE = '23514';
    END IF;

    IF OLD.id IS DISTINCT FROM NEW.id
        OR OLD.debt_id IS DISTINCT FROM NEW.debt_id
        OR OLD.group_id IS DISTINCT FROM NEW.group_id
        OR OLD.debtor_membership_id IS DISTINCT FROM NEW.debtor_membership_id
        OR OLD.amount IS DISTINCT FROM NEW.amount
        OR OLD.currency_code IS DISTINCT FROM NEW.currency_code
        OR OLD.submitted_at IS DISTINCT FROM NEW.submitted_at
        OR OLD.created_at IS DISTINCT FROM NEW.created_at THEN
        RAISE EXCEPTION 'Payment submission fields are immutable after creation'
            USING ERRCODE = '23514';
    END IF;

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_20_payments_guard_update
BEFORE UPDATE ON payments
FOR EACH ROW EXECUTE FUNCTION fn_guard_payment_update();


CREATE FUNCTION fn_prevent_activity_log_mutation()
RETURNS trigger
LANGUAGE plpgsql
AS $$
BEGIN
    RAISE EXCEPTION 'ActivityLog is immutable and cannot be updated or deleted'
        USING ERRCODE = '23514';
END;
$$;

CREATE TRIGGER trg_20_activity_logs_immutable
BEFORE UPDATE OR DELETE ON activity_logs
FOR EACH ROW EXECUTE FUNCTION fn_prevent_activity_log_mutation();