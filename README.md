# Automated Invoice Generator

Full-stack example: Spring Boot backend + lightweight JavaScript frontends for generating, storing, PDF-ing, and emailing invoices.

Quick summary
- Backend: Spring Boot (Maven), H2 in-memory DB, PDF generation with Apache PDFBox, email sending via Spring Mail.
- Frontend (lightweight): AngularJS (plain JS) UI (`frontend_angularjs/`) and a tiny static UI (`frontend_static/`). A full Angular (TypeScript) scaffold exists in `frontend/` but is not functional out-of-the-box.

Repository layout
- backend/ — Spring Boot application
  - src/main/java/com/invoice — application code
  - src/main/resources/application.properties — configuration
  - .env.example — sample .env (DO NOT COMMIT real secrets)
- frontend_angularjs/ — AngularJS (1.x) plain-JS UI (recommended for quick run)
- frontend_static/ — very small static UI (zero-deps)
- frontend/ — attempted Angular CLI scaffold (may be incomplete)

Prerequisites
- Java 11+ (Java 17+ recommended)
- Maven
- Node.js + npm (only required to serve static frontends or run the Angular scaffold)

Environment / SMTP setup (Gmail)
- This project supports loading SMTP credentials from a `.env` file in `backend/` (loaded at startup) or from environment variables.
- Create a Google App Password (Account → Security → App passwords) and use it as `SMTP_PASSWORD`.
- Create `backend/.env` (DO NOT commit) with:
```
SMTP_USERNAME=you@gmail.com
SMTP_PASSWORD=your_app_password
```
- Alternatively set env vars in PowerShell:
```powershell
setx SMTP_USERNAME "you@gmail.com"
setx SMTP_PASSWORD "your_app_password"
# restart shell / IDE
```

Backend — run & develop
1. Build
```powershell
cd backend
mvn -DskipTests package
```
2. Run
```powershell
mvn spring-boot:run
```
3. Configuration
- `src/main/resources/application.properties` reads `spring.mail.username` and `spring.mail.password` from the environment (or `.env`).
- Enable SMTP debug in `application.properties` with `spring.mail.properties.mail.debug=true` to see mail handshake logs.

Backend API (summary)
- POST /api/invoices — create invoice (JSON)
- GET /api/invoices — list invoices
- GET /api/invoices/{id} — get invoice
- PUT /api/invoices/{id} — update invoice
- DELETE /api/invoices/{id} — delete invoice
- GET /api/invoices/{id}/pdf — download invoice PDF
- POST /api/invoices/{id}/send — send invoice by email (body: `{ "to": "recipient@example.com", "subject": "...", "body": "..." }`)

Frontend — AngularJS (recommended quick run)
1. Serve the folder (from project root):
```powershell
cd frontend_angularjs
npx http-server -p 4201
```
2. Open http://localhost:4201 and make sure backend is running at http://localhost:8080.

Frontend — Static (alternative)
1. Serve the folder:
```powershell
cd frontend_static
npx http-server -p 4200
```
2. Open http://localhost:4200

CORS
- The backend includes a CORS filter allowing origins `http://localhost:4200` and `http://localhost:4201` for local development.

Troubleshooting
- 500 from `/api/invoices/{id}/send`: typically means SMTP credentials are missing or invalid. Ensure `.env` or env vars are set and that you used a Google App Password.
- If server fails to start because port 8080 is in use, stop the other Java process or change `server.port` in `application.properties`.
- If frontends can't reach backend, check CORS settings and that the backend is running.

Security notes
- Do NOT commit `.env` or real credentials. Use a secrets manager or environment variables in production.

Next steps / improvements
- Add authentication for APIs.
- Improve PDF layout and currency formatting.
- Build a proper Angular (TypeScript) frontend (replace AngularJS) and set up a build pipeline.

If you want, I can:
- Add a startup check that fails fast when SMTP credentials are missing.
- Wire MailHog / Mailtrap for local mail testing and show exact commands.
- Replace the AngularJS UI with a modern Angular app scaffold.

