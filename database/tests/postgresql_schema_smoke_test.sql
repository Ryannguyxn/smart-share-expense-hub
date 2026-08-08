\set ON_ERROR_STOP on

BEGIN;

INSERT INTO users (id, email, password_hash) VALUES
    ('00000000-0000-0000-0000-000000000001', 'owner@test.local', 'test-hash'),
    ('00000000-0000-0000-0000-000000000002', 'member@test.local', 'test-hash');

INSERT INTO groups (id, name, currency_code) VALUES
    ('00000000-0000-0000-0000-000000000100', 'Schema Smoke Test', 'VND');

INSERT INTO memberships (id, user_id, group_id, role) VALUES
    ('00000000-0000-0000-0000-000000000101', '00000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000100', 'OWNER'),
    ('00000000-0000-0000-0000-000000000102', '00000000-0000-0000-0000-000000000002', '00000000-0000-0000-0000-000000000100', 'MEMBER');

INSERT INTO settlements (id, group_id) VALUES
    ('00000000-0000-0000-0000-000000000200', '00000000-0000-0000-0000-000000000100');

INSERT INTO expenses
    (id, group_id, payer_membership_id, description, amount, currency_code, split_method)
VALUES
    ('00000000-0000-0000-0000-000000000300',
     '00000000-0000-0000-0000-000000000100',
     '00000000-0000-0000-0000-000000000101',
     'Schema test expense', 100.0000, 'VND', 'EQUAL');

INSERT INTO expense_participants
    (id, expense_id, membership_id, group_id, allocated_amount)
VALUES
    ('00000000-0000-0000-0000-000000000301',
     '00000000-0000-0000-0000-000000000300',
     '00000000-0000-0000-0000-000000000101',
     '00000000-0000-0000-0000-000000000100', 50.0000),
    ('00000000-0000-0000-0000-000000000302',
     '00000000-0000-0000-0000-000000000300',
     '00000000-0000-0000-0000-000000000102',
     '00000000-0000-0000-0000-000000000100', 50.0000);

INSERT INTO debts
    (id, settlement_id, group_id, debtor_membership_id, creditor_membership_id,
     original_amount, outstanding_amount, currency_code)
VALUES
    ('00000000-0000-0000-0000-000000000400',
     '00000000-0000-0000-0000-000000000200',
     '00000000-0000-0000-0000-000000000100',
     '00000000-0000-0000-0000-000000000102',
     '00000000-0000-0000-0000-000000000101',
     100.0000, 100.0000, 'VND');

INSERT INTO payments
    (id, debt_id, group_id, debtor_membership_id, amount, currency_code)
VALUES
    ('00000000-0000-0000-0000-000000000500',
     '00000000-0000-0000-0000-000000000400',
     '00000000-0000-0000-0000-000000000100',
     '00000000-0000-0000-0000-000000000102',
     100.0000, 'VND');

INSERT INTO activity_logs
    (id, group_id, actor_user_id, actor_membership_id, action_type, entity_type)
VALUES
    ('00000000-0000-0000-0000-000000000600',
     '00000000-0000-0000-0000-000000000100',
     '00000000-0000-0000-0000-000000000001',
     '00000000-0000-0000-0000-000000000101',
     'SCHEMA_TEST', 'GROUP');

SET CONSTRAINTS ALL IMMEDIATE;

DO $$
BEGIN
    BEGIN
        INSERT INTO memberships (id, user_id, group_id)
        VALUES ('00000000-0000-0000-0000-000000000103',
                '00000000-0000-0000-0000-000000000001',
                '00000000-0000-0000-0000-000000000100');
        RAISE EXCEPTION 'Expected active-membership uniqueness violation';
    EXCEPTION WHEN unique_violation THEN
        RAISE NOTICE 'PASS: one active Membership per User and Group';
    END;

    BEGIN
        UPDATE groups SET currency_code = 'USD'
        WHERE id = '00000000-0000-0000-0000-000000000100';
        RAISE EXCEPTION 'Expected Group currency immutability violation';
    EXCEPTION WHEN SQLSTATE '23514' THEN
        RAISE NOTICE 'PASS: Group currency is immutable';
    END;

    BEGIN
        UPDATE debts SET original_amount = 99.0000
        WHERE id = '00000000-0000-0000-0000-000000000400';
        RAISE EXCEPTION 'Expected Debt snapshot immutability violation';
    EXCEPTION WHEN SQLSTATE '23514' THEN
        RAISE NOTICE 'PASS: Debt snapshot is immutable';
    END;

    BEGIN
        UPDATE payments SET amount = 99.0000
        WHERE id = '00000000-0000-0000-0000-000000000500';
        RAISE EXCEPTION 'Expected Payment snapshot immutability violation';
    EXCEPTION WHEN SQLSTATE '23514' THEN
        RAISE NOTICE 'PASS: Payment submission snapshot is immutable';
    END;

    BEGIN
        UPDATE activity_logs SET action_type = 'MUTATED'
        WHERE id = '00000000-0000-0000-0000-000000000600';
        RAISE EXCEPTION 'Expected ActivityLog immutability violation';
    EXCEPTION WHEN SQLSTATE '23514' THEN
        RAISE NOTICE 'PASS: ActivityLog is immutable';
    END;
END;
$$;

ROLLBACK;