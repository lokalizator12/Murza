# MURZA server deployment

Clone the backend into `app/` and the frontend into `frontend/` under one parent directory. Keep the private `.env` in that parent directory; never commit it.

From `app/`, deploy both services and their dependencies:

```sh
docker compose --env-file ../.env -f deploy/compose.yaml up -d --build
```

The frontend listens on port 80 and proxies `/api/` and `/ws/` to the backend. The database, MongoDB, Redis, and Kafka are internal to the Compose network. Docker volumes hold their data across container recreation.

The backend adds missing standard shipping methods at startup. It preserves existing method IDs and creates only missing names. The frontend requests the methods from the API instead of assuming fixed IDs.

## Current stabilization status

- Backend and frontend build successfully in Docker.
- Backend security regression tests cover private user fields, request ownership, public signup role, revoked WebSocket tokens, and password reset tokens.
- Frontend wizard test covers retaining form data through submission.
- Live API checks cover login, request lists, ownership, input validation, and shipping methods.

## Authenticated messages

`POST /api/messages` accepts a recipient ID and 1–2,000 characters, returns the saved message with HTTP 201, and broadcasts to the two participants. The sender comes from authentication. Redis atomically limits each account to 30 sends per 60 seconds; authenticated chat uses this limit instead of an external CAPTCHA. The legacy STOMP send route uses the same delivery service. History pages are capped at 100 messages. Message timestamps use UTC instants, and MongoDB continues storing encrypted content.

WebSocket clients can subscribe only to their own message/read-receipt queues and public presence topics. They can publish only to explicit application actions. Expiration and revocation are checked again on CONNECT, SEND and SUBSCRIBE. The inbox loads the last message and an unread count instead of materializing every conversation's history; read receipts update only incoming unread messages.


## Remaining work

1. Add a domain and HTTPS before production use. Current HTTP traffic exposes credentials in transit.
2. Rotate the external credentials shared during setup (AWS, SMTP, Twilio, reCAPTCHA, and other keys), then update the private `.env`.
3. Replace Hibernate schema auto-update and the disabled Flyway setup with reviewed, repeatable database migrations. Back up the existing volume first.
4. Add integration tests for registration, verification, password reset email delivery, profile updates, subscriptions, chat, and file uploads.
5. Review large list endpoints and database query counts. Some endpoints load entire tables or related users per row.
6. Modernize the frontend build tool and dependency set; the current Create React App build reports maintenance warnings.
7. Add message request IDs for safe retries after uncertain delivery, enforce a unique conversation per pair of users, and review MongoDB indexes for history and unread counts.
