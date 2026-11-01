CREATE DATABASE IF NOT EXISTS smart_job_portal;
USE smart_job_portal;
-- Spring Data JPA/Hibernate creates and updates the normalized tables at startup.
-- This reference schema documents key relations and uniqueness constraints.
-- users(id PK, full_name, email UNIQUE, password_hash, role, skills, created_at)
-- jobs(id PK, recruiter_id FK users.id, title, company, location, description,
--      required_skills, min_salary, max_salary, min_experience_years, published, created_at, version)
-- job_applications(id PK, candidate_id FK users.id, job_id FK jobs.id,
--      resume_path, status, applied_at, version, UNIQUE(candidate_id, job_id))
-- application_status_history(id PK, application_id FK job_applications.id,
--      old_status, new_status, changed_by FK users.id, note, changed_at)
-- interviews(id PK, application_id FK job_applications.id, candidate_id FK users.id,
--      recruiter_id FK users.id, start_time, end_time, meeting_link, cancelled, created_at)
