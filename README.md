# Expense Tracker + Budget Alerts

A simple full-stack expense tracker that supports CRUD expenses, monthly summaries, category totals, budget alerts, and CSV export.

## Stack
- Backend: Java 17, Spring Boot, JPA
- Database: PostgreSQL (Docker)
- Frontend: React (Vite)

## Features
- Create, edit, list, and delete expenses
- Monthly and category summaries
- Budget limits with alert statuses (OK, WARNING, OVER)
- CSV export for any month

## Local Setup

### 1) Start Postgres (local only)
```bash
docker-compose up -d
```

### 2) Run backend
```bash
cd backend
mvn spring-boot:run
```

### 3) Run frontend
```bash
cd frontend
npm install
npm run dev
```

Frontend runs on `http://localhost:5173` and proxies API calls to `http://localhost:8080`.

### One-command Docker setup (frontend + backend + DB)
```bash
docker-compose up --build
```

Frontend: `http://localhost:5173`  
Swagger UI: `http://localhost:8080/swagger-ui.html`

Config is loaded from `.env` in the project root (DB name/user/password).

## API Endpoints (backend)
- `GET /api/expenses`
- `GET /api/expenses?month=YYYY-MM`
- `GET /api/expenses?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
- `GET /api/expenses/{id}`
- `POST /api/expenses`
- `PUT /api/expenses/{id}`
- `DELETE /api/expenses/{id}`
- `GET /api/expenses/export?month=YYYY-MM`
- `GET /api/budgets`
- `POST /api/budgets`
- `DELETE /api/budgets/{id}`
- `GET /api/summary/monthly?year=YYYY`
- `GET /api/summary/categories?month=YYYY-MM`
- `GET /api/summary/alerts?month=YYYY-MM`
