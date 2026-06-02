# Система управления банковскими картами

Backend API для управления банковскими картами: аутентификация через JWT, ролевой доступ (ADMIN / USER), шифрование номера карты, переводы между своими картами и процесс запроса блокировки.


## Технологии

- Java 17, Spring Boot 3.3
- Spring Security + JWT
- Spring Data JPA, PostgreSQL
- Liquibase (миграции)
- Docker Compose
- OpenAPI / Swagger UI

## Требования

- JDK 17+
- Maven 3.9+
- Docker

## Быстрый старт

### Полностью в Docker (приложение + PostgreSQL)

Перед запуском создай `.env` в корне проекта:

```env
JWT_SECRET=change-me-to-a-very-long-secret-key-at-least-256-bits-long-for-hs256
CARD_ENCRYPTION_KEY=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=
```

```bash
docker compose up --build
```

После старта:

- API: http://localhost:8080
- Swagger UI: http://localhost:8080/swagger-ui.html

#### Открыть документацию API

- Swagger UI: http://localhost:8080/swagger-ui.html

## Тестовые пользователи по умолчанию

| Логин | Пароль | Роль |
|------|--------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |


## Запуск тестов

```bash
mvn clean test
```

## Основные группы API

| Префикс | Описание |
|--------|----------|
| `POST /api/auth/login` | Получение JWT-токена |
| `/api/admin/users` | Управление пользователями (ADMIN) |
| `/api/admin/cards` | Управление картами (ADMIN) |
| `/api/admin/block-requests` | Подтверждение/отклонение заявок на блокировку |
| `/api/cards` | Карты текущего пользователя, баланс, запрос блокировки |
| `POST /api/transfers` | Переводы между своими картами |

## Статусы карты

- `ACTIVE` — активна
- `BLOCK_REQUESTED` — пользователь отправил запрос на блокировку, ожидается решение администратора
- `BLOCKED` — заблокирована
- `EXPIRED` — срок действия истек
