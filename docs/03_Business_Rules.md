# 03. Business Rules

---

## Purpose

This document defines the business constraints that govern the behavior of SmartShareExpenseHub.

Business Rules are implementation-independent and must be respected by every layer of the system, including Backend services, Database constraints, APIs, and Frontend validation.

---

## Scope

This document covers the following business domains:

- User Management
- Group Management
- Membership
- Expense Management
- Settlement
- Debt Management
- Payment
- Data Lifecycle

---

# Group Management Rules

---

## BR-001 — Every Group must have at least one active Owner.

### Description

A Group cannot exist without an active Owner.

If the last remaining Owner wants to leave the Group, ownership must first be transferred to another Member.

### Reason

Prevent orphan Groups that have no administrator.

---

## BR-002 — Multiple Owners are supported.

### Description

A Group may have one or more Owners.

All Owners have identical permissions in Version 1.

### Reason

Many real-world groups have multiple organizers.

---

## BR-003 — A User may belong to multiple Groups.

### Description

There is no restriction on the number of Groups a User may join.

A User may have different roles in different Groups.

---

## BR-004 — A Member cannot leave a Group while being the only active Owner.

### Description

The system must reject Leave Group requests if the Member is the last remaining Owner.

Ownership transfer is required first.

---

## BR-005 — Members may be removed only when they have no outstanding balance.

### Description

Removing a Member is allowed only if:

- Total Debt = 0
- Total Credit = 0

### Exception

Owners may perform Force Removal.

Historical financial records must remain unchanged. Force Removal does not cancel, transfer, or modify existing Debts, Payments, Expenses, or Settlements.

A former Membership remains a valid financial party for its existing Debt records until all related Debts are fully settled.

---

## BR-006 — Leaving or removing a Member never deletes historical records.

### Description

Historical Expenses, Payments, Settlements, and Activity Logs must remain permanently.

Former Members continue appearing in historical reports.

---

## BR-007 — Archived Groups become read-only.

### Description

Archived Groups cannot:

- Create Expenses
- Invite Members
- Generate Settlements
- Modify Group information

Historical information remains available.

---

## BR-008 — A Member cannot remove themselves.

### Description

Self-removal is not allowed.

Owners who wish to exit must use Leave Group.

---

## BR-009 — When the last Member leaves a Group, the Group is automatically archived.

### Description

Groups with zero active Members must never be hard deleted.

The system archives the Group while preserving all historical financial data.

---

# Expense Management Rules

---

## BR-010 — Every Expense belongs to exactly one Group.

### Description

An Expense cannot exist independently.

It must always be associated with one Group.

---

## BR-011 — Every Expense must have exactly one payer.

### Description

An Expense always records one Member who initially paid the money.

Other Members participate only in expense sharing.

---

## BR-012 — Expenses reference Membership instead of User.

### Description

Financial records reference Membership to preserve historical context even if a User leaves or rejoins the Group.

### Reason

Membership represents a user's participation during a specific period.

---

## BR-013 — Expenses become immutable after Settlement.

### Description

Once an Expense is included in a generated Settlement, it becomes read-only.

The following operations are prohibited:

- Edit
- Delete
- Change payer
- Change participants
- Change split method
- Change amount

### Reason

Protect financial integrity.

Prevent disputes.

---

## BR-014 — Every Expense must have at least one participant.

### Description

An Expense without participants is invalid.

The payer may also be one of the participants.

---

## BR-014A — Deleting an Expense uses soft deletion.

### Description

An Expense may be deleted only while its status is Active.

The system must change the Expense status to Deleted instead of physically deleting the record.

A Deleted Expense must not be included in Settlement generation and cannot be edited or restored.

### Reason

Soft deletion preserves financial history and auditability while preventing deleted Expenses from affecting future financial calculations.

---

## BR-014B — Expense Participant allocations must equal the Expense amount.

### Description

Every active Expense must have at least one ExpenseParticipant.

A Membership may appear at most once within the same Expense.

Every ExpenseParticipant must have an allocated amount greater than zero.

The sum of all ExpenseParticipant allocated amounts must equal the Expense amount exactly.

For Equal Split:

- The system calculates an equal raw allocation for every participant.
- The system rounds allocations down to four decimal places.
- The remaining amount is distributed using the largest remainder method.
- If two or more participants have the same remainder, the system resolves the tie using Membership ID in ascending order.

For Percentage Split:

- Every percentage must be greater than zero.
- The sum of all participant percentages must equal exactly 100.0000.
- The system calculates allocations from the submitted percentages.
- The system rounds allocations down to four decimal places.
- The remaining amount is distributed using the largest remainder method.
- If two or more participants have the same remainder, the system resolves the tie using Membership ID in ascending order.

For Custom Amount Split:

- Every custom allocated amount must be greater than zero.
- The sum of all custom allocated amounts must equal the Expense amount exactly.
- The system must reject the Expense if the total differs from the Expense amount.

### Reason

Every Expense must produce deterministic and internally consistent financial allocations.

The allocation total must always equal the Expense amount to prevent incorrect balances, Debts, and Settlement results.

---

# Settlement Rules

---

## BR-015 — Settlement generation freezes all related Expenses atomically.

### Description

Settlement generation must lock every associated Expense within a single database transaction.

Partial generation is not allowed.

---

## BR-016 — Every Settlement generates Debt records.

### Description

Settlement calculates balances and creates Debt records.

Settlement does not create Payment records.

