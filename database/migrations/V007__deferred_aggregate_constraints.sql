-- Deferred cross-row integrity constraints.
-- These checks run at transaction commit, after all related rows are written.

CREATE FUNCTION fn_assert_group_has_active_owner()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_group_id uuid;
BEGIN
    IF TG_TABLE_NAME = 'groups' THEN
        v_group_id := COALESCE(NEW.id, OLD.id);
    ELSE
        v_group_id := COALESCE(NEW.group_id, OLD.group_id);
    END IF;

    -- The parent may already have been deleted in the same transaction.
    PERFORM 1
    FROM groups
    WHERE id = v_group_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN NULL;
    END IF;

    IF NOT EXISTS (
        SELECT 1
        FROM memberships
        WHERE group_id = v_group_id
          AND role = 'OWNER'
          AND status = 'ACTIVE'
    ) THEN
        RAISE EXCEPTION 'Group % must retain at least one ACTIVE OWNER', v_group_id
            USING ERRCODE = '23514';
    END IF;

    RETURN NULL;
END;
$$;

CREATE CONSTRAINT TRIGGER trg_90_groups_require_active_owner
AFTER INSERT OR UPDATE OR DELETE ON groups
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_assert_group_has_active_owner();

CREATE CONSTRAINT TRIGGER trg_90_memberships_require_group_active_owner
AFTER INSERT OR UPDATE OR DELETE ON memberships
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_assert_group_has_active_owner();


CREATE FUNCTION fn_assert_expense_participant_integrity()
RETURNS trigger
LANGUAGE plpgsql
AS $$
DECLARE
    v_expense_id uuid;
    v_expense_amount numeric(19, 4);
    v_split_method split_method;
    v_expense_status expense_status;
    v_participant_count integer;
    v_allocated_total numeric(19, 4);
    v_missing_percentage_count integer;
    v_percentage_total numeric(19, 4);
BEGIN
    IF TG_TABLE_NAME = 'expenses' THEN
        v_expense_id := COALESCE(NEW.id, OLD.id);
    ELSE
        v_expense_id := COALESCE(NEW.expense_id, OLD.expense_id);
    END IF;

    -- Serialize validation for one Expense.
    SELECT amount, split_method, status
    INTO v_expense_amount, v_split_method, v_expense_status
    FROM expenses
    WHERE id = v_expense_id
    FOR UPDATE;

    IF NOT FOUND THEN
        RETURN NULL;
    END IF;

    SELECT
        COUNT(*),
        COALESCE(SUM(allocated_amount), 0),
        COUNT(*) FILTER (WHERE percentage IS NULL),
        COALESCE(SUM(percentage), 0)
    INTO
        v_participant_count,
        v_allocated_total,
        v_missing_percentage_count,
        v_percentage_total
    FROM expense_participants
    WHERE expense_id = v_expense_id;

    IF v_expense_status = 'ACTIVE' AND v_participant_count = 0 THEN
        RAISE EXCEPTION 'Active Expense % must have at least one participant', v_expense_id
            USING ERRCODE = '23514';
    END IF;

    IF v_expense_status = 'ACTIVE'
       AND v_allocated_total <> v_expense_amount THEN
        RAISE EXCEPTION
            'Expense % participant allocation total (%) must equal expense amount (%)',
            v_expense_id, v_allocated_total, v_expense_amount
            USING ERRCODE = '23514';
    END IF;

    IF v_expense_status = 'ACTIVE'
       AND v_split_method = 'PERCENTAGE'
       AND (
           v_missing_percentage_count > 0
           OR v_percentage_total <> 100.0000
       ) THEN
        RAISE EXCEPTION
            'Percentage Expense % must have complete percentages totaling 100.0000',
            v_expense_id
            USING ERRCODE = '23514';
    END IF;

    RETURN NULL;
END;
$$;

CREATE CONSTRAINT TRIGGER trg_90_expenses_participant_integrity
AFTER INSERT OR UPDATE OR DELETE ON expenses
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_assert_expense_participant_integrity();

CREATE CONSTRAINT TRIGGER trg_90_expense_participants_integrity
AFTER INSERT OR UPDATE OR DELETE ON expense_participants
DEFERRABLE INITIALLY DEFERRED
FOR EACH ROW EXECUTE FUNCTION fn_assert_expense_participant_integrity();