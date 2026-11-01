# Smart Job Portal & Recruitment Management System

A Java 17 / Spring Boot REST API starter implementation for the internship task. It includes JWT authentication, role-based access, job CRUD/search, applications with duplicate protection and status history, interview scheduling, recommendations, validation, Swagger UI, MySQL and Docker Compose.

## Requirements
- Java 17+
- Maven 3.9+
- MySQL 8+ (or Docker Desktop)

## Run locally
1. Set up MySQL, then create database `smart_job_portal` (or let the JDBC URL create it).
2. Configure environment variables if needed:
   - `DB_URL` (default `jdbc:mysql://localhost:3306/smart_job_portal?...`)
   - `DB_USERNAME` (default `root`)
   - `DB_PASSWORD` (default `root`)
   - `JWT_SECRET` (default is development-only; replace it)
3. Start:
   ```bash
   mvn spring-boot:run
   ```
4. API docs: http://localhost:8080/swagger-ui/index.html

## Run with Docker Compose
```bash
docker compose up --build
```
API listens on `http://localhost:8080`. Default credentials in compose are for local development only; change them before using this anywhere public.

## API quick start
Register:
```http
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "Candidate One",
  "email": "candidate@example.com",
  "password": "StrongPass123",
  "role": "CANDIDATE",
  "skills": "Java, SQL, Spring Boot"
}
```
Recruiter registration is the same with `"role": "RECRUITER"`. The response contains a JWT token. Send it on protected endpoints:
`Authorization: Bearer <token>`

Login: `POST /api/auth/login`
```json
{"email":"candidate@example.com","password":"StrongPass123"}
```

### Endpoints
| Method | Endpoint | Access | Purpose |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register candidate/recruiter |
| POST | `/api/auth/login` | Public | Login and issue JWT |
| GET | `/api/jobs` | Public | Search published jobs |
| GET | `/api/jobs/recommendations` | Candidate JWT | Skill-based recommendations |
| POST | `/api/jobs` | Recruiter JWT | Create job |
| PUT | `/api/jobs/{id}` | Owning recruiter | Update job |
| DELETE | `/api/jobs/{id}` | Owning recruiter | Delete job |
| POST | `/api/applications/{jobId}` | Candidate JWT | Apply; multipart field `resume` |
| GET | `/api/applications/me` | Candidate JWT | List own applications |
| GET | `/api/applications/job/{jobId}` | Owning recruiter | List applicants for a job |
| PATCH | `/api/applications/{id}/status` | Owning recruiter | Update status |
| GET | `/api/applications/{id}/history` | Related candidate/recruiter | View status history |
| POST | `/api/interviews` | Owning recruiter | Schedule interview |
| GET | `/api/interviews/me` | Candidate/recruiter JWT | List own interviews |
| PATCH | `/api/interviews/{id}/cancel` | Interview participant | Cancel interview |

### Job search parameters
`keyword`, `location`, `skills`, `minExperience`, `page` (0-based), `size` (max 50), `sortBy` (`createdAt`, `title`, `minSalary`, `location`), `direction` (`asc` or `desc`).

### Application status transitions
- `APPLIED -> SHORTLISTED | REJECTED`
- `SHORTLISTED -> INTERVIEW_SCHEDULED | REJECTED`
- `INTERVIEW_SCHEDULED -> SELECTED | REJECTED | SHORTLISTED`
- `SELECTED` and `REJECTED` are terminal.

## Tests
Run `mvn test`. The included context-load test is a starter test only. The task specifically requires ten scenario tests; add controller/service integration tests for all ten and use Testcontainers or a dedicated test database for MySQL-specific concurrency/rollback verification before claiming full test coverage.

## Important limitations before submission
This is a working backend foundation, not a verified production deployment. Email notifications are not wired to an email provider. Interview overlap detection checks existing records but needs candidate-level locking to be fully race-proof under simultaneous scheduling. Rescheduling can be done by cancelling and creating a new interview; a dedicated reschedule endpoint is not included yet. Resume downloads are not exposed. Review and test all requirements yourself before submitting, and be ready to explain every module.
