# 04. Domain Glossary

---

## Purpose

This document defines the core business terms used throughout SmartShareExpenseHub.

Every stakeholder, developer, tester, and future contributor should use these definitions consistently.

---

## Scope

This glossary covers the business vocabulary used in:

- Product Requirements
- Business Rules
- Database Design
- REST APIs
- Source Code

## User

A registered account in the system.

A User can belong to multiple Groups through Memberships.

---

## Group

A collaborative space where Members share expenses and manage financial activities together.

Examples:

- Rental house
- Travel group
- Family
- Club

---

## Group Currency

The single ISO 4217 currency code assigned to a Group when it is created.

Group Currency is immutable and must be used by all Expenses, Debts, and Payments in that Group.

---

## Membership

Represents one specific period of participation of a User in a Group.

Membership stores information such as:

- Role
- Join Date
- Leave Date
- Status

A User may have multiple historical Memberships in the same Group, but may have at most one active Membership at a time.

If a User leaves or is removed and later rejoins the Group, the system creates a new Membership. Previous Membership records are never reactivated.

Financial records reference Membership instead of User to preserve historical context.

---

## Owner

A Membership with administrative permissions.

Owners can:

- Invite Members
- Remove Members
- Archive Groups
- Generate Settlements
- Transfer Ownership

A Group may have multiple Owners.

---

## Member

A regular participant in a Group.

Members can:

- Create Expenses
- View Reports
- Record Payments

Members cannot manage Group settings.

## Expense

A financial record representing money paid by one Member on behalf of one or more Members.

Examples:

- Rent
- Electricity
- Dinner
- Hotel
- Taxi

---

## Expense Participant

A Membership that shares financial responsibility for an Expense.

Each Expense Participant stores an allocated amount.

The allocated amounts of all Expense Participants must equal the total Expense amount exactly.

For Percentage Split, an Expense Participant also stores the percentage used to calculate its allocated amount.

A Membership may participate at most once in the same Expense.

---

## Split Method

Defines how an Expense is divided among participants.

Supported methods:

- Equal Split
- Percentage Split
- Custom Amount Split

Future versions may support weighted splitting.

---

## Settlement

A financial closing process that calculates the final balances of all Members.

Settlement freezes related Expenses and generates Debt records.

---

## Debt

A financial obligation generated after Settlement.

Example:

Ryan owes Nam 200,000 VND.

Debt exists until fully paid.

---

## Payment

A real money transfer used to settle a Debt.

Payments never exist without an associated Debt.

---

---

## Former Member

A Membership that has left or been removed from a Group while preserving historical financial records.

---

## Outstanding Balance

The remaining unpaid amount of a Debt after Confirmed Payments have been applied.

Pending Payments do not reduce the Outstanding Balance.

---

## Partial Payment

A Payment that settles only part of a Debt.

---

## Pending Payment

A Payment awaiting review by the Creditor.

A Pending Payment reserves Debt payment capacity but does not reduce the Debt Outstanding Balance.

---

## Confirmed Payment

A Payment accepted by the Creditor.

## Rejected Payment

A Payment rejected by the Creditor.

A Rejected Payment does not reduce the Debt Outstanding Balance and releases its reserved payment capacity.

## Activity Log

A chronological record of important business actions.

Examples:

- Expense Created
- Member Joined
- Settlement Completed
- Ownership Transferred

Activity Logs are immutable.

---

## Archived Group

A Group that has completed its lifecycle.

Archived Groups become read-only while preserving all historical data.

## Naming Convention

The following terms should always be used consistently throughout the project.

| Preferred Term | Avoid                                                 |
| -------------- | ----------------------------------------------------- |
| Group          | Team, Room                                            |
| Member         | Participant (when referring to a person in the group) |
| Expense        | Bill                                                  |
| Debt           | Balance                                               |
| Settlement     | Closing                                               |
| Payment        | Transfer                                              |
| Membership     | UserGroup                                             |

---

Version: 1.0

Status: Draft

Sprint: Sprint 0