---

## BR-017 — Debt and Payment represent different business concepts.

### Description

Debt represents financial obligation.

Payment represents an actual money transfer.

A Debt exists before a Payment.

---

## BR-018 — Payments can only be recorded for existing Debts.

### Description

Every Payment must reference an existing Debt.

---

# Data Integrity Rules

---

## BR-019 — Historical financial records are never physically deleted.

### Description

Expenses, Payments, Settlements, and Debts must never be hard deleted.

Soft deletion or archival should be used when necessary.

---

## BR-020 — Every business operation affecting financial data must preserve consistency.

### Description

Financial operations must never leave the system in a partially completed state.

Operations involving multiple records must execute within a database transaction.

---

---

# Payment Rules

---

## BR-021 — A Debt may be settled through multiple Payments.

### Description

A Debt may be paid partially through multiple Payment records until its outstanding balance becomes zero.

---

## BR-022 — Payment amount must not exceed the remaining Debt.

### Description

When a Payment is submitted, its amount must not exceed the available payment amount of the associated Debt.

Available payment amount is calculated as:

Outstanding Balance
− Total amount of Pending Payments for the same Debt

When a Payment is confirmed, the system must validate again that the Payment amount does not exceed the current Outstanding Balance.

Payment submission and Payment confirmation must execute inside database transactions to prevent overpayment caused by concurrent requests.

---

## BR-023 — Only the Debtor may submit a Payment.

### Description

Only the Member responsible for a Debt may initiate a Payment.

The Creditor reviews and either confirms or rejects the submitted Payment.

---

## BR-024 — Payment requires review by the Creditor.

### Description

Every submitted Payment starts in Pending status.

Only the Creditor may review a Pending Payment.

The Creditor may either confirm or reject the Payment.

The system must never confirm or reject Payments automatically.

### Reason

Payment confirmation requires explicit acknowledgement by the Member expected to receive the money.

---

## BR-024A — Pending Payments reserve Debt payment capacity.

### Description

A Pending Payment does not reduce the Outstanding Balance of its Debt.

However, the Payment amount must reserve part of the Debt payment capacity.

The total amount of all Pending Payments and Confirmed Payments for the same Debt must never exceed the original Debt amount.

When a Payment is confirmed:

- The system reduces the Debt Outstanding Balance by the Payment amount.
- The system recalculates the Debt status.

When a Payment is rejected:

- The Payment does not affect the Debt Outstanding Balance.
- The reserved payment capacity is released.

### Reason

Pending Payments have not yet been accepted by the Creditor, but they must still prevent multiple concurrent submissions from exceeding the Debt amount.

---

## BR-025 — One Expense belongs to at most one Settlement.

### Description

An Expense may be included in only one generated Settlement.

Once included in a generated Settlement, the Expense must not participate in any other Settlement.

---

## BR-026 — Settlement generation must execute atomically.

### Description

Generating a Settlement must be executed inside a single database transaction.

If any step fails, the entire Settlement generation must be rolled back.

---

## BR-027 — A Group cannot generate multiple Settlements concurrently.

### Description

Only one Settlement generation process may execute within the same Group at any given time.

Concurrent requests must be rejected or synchronized.

---

## BR-028 — Rejoining a Group creates a new Membership.

### Description

When a User rejoins a Group after previously leaving or being removed, the system must create a new Membership.

A Membership with status Left or Removed must never be reactivated.

A User may have at most one active Membership in the same Group at any given time.

### Reason

A Membership represents a specific period of participation in a Group.

Creating a new Membership preserves historical financial context and prevents different participation periods from being mixed together.

---

## BR-029 — A Member cannot voluntarily leave a Group while having an outstanding balance.

### Description

A Member may leave a Group only when:

- Total Debt = 0
- Total Credit = 0

The system must reject a Leave Group request when either value is non-zero.

### Reason

A voluntary exit must not leave unresolved financial obligations or credits inside the Group.

---

## BR-030 — Former Memberships remain valid for outstanding Debt settlement.

### Description

A Membership with status Left or Removed may still be referenced by existing Debt and Payment records.

For a Debt associated with a former Membership:

- The User associated with the debtor Membership may submit Payments.
- The User associated with the creditor Membership may confirm Payments.
- The former Membership must not create new Expenses or participate in new Expenses.

### Reason

Leaving or Force Removal must preserve financial obligations without granting access to new Group activities.

---

## BR-031 — Every Group has exactly one immutable currency.

### Description

A Group must have exactly one currency code using an ISO 4217 alphabetic code.

The currency must be selected when the Group is created and cannot be changed afterward.

All Expenses, Debts, and Payments associated with the Group must use the Group currency.

The system must reject any financial record whose currency differs from the currency of its Group.

### Reason

A Group operates within one consistent monetary unit in Version 1.

This prevents currency conversion, exchange-rate, and rounding complexity while preserving international usability across different Groups.

---

# General Principles

---

## GP-001 — Financial history is immutable.

Money that has already been settled must never be silently modified.

---

## GP-002 — Auditability is mandatory.

Every important business action should be traceable.

Examples include:

- Creating Expenses
- Editing Expenses
- Removing Members
- Ownership Transfer
- Settlement Completion

---

## GP-003 — Business Rules take precedence over user convenience.

Protecting financial correctness is always more important than allowing unrestricted modifications.

---

## Version

Version: 1.0

Status: Draft

Sprint: Sprint 0

Last Updated: 2026
