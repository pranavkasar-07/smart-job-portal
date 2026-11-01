# API Examples

## Create a job (recruiter token)
`POST /api/jobs`
```json
{
  "title": "Java Developer Intern",
  "company": "Example Pvt Ltd",
  "location": "Pune",
  "description": "Build Spring Boot APIs and write tests.",
  "requiredSkills": "Java, Spring Boot, SQL",
  "minSalary": 8000,
  "maxSalary": 15000,
  "minExperienceYears": 0,
  "published": true
}
```

## Update application status (recruiter token)
`PATCH /api/applications/1/status`
```json
{"status":"SHORTLISTED","note":"Profile matches role requirements"}
```

## Schedule interview (recruiter token)
`POST /api/interviews`
```json
{
  "applicationId": 1,
  "startTime": "2026-11-05T09:00:00Z",
  "endTime": "2026-11-05T09:30:00Z",
  "meetingLink": "https://meet.example.com/interview"
}
```
