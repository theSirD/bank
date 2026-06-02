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

### Вариант A: полностью в Docker (приложение + PostgreSQL)

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

### Вариант B: локально (приложение на хосте, БД в Docker)

#### 1) Запустить PostgreSQL

```bash
docker compose up -d postgres
```

#### 2) Запустить приложение

```bash
mvn spring-boot:run
```

#### 3) Открыть документацию API

- Swagger UI: http://localhost:8080/swagger-ui.html

## Тестовые пользователи по умолчанию

| Логин | Пароль | Роль |
|------|--------|------|
| admin | admin123 | ADMIN |
| user | user123 | USER |

## Переменные окружения (необязательно)

| Переменная | Назначение |
|-----------|------------|
| `JWT_SECRET` | Секрет HMAC для подписи JWT (желательно не меньше 256 бит); обязателен для Docker Compose |
| `CARD_ENCRYPTION_KEY` | Base64-строка для AES-ключа длиной 32 байта; обязательна для Docker Compose |

## Пример: вход и получение своих карт

```bash
# Авторизация
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}' | jq -r .accessToken)

# Список своих карт
curl -s http://localhost:8080/api/cards?page=0&size=10 \
  -H "Authorization: Bearer $TOKEN"
```

## Пример для администратора: создание карты

Используй корректный 16-значный номер карты (Luhn), например: `4111111111111111`.

```bash
ADMIN_TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' | jq -r .accessToken)

curl -s -X POST http://localhost:8080/api/admin/cards \
  -H "Authorization: Bearer $ADMIN_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "ownerId": 2,
    "pan": "4111111111111111",
    "expiryMonth": 12,
    "expiryYear": 2028,
    "initialBalance": 1000.00
  }'
```

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
