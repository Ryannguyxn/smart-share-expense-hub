# 07. Data Lifecycle

---

## Purpose

This document defines the lifecycle of the core domain entities in SmartShareExpenseHub.

A lifecycle describes how an entity changes over time, including:

- Valid states
- Allowed state transitions
- Transition conditions
- Business restrictions

This document serves as the foundation for:

- Database Design
- Backend Business Logic
- Authorization Rules
- Transaction Management
- Audit Logging

---

# General Principles

## Immutable Financial History

Financial records that have participated in a Settlement shall never be physically deleted.

Historical financial data must remain available for auditing purposes.

---

## Controlled State Transition

Entities may only transition through predefined states.

Direct transitions outside the defined lifecycle are not allowed.

---

## Auditability

Important lifecycle transitions should be recorded in ActivityLog whenever applicable.

---

# Group Lifecycle

## Lifecycle

```text
Active
   │
   ▼
Archived
```

## State Description

### Active

The Group is available for normal operations.

Allowed operations:

- Invite Members
- Remove Members
- Create Expenses
- Generate Settlements
- Record Payments

---

### Archived

The Group becomes read-only.

Restrictions:

- No new Memberships
- No new Expenses
- No new Settlements
- Historical data remains accessible

---

## Allowed Transitions

| From   | To       | Trigger                        |
| ------ | -------- | ------------------------------ |
| Active | Archived | Group Owner archives the Group |

---

# Membership Lifecycle

## Lifecycle

```text
Active
├────────► Left
│
└────────► Removed
```

## State Description

### Active

The User actively participates in the Group.

Allowed operations:

- Create Expenses
- Participate in Expense Splitting
- View Reports
- Record Payments

---

### Left

The Member voluntarily leaves the Group.

Historical financial records remain unchanged.

---

### Removed

The Member is removed by a Group Owner.

Historical financial records remain unchanged.

---

## Allowed Transitions

| From   | To      | Trigger                    |
| ------ | ------- | -------------------------- |
| Active | Left    | Member leaves the Group    |
| Active | Removed | Group Owner removes Member |

---

## Terminal State Rule

Left and Removed are terminal states.

A Membership in either state must never transition back to Active.

If the same User joins the Group again, the system must create a new Membership.

---

# Expense Lifecycle

## Lifecycle

```text
Active
├────────► Settled
│
└────────► Deleted
```

## State Description

### Active

The Expense is available for editing according to Business Rules.

Allowed operations:

- Edit Expense
- Delete Expense
- Update Participants
- Update Split Method

---

### Settled

The Expense has been included in a Settlement.

Restrictions:

- Cannot be edited
- Cannot be deleted
- Financial values become immutable

---

### Deleted

The Expense was soft-deleted before Settlement.

Restrictions:

- Cannot be edited
- Cannot be restored
- Must not be included in Settlement generation
- Historical data remains preserved

---

## Allowed Transitions

| From   | To      | Trigger                                      |
| ------ | ------- | -------------------------------------------- |
| Active | Settled | Settlement is generated                      |
| Active | Deleted | Member deletes the Expense before Settlement |

---

# Settlement Lifecycle

## Lifecycle

```text
Generated
│
▼
Completed
```

## State Description

### Generated

The Settlement has been created.

The system has:

- Calculated balances
- Generated Debt records

Payments may now be recorded.

---

### Completed

All related Debts have been fully settled.

The Settlement becomes final.

---

## Allowed Transitions

| From      | To        | Trigger                  |
| --------- | --------- | ------------------------ |
| Generated | Completed | All Debts are fully paid |

---

# Debt Lifecycle

## Lifecycle

```text
Outstanding
│
▼
Paid
```

## State Description

### Outstanding

The Debt has not yet been fully paid.

---

### Paid

The Debt has been completely settled.

No outstanding balance remains.

---

## Allowed Transitions

| From        | To   | Trigger                          |
| ----------- | ---- | -------------------------------- |
| Outstanding | Paid | Outstanding balance reaches zero |

---

# Payment Lifecycle

## Lifecycle

```text
Pending
   ├────────► Confirmed
   │
   └────────► Rejected
```

## State Description

### Pending

The Payment has been submitted by the Debtor and is awaiting review by the Creditor.

The Payment reserves Debt payment capacity but does not reduce the Debt Outstanding Balance.

---

### Confirmed

The Payment has been accepted by the Creditor.

The related Debt Outstanding Balance is updated.

---

### Rejected

The Payment has been rejected by the Creditor.

The related Debt Outstanding Balance remains unchanged.

## Allowed Transitions

| From    | To        | Trigger                       |
| ------- | --------- | ----------------------------- |
| Pending | Confirmed | Creditor confirms the Payment |
| Pending | Rejected  | Creditor rejects the Payment  |

---

# Lifecycle Summary

| Entity     | Initial State | Final State          |
| ---------- | ------------- | -------------------- |
| Group      | Active        | Archived             |
| Membership | Active        | Left / Removed       |
| Expense    | Active        | Settled / Deleted    |
| Settlement | Generated     | Completed            |
| Debt       | Outstanding   | Paid                 |
| Payment    | Pending       | Confirmed / Rejected |

---

Version: 1.0

Status: Draft

Sprint: Sprint 0
