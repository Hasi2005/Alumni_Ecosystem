# Alumni_Student_Ecosystem

Run the full microservices stack with Docker Compose.

What’s included
- Multi-stage Dockerfiles for each service
- Dedicated MySQL 8 instance per microservice (security_db, mentorship, job_service_db, referral_service_db, fund_db)
- docker-compose.yml wiring each service to its own MySQL container
- .env to set credentials and host ports (edit as needed)

Prerequisites
- Docker Desktop 4.x+

Quick start
```bash
# From the Alumni_student/ directory
docker compose build
docker compose up -d
```

Services (host URLs)
- Auth: http://34.66.236.172:${AUTH_HOST_PORT:-9081}
- Mentorship: http://34.66.236.172:${MENTORSHIP_HOST_PORT:-8082}
- Job: http://34.66.236.172:${JOB_HOST_PORT:-9083}
- Referral: http://34.66.236.172:${REFERRAL_HOST_PORT:-9084}
- Fund Allocation: http://34.66.236.172:${FUND_HOST_PORT:-8080}

MySQL (one container per service; connect with MySQL Workbench)
- Auth DB:       34.66.236.172:${AUTH_DB_HOST_PORT:-3310}  (db: security_db, user: root, pass: from .env)
- Mentorship DB: 34.66.236.172:${MENTORSHIP_DB_HOST_PORT:-3311}  (db: mentorship)
- Job DB:        34.66.236.172:${JOB_DB_HOST_PORT:-3312}  (db: job_service_db)
- Referral DB:   34.66.236.172:${REFERRAL_DB_HOST_PORT:-3313}  (db: referral_service_db)
- Fund DB:       34.66.236.172:${FUND_DB_HOST_PORT:-3314}  (db: fund_db)

Environment overrides
- Set MYSQL_ROOT_PASSWORD, STRIPE_SECRET_KEY, STRIPE_PUBLISHABLE_KEY in .env (already created).
- Host ports are configurable via .env:
  - Service ports: AUTH_HOST_PORT, MENTORSHIP_HOST_PORT, JOB_HOST_PORT, REFERRAL_HOST_PORT, FUND_HOST_PORT
  - DB ports: AUTH_DB_HOST_PORT, MENTORSHIP_DB_HOST_PORT, JOB_DB_HOST_PORT, REFERRAL_DB_HOST_PORT, FUND_DB_HOST_PORT

Stop and clean
```bash
# Stop
docker compose down

# To also remove all MySQL data volumes (for a fresh start)
docker compose down -v
```

Troubleshooting
- If a port is busy, change the left side of the port mappings in .env.
- Services wait for their DB container to be healthy before starting.
- Databases are auto-created via each MySQL container's MYSQL_DATABASE plus Spring's `createDatabaseIfNotExist=true`.
- Spring env vars in compose override application.properties (datasource URLs point at their dedicated mysql-* container).

Windows-specific setup
1) Start Docker Desktop and ensure it’s running.
2) Use Linux containers.
3) Ensure WSL 2 is installed and enabled:
```powershell
wsl --status
```
4) Optional: verify Docker connectivity:
```powershell
docker version
docker info
```
5) Build and run:
```powershell
cd <path to>/Alumni_student
docker compose build
docker compose up -d
```

Notes
- Compose v2 is used; no `version:` key is needed.
- Each service has its own persistent MySQL volume (e.g., mysql_auth_data, mysql_job_data).
