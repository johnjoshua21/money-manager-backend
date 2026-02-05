# Money Manager Backend

Backend API for Money Manager Application built with Spring Boot and MongoDB.


## API Endpoints

### Transaction APIs

- `POST /api/transactions` - Create transaction
- `GET /api/transactions` - Get all transactions (with filters)
- `GET /api/transactions/{id}` - Get transaction by ID
- `PUT /api/transactions/{id}` - Update transaction (within 12 hours)
- `DELETE /api/transactions/{id}` - Delete transaction (within 12 hours)

**Query Parameters for GET /api/transactions:**
- `startDate` - Filter by start date (ISO 8601 format)
- `endDate` - Filter by end date (ISO 8601 format)
- `type` - Filter by INCOME or EXPENSE
- `division` - Filter by OFFICE or PERSONAL
- `category` - Filter by category name

### Dashboard APIs

- `GET /api/dashboard/summary` - Get income/expense summary
  - Query: `period` (MONTHLY, WEEKLY, YEARLY), `date`
- `GET /api/dashboard/chart` - Get chart data
  - Query: `period` (MONTHLY, WEEKLY, YEARLY), `year`
- `GET /api/dashboard/category-summary` - Get category-wise breakdown
  - Query: `startDate`, `endDate`, `type`
- `GET /api/dashboard/division-summary` - Get division-wise breakdown
  - Query: `startDate`, `endDate`

### Account APIs

- `POST /api/accounts` - Create account
- `GET /api/accounts` - Get all accounts
- `GET /api/accounts/{id}` - Get account by ID
- `PUT /api/accounts/{id}` - Update account
- `DELETE /api/accounts/{id}` - Delete account

### Transfer APIs

- `POST /api/transfers` - Create transfer between accounts
- `GET /api/transfers` - Get all transfers
  - Query: `startDate`, `endDate`
- `GET /api/transfers/{id}` - Get transfer by ID

### Category APIs

- `GET /api/categories` - Get all categories
- `POST /api/categories` - Create category
- `POST /api/categories/initialize` - Initialize default categories

## Sample API Requests

### Create Income Transaction

```json
POST /api/transactions
{
  "type": "INCOME",
  "amount": 50000,
  "category": "salary",
  "division": "PERSONAL",
  "description": "Monthly salary",
  "date": "2024-02-05T10:00:00"
}
```

### Create Expense Transaction

```json
POST /api/transactions
{
  "type": "EXPENSE",
  "amount": 500,
  "category": "fuel",
  "division": "OFFICE",
  "description": "Office vehicle fuel",
  "date": "2024-02-05T15:30:00"
}
```

### Create Account

```json
POST /api/accounts
{
  "accountName": "Savings Account",
  "balance": 100000
}
```

### Create Transfer

```json
POST /api/transfers
{
  "fromAccountId": "account_id_1",
  "toAccountId": "account_id_2",
  "amount": 5000,
  "description": "Transfer to savings",
  "date": "2024-02-05T10:00:00"
}
```

## Response Format

All API responses follow this structure:

```json
{
  "success": true,
  "data": { ... },
  "message": "Success message"
}
```

Error responses:

```json
{
  "success": false,
  "data": null,
  "message": "Error message"
}
```

## Business Rules

1. **12-Hour Edit Rule**: Transactions can only be edited or deleted within 12 hours of creation
2. **Account Transfers**: Validates sufficient balance before transferring
3. **Date Range Filtering**: All date parameters should be in ISO 8601 format
4. **Transaction Types**: INCOME or EXPENSE
5. **Divisions**: OFFICE or PERSONAL



## Project Structure

```
src/
├── main/
│   ├── java/com/moneymanager/
│   │   ├── config/         # Configuration classes
│   │   ├── controller/     # REST Controllers
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── exception/      # Custom Exceptions
│   │   ├── model/          # Entity Models
│   │   ├── repository/     # MongoDB Repositories
│   │   ├── service/        # Business Logic
│   │   └── MoneyManagerApplication.java
│   └── resources/
│       └── application.properties
```

## Technologies Used

- Spring Boot 3.2.0
- Spring Data MongoDB
- Lombok
- Maven

