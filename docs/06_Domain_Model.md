# 06. Domain Model

---

## Purpose

This document defines the core domain model of SmartShareExpenseHub.

The domain model represents business concepts before translating them into database tables or source code.

Its purpose is to establish a shared understanding of the business domain.

---

# Domain Overview

The system revolves around collaborative financial management.

There are four primary aggregates:

- User Aggregate
- Group Aggregate
- Expense Aggregate
- Settlement Aggregate

Each aggregate protects its own business consistency.

---

# Aggregate 1 — User

## Aggregate Root

User

### Responsibilities

- Register account
- Authenticate
- Join Groups
- Leave Groups
- Receive Notifications

### Child Entities

- Notification (Future)
- UserPreference (Future)

---

# Aggregate 2 — Group

## Aggregate Root

Group

### Responsibilities

- Manage Members
- Manage Owners
- Archive Group
- Configure Group Settings
- Define and preserve Group Currency

### Child Entities

- Membership

Future:

- Invitation
- GroupSetting

---

# Aggregate 3 — Expense

## Aggregate Root

Expense

### Responsibilities

- Record shared expenses
- Manage participants
- Calculate split amounts
- Preserve expense history

### Child Entities

- ExpenseParticipant

Future:

- Receipt
- Attachment

---

# Aggregate 4 — Settlement

## Aggregate Root

Settlement

### Responsibilities

- Freeze Expenses
- Calculate balances
- Generate Debts
- Close settlement cycle

### Child Entities

- Debt
- Payment

---

# Entity List

The following business entities have independent identities.

| Entity             | Description                         |
| ------------------ | ----------------------------------- |
| User               | Registered system account           |
| Group              | Collaborative financial workspace   |
| Membership         | Relationship between User and Group |
| Expense            | Shared financial record             |
| ExpenseParticipant | Allocation of an Expense            |
| Settlement         | Financial closing cycle             |
| Debt               | Financial obligation                |
| Payment            | Debt settlement transaction         |
| ActivityLog        | Business audit history              |
| Notification       | User notification                   |

---

# Value Objects

The following objects are defined by value rather than identity.

| Value Object     | Description                                               |
| ---------------- | --------------------------------------------------------- |
| Money            | Monetary amount associated with an ISO 4217 currency code |
| Email            | Email address                                             |
| Password         | Hashed password                                           |
| GroupRole        | OWNER / MEMBER                                            |
| GroupStatus      | ACTIVE / ARCHIVED                                         |
| MembershipStatus | ACTIVE / LEFT / REMOVED                                   |
| ExpenseStatus    | ACTIVE / SETTLED / DELETED                                |
| SettlementStatus | GENERATED / COMPLETED                                     |
| SplitMethod      | EQUAL / PERCENTAGE / CUSTOM                               |
| NotificationType | Notification category                                     |
| CurrencyCode     | ISO 4217 currency code assigned to a Group                |
| DebtStatus       | OUTSTANDING / PAID                                        |
| PaymentStatus    | PENDING / CONFIRMED / REJECTED                            |

---

# Aggregate Relationships

User

↓

Membership

↓

Group

↓

Expense

↓

ExpenseParticipant

↓

Settlement

↓

Debt

↓

Payment

---

# Design Principles

## Financial history is immutable.

Historical financial records must never be modified after Settlement.

---

## Membership owns historical context.

Financial records reference Membership instead of User.

---

## Every Aggregate protects its own consistency.

Changes inside an Aggregate must be completed within a single transaction.

---

## Aggregates communicate through identifiers.

Aggregates should reference each other by ID rather than direct object ownership whenever possible.

---

# Domain Decisions

## DD-001

Settlement is an independent business entity.

---

## DD-002

Financial records reference Membership instead of User.

---

## DD-003

Settlement generates Debt records.

It never generates Payment records.

---

## DD-004

Payment belongs to exactly one Debt.

---

## DD-005

One Debt may have multiple Payments.

---

## DD-006

Historical financial records are immutable after Settlement.

---

## DD-007

Archived Groups are read-only.

---

## DD-008

Aggregates communicate through identifiers instead of direct object ownership whenever possible.

Version

1.0

Status

Draft

Sprint

Sprint 0
