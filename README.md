# Money Manager Backend

Backend API for Money Manager Application built with Spring Boot and MongoDB.

## Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB Atlas account (or local MongoDB)

## Setup Instructions

### 1. Clone the Repository

```bash
git clone <your-repo-url>
cd money-manager-backend
```

### 2. Configure MongoDB Atlas

1. Go to [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)
2. Create a free cluster
3. Create a database user
4. Whitelist your IP address (or use 0.0.0.0/0 for development)
5. Get your connection string

### 3. Update application.properties

Edit `src/main/resources/application.properties`:

```properties
spring.data.mongodb.uri=mongodb+srv://<username>:<password>@cluster0.xxxxx.mongodb.net/moneymanager?retryWrites=true&w=majority
```

Replace `<username>`, `<password>`, and cluster URL with your MongoDB Atlas credentials.

### 4. Build the Project

```bash
mvn clean install
```

### 5. Run the Application

```bash
mvn spring-boot:run
```

The server will start on `http://localhost:8080`

### 6. Initialize Default Categories

Make a POST request to:
```
POST http://localhost:8080/api/categories/initialize
```

This will create default income and expense categories.

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

## Testing with Postman

1. Import the API endpoints into Postman
2. Initialize categories first: `POST /api/categories/initialize`
3. Create some accounts: `POST /api/accounts`
4. Create transactions: `POST /api/transactions`
5. Test dashboard APIs to see summaries

## Deployment

### Deploy to Render

1. Create account on [Render](https://render.com)
2. Create new Web Service
3. Connect your GitHub repository
4. Set build command: `mvn clean install`
5. Set start command: `java -jar target/money-manager-backend-1.0.0.jar`
6. Add environment variables:
   - `SPRING_DATA_MONGODB_URI`
   - `CORS_ALLOWED_ORIGINS`
7. Deploy!

### Deploy to Railway

1. Create account on [Railway](https://railway.app)
2. Create new project from GitHub repo
3. Railway will auto-detect Spring Boot
4. Add environment variables
5. Deploy!

## Environment Variables for Production

```
SPRING_DATA_MONGODB_URI=your_mongodb_atlas_uri
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.com
SERVER_PORT=8080
```

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

## License

Open source for hackathon project.
