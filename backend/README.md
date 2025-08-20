# Invoice Backend

Spring Boot backend for Automated Invoice Generator.

Requirements:
- Java 11
- Maven

Run:

```powershell
mvn spring-boot:run
```

Endpoints:
- POST /api/invoices
- GET /api/invoices
- GET /api/invoices/{id}
- PUT /api/invoices/{id}
- DELETE /api/invoices/{id}
- GET /api/invoices/{id}/pdf

Notes:
- SMTP in `application.properties` is configured for a local test SMTP (e.g., MailHog). Configure real credentials to send emails.
