# 05. Use Cases

---

## Purpose

This document defines the functional interactions between users and SmartShareExpenseHub.

Use Cases describe what the system should provide from the user's perspective.

This document focuses on business behavior and user interaction before implementation.

---

# Actors

---

## Actor 01 — System Administrator

### Description

A system-level administrator responsible for managing and monitoring the platform.

### Responsibilities

- Manage users
- Manage system categories
- Moderate reported content
- View system statistics
- Configure global settings

---

## Actor 02 — User

### Description

A registered account in the system.

A User can participate in multiple Groups through Membership.

### Responsibilities

- Manage personal account
- Join Groups
- Participate in shared expenses
- Manage financial activities inside Groups

---

## Actor 03 — Group Owner

### Description

A Membership with administrative permissions inside a Group.

A Group may have multiple Owners.

### Responsibilities

- Manage Group members
- Invite members
- Remove members
- Transfer ownership
- Archive Group
- Generate Settlement

---

## Actor 04 — Group Member

### Description

A regular participant inside a Group.

### Responsibilities

- Create Expenses
- Participate in Expense sharing
- View financial reports
- Record Payments

---

# Use Cases

---

# User Management

---

## UC-001 — Register Account

### Actor

User

### Description

User creates a new account in the system.

### Business Flow

1. User provides registration information.
2. System validates account information.
3. System creates a new User.
4. User can authenticate and access the platform.

---

## UC-002 — Authenticate User

### Actor

User

### Description

User logs into the system.

### Business Flow

1. User provides authentication credentials.
2. System verifies credentials.
3. System creates authentication session using JWT.
4. User accesses authorized features.

---

## UC-003 — Manage Profile

### Actor

User

### Description

User updates personal information.

### Business Flow

1. User opens profile settings.
2. User modifies allowed information.
3. System validates changes.
4. System updates User information.

---

# Group Management

---

## UC-004 — Create Group

### Actor

User

### Description

User creates a new Group for shared expense management.

### Business Flow

1. User provides Group information, including its currency.
2. System validates the Group information and currency code.
3. System creates a new Group and assigns the validated currency permanently.
4. Creator becomes an Owner Membership.
5. Group becomes available for collaboration.

---

## UC-005 — Invite Member

### Actor

Group Owner

### Description

Owner invites another User to join a Group.

### Business Flow

1. Owner sends invitation.
2. System creates invitation process.
3. User accepts invitation.
4. System creates Membership.

---

## UC-006 — Manage Group Members

### Actor

Group Owner

### Description

Owner manages Members inside a Group.

Includes:

- Remove Member
- Transfer Ownership

### Business Flow

1. Owner selects a Member.
2. System checks Business Rules.
3. System performs allowed operation.
4. Historical records remain unchanged.

---

## UC-007 — Leave Group

### Actor

Group Member

### Description

A Member leaves a Group.

### Business Flow

1. Member requests leaving Group.
2. System verifies that the Member is not the only active Owner and has no outstanding Debt or Credit balance.
3. If all Business Rules are satisfied, the system changes the Membership status to Left.
4. Historical financial records remain preserved.

---

## UC-008 — Archive Group

### Actor

Group Owner

### Description

Owner archives a Group.

### Business Flow

1. Owner requests archive.
2. System validates permission.
3. Group status changes to Archived.
4. Group becomes read-only.

---

# Expense Management

---

## UC-009 — Create Expense

### Actor

Group Member

### Description

Member creates a shared Expense inside a Group.

### Business Flow

1. Member enters expense information.
2. Member selects payer.
3. Member assigns Expense Participants.
4. System validates expense data.
5. System creates Expense.

---

## UC-010 — Split Expense

### Actor

Group Member

### Description

Member defines how an Expense is divided.

### Supported Methods

- Equal Split
- Percentage Split
- Custom Amount Split

### Business Flow

1. User selects a Split Method.
2. User selects one or more Expense Participants.
3. For Percentage Split, User provides a percentage for each participant.
4. For Custom Amount Split, User provides an allocated amount for each participant.
5. For Equal Split, the system calculates allocations equally among all participants.
6. System validates that every selected Membership appears only once and every submitted value is greater than zero.
7. System calculates or validates allocated amounts according to BR-014B.
8. System verifies that the total allocated amount equals the Expense amount exactly.
9. System stores ExpenseParticipant records.

---

## UC-011 — Edit Expense

### Actor

Group Member

