# Repository Guidelines

## Project Structure & Module Organization
- `backend/` houses the Spring Boot service.
- `backend/src/main/java/com/example/perm` contains controllers, services, repositories, and entities.
- `backend/src/main/resources` holds `application*.yml` profiles.
- `frontend/` is a Vue 3 + Vite app; UI code lives in `frontend/src` (router, views, stores).
- `frontend/dist` is the production build output.
- `start.ps1` and `start.bat` provide a one-command local startup.

## Build, Test, and Development Commands
- `./start.ps1` or `start.bat` starts backend (H2 profile) and frontend dev server.
- Backend dev: `cd backend` then `mvn spring-boot:run "-Dspring-boot.run.profiles=h2"`.
- Backend build: `mvn clean package`.
- Frontend install: `cd frontend` then `npm install`.
- Frontend dev: `npm run dev -- --port 5173`.
- Frontend build/preview: `npm run build`, `npm run preview`.
- Frontend type check: `npm run typecheck`.

## Coding Style & Naming Conventions
- Java uses 4-space indentation; keep package names under `com.example.perm`.
- Class names use PascalCase; methods and fields use lowerCamelCase.
- Vue/TypeScript files follow existing formatting; no lint/format scripts are configured.
- Views are in `frontend/src/views`, routes in `frontend/src/router`, shared API types in `frontend/src/types`.

## Testing Guidelines
- Backend includes Spring Boot test dependencies; run `mvn test`.
- No tests are present under `backend/src/test` yet; add tests alongside new logic.
- Frontend has no test runner configured; avoid introducing one without team agreement.

## Commit & Pull Request Guidelines
- Git history shows no consistent convention; use short, imperative subjects (e.g., "backend: add role API").
- PRs should describe changes, include verification steps, and add screenshots for UI changes.

## Configuration & Security
- Local dev uses the H2 profile; production uses MySQL settings from `application.yml`.
- Prefer environment variables: `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`, `JWT_SECRET`.
- Do not commit secrets or local credentials.
