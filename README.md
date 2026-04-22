# PaperTrade Hub

PaperTrade Hub is a crypto watchlist and paper trading dashboard built for backend-focused portfolio presentation. The project combines a Spring Boot API with a React dashboard and demonstrates layered backend design, relational modeling, validation, CRUD operations, watchlist state tracking, and trade logging.

## Project Description

PaperTrade Hub is a portfolio simulation application designed to help users track market assets, organize trade setups, and log paper trades without using real capital. The backend is built with Spring Boot using layered architecture, DTO-based request and response handling, validation, and JPA/Hibernate for data persistence. The frontend is a React dashboard that presents watchlist status, asset momentum, trade history, and setup notes in a responsive internal-tool interface.

## GitHub Short Description

Backend-first crypto watchlist and paper trading dashboard built with Spring Boot, React, JPA, and H2 in MSSQL mode.

## Stack

- Backend: Java 21, Spring Boot 3.5, Spring Data JPA, Spring Security, Validation
- Database: H2 in MSSQL compatibility mode
- Frontend: React 19, Vite
- Testing: JUnit 5, MockMvc

## Core Features

- Asset tracking with symbol, asset class, market price, daily change, and thesis notes
- Watchlist management with `ACTIVE`, `READY`, and `PAUSED` setup states
- Paper trade logging for `BUY` and `SELL` actions with strategy tags and trade notes
- Dashboard metrics for tracked assets, ready setups, trade count, and recent execution history
- Seed data for immediate demo without manual setup
- Global error handling and request validation

## Project Structure

- `backend`: Spring Boot REST API
- `frontend`: React dashboard

## Run Locally

### Requirements

- Java 21 or newer
- Node.js 20+
- npm

### 1. Start the backend

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

The backend starts on `http://localhost:8080`

### 2. Start the frontend in a second terminal

```powershell
cd frontend
npm install
npm run dev
```

The frontend starts on `http://localhost:5173`

### 3. Open the application

- Open `http://localhost:5173`
- The frontend talks to `http://localhost:8080` by default
- If you want to change the backend URL, set `VITE_API_BASE_URL` before running the frontend

### Easier option

From the project root, you can start both services with one command:

```powershell
.\start-local.ps1
```

To stop background processes started for this project:

```powershell
.\stop-local.ps1
```

## Useful Endpoints

- `GET /api/dashboard`
- `GET /api/assets`
- `POST /api/assets`
- `GET /api/watchlist`
- `POST /api/watchlist`
- `GET /api/trades`
- `POST /api/trades`

## Demo Notes

- H2 console is available at `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:papertradehub`
- Username: `sa`
- Password: empty

## Why It Fits Your CV

- Shows `Spring Boot`, `REST API`, `JPA`, `Hibernate`, `DTO`, and layered architecture knowledge
- Connects naturally to the crypto interface experience already present in your CV
- Gives you a portfolio project with realistic state management, trade logging, and decision support metrics