### Description

Member modifies an Expense before Settlement.

### Business Flow

1. Member selects Expense.
2. System checks Expense status.
3. If Expense is not settled, modification is allowed.
4. System updates Expense information.

---

## UC-012 — Delete Expense

### Actor

Group Member

### Description

Member removes an Expense before Settlement.

### Business Flow

1. Member requests deletion.
2. System checks Expense status.
3. If allowed, the system changes the Expense status to Deleted.
4. The Expense remains preserved as a historical record.
5. Settled Expenses cannot be deleted.

---

# Settlement Management

---

## UC-013 — Create Settlement

### Actor

Group Owner

### Description

Owner starts a financial closing process for a Group.

### Business Flow

1. Owner requests Settlement.
2. System collects active Expenses.
3. System calculates Member balances.
4. System generates Debt records.
5. Related Expenses become frozen.

---

## UC-014 — View Settlement Result

### Actor

Group Member

### Description

Members view final financial results.

### Business Flow

1. Member opens Settlement.
2. System displays calculated balances.
3. System displays generated Debts.

---

# Debt Management

---

## UC-015 — View Debt

### Actor

Group Member

### Description

Member views outstanding Debt obligations.

### Business Flow

1. Member opens financial information.
2. System retrieves related Debts.
3. System displays unpaid amounts.

---

# Payment Management

---

## UC-016 — Record Payment

### Actor

Group Member

### Description

Member records a payment used to settle a Debt.

### Business Flow

1. Debtor records a Payment for an existing Debt.
2. System validates that the Payment amount does not exceed the available payment amount of the Debt.
3. System creates the Payment with Pending status.
4. System reserves the Payment amount from the Debt payment capacity.
5. Debt Outstanding Balance remains unchanged until the Payment is confirmed.

---

## UC-017 — Confirm Payment

### Actor

Group Member

### Description

Creditor confirms received Payment.

### Business Flow

1. Creditor reviews a Pending Payment.
2. Creditor confirms the Payment.
3. System changes the Payment status to Confirmed.
4. System updates the Debt Outstanding Balance within the same transaction.
5. System recalculates the Debt status.

---

# Reporting

---

## UC-018 — View Group Dashboard

### Actor

Group Member

### Description

Member views Group financial information.

### Business Flow

1. Member opens Group dashboard.
2. System retrieves expense and settlement information.
3. System displays financial summary.

---

## UC-019 — View Activity History

### Actor

Group Member

### Description

Member views important business activities.

### Business Flow

1. Member opens Activity Log.
2. System retrieves historical activities.
3. System displays chronological records.

---

## UC-020 — Reject Payment

### Actor

Group Member

### Description

Creditor rejects a Pending Payment that has not been accepted.

### Business Flow

1. Creditor reviews a Pending Payment.
2. Creditor rejects the Payment.
3. System changes the Payment status to Rejected.
4. System releases the reserved Debt payment capacity.
5. Debt Outstanding Balance remains unchanged.

---

# Use Case Relationships

---

## Include Relationships

| Base Use Case     | Included Use Case  |
| ----------------- | ------------------ |
| Create Expense    | Split Expense      |
| Create Settlement | Calculate Balance  |
| Record Payment    | Update Debt Status |

---

## Extend Relationships

| Base Use Case        | Extension            |
| -------------------- | -------------------- |
| Manage Group Members | Transfer Ownership   |
| Leave Group          | Ownership Validation |
| Create Settlement    | Debt Optimization    |

---

# Use Case Summary

| ID     | Use Case               |
| ------ | ---------------------- |
| UC-001 | Register Account       |
| UC-002 | Authenticate User      |
| UC-003 | Manage Profile         |
| UC-004 | Create Group           |
| UC-005 | Invite Member          |
| UC-006 | Manage Group Members   |
| UC-007 | Leave Group            |
| UC-008 | Archive Group          |
| UC-009 | Create Expense         |
| UC-010 | Split Expense          |
| UC-011 | Edit Expense           |
| UC-012 | Delete Expense         |
| UC-013 | Create Settlement      |
| UC-014 | View Settlement Result |
| UC-015 | View Debt              |
| UC-016 | Record Payment         |
| UC-017 | Confirm Payment        |
| UC-018 | View Group Dashboard   |
| UC-019 | View Activity History  |
| UC-020 | Reject Payment         |

---

Version: 1.0

Status: Draft

Sprint: Sprint 0
