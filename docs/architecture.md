# Architecture Notes

- **Layers:** REST controllers -> services -> Spring Data JPA repositories -> MySQL.
- **Authentication:** registration/login returns a signed JWT. Passwords are stored with BCrypt. Protected requests use `Authorization: Bearer <token>`.
- **Authorization:** service-level ownership checks ensure a recruiter can only modify their own jobs and applications. Candidate and recruiter roles are separate.
- **Data integrity:** `job_applications` has a database unique constraint on `(candidate_id, job_id)`; service checks improve the normal path, while the unique constraint protects concurrent requests. `@Transactional` keeps application and initial status history consistent.
- **Status history:** each accepted status change creates a history row. Terminal `SELECTED` and `REJECTED` statuses cannot be changed.
- **Interview overlap:** an overlap query checks `existing.start < requested.end && existing.end > requested.start`. For strict race-proof scheduling under heavy concurrent traffic, add a per-candidate locking strategy (e.g. lock the candidate row with `PESSIMISTIC_WRITE`) or a database-specific exclusion/locking design.
- **Optimistic locking:** `@Version` is used on jobs and applications to detect conflicting updates.
- **Resumes:** files are stored under `uploads/resumes` by default, with random server-generated names and extension/size checks. For production, store them in private object storage and scan for malware.
- **Known production hardening:** replace default JWT secret, restrict CORS, add rate limiting, email provider, audit logging, file content-type/signature validation, migration tooling (Flyway), and integration tests with Testcontainers.
