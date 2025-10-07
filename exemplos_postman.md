# 📮 Exemplos de Requisições (Postman/cURL)

## 🔑 Autenticação

### 1. Registrar Novo Usuário

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "joao@example.com",
  "password": "senha123",
  "fullName": "João Silva"
}
```

**Response 201:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 3,
    "email": "joao@example.com",
    "fullName": "João Silva",
    "role": "USER",
    "active": true,
    "createdAt": "2025-10-06T15:30:00"
  }
}
```

---

### 2. Login

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@cambio.com",
  "password": "user123"
}
```

**Response 200:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c2VyQGNhbWJpby5jb20iLCJpYXQiOjE2OTYzNDU2MDAsImV4cCI6MTY5NjQzMjAwMH0.signature",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 2,
    "email": "user@cambio.com",
    "fullName": "Usuário Teste",
    "role": "USER",
    "active": true,
    "createdAt": "2025-10-05T10:00:00"
  }
}
```

---

## 💰 Moedas (Currencies)

### 3. Listar Todas as Moedas

```bash
GET http://localhost:8080/api/currencies
Authorization: Bearer {seu_token}
```

**Response 200:**
```json
[
  {
    "id": 1,
    "code": "BRL",
    "name": "Real Brasileiro",
    "symbol": "R$",
    "bcbCode": "BRL",
    "description": "Moeda oficial do Brasil",
    "active": true,
    "createdAt": "2025-10-05T10:00:00",
    "updatedAt": "2025-10-05T10:00:00"
  },
  {
    "id": 2,
    "code": "USD",
    "name": "Dólar Americano",
    "symbol": "$",
    "bcbCode": "USD",
    "description": "Moeda dos Estados Unidos",
    "active": true,
    "createdAt": "2025-10-05T10:00:00",
    "updatedAt": "2025-10-05T10:00:00"
  }
]
```

---

### 4. Listar Apenas Moedas Ativas

```bash
GET http://localhost:8080/api/currencies/active
Authorization: Bearer {seu_token}
```

---

### 5. Buscar Moeda por Código

```bash
GET http://localhost:8080/api/currencies/code/USD
Authorization: Bearer {seu_token}
```

**Response 200:**
```json
{
  "id": 2,
  "code": "USD",
  "name": "Dólar Americano",
  "symbol": "$",
  "bcbCode": "USD",
  "description": "Moeda dos Estados Unidos",
  "active": true,
  "createdAt": "2025-10-05T10:00:00",
  "updatedAt": "2025-10-05T10:00:00"
}
```

---

### 6. Criar Nova Moeda (ADMIN)

```bash
POST http://localhost:8080/api/currencies
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "code": "JPY",
  "name": "Iene Japonês",
  "symbol": "¥",
  "bcbCode": "JPY",
  "description": "Moeda do Japão",
  "active": true
}
```

**Response 201:**
```json
{
  "id": 6,
  "code": "JPY",
  "name": "Iene Japonês",
  "symbol": "¥",
  "bcbCode": "JPY",
  "description": "Moeda do Japão",
  "active": true,
  "createdAt": "2025-10-06T16:00:00",
  "updatedAt": "2025-10-06T16:00:00"
}
```

---

### 7. Atualizar Moeda (ADMIN)

```bash
PUT http://localhost:8080/api/currencies/6
Authorization: Bearer {admin_token}
Content-Type: application/json

{
  "code": "JPY",
  "name": "Iene Japonês",
  "symbol": "¥",
  "bcbCode": "JPY",
  "description": "Moeda oficial do Japão - Atualizada",
  "active": true
}
```

---

### 8. Ativar/Desativar Moeda (ADMIN)

```bash
PATCH http://localhost:8080/api/currencies/6/toggle
Authorization: Bearer {admin_token}
```

**Response 200:**
```json
{
  "id": 6,
  "code": "JPY",
  "active": false,
  ...
}
```

---

### 9. Deletar Moeda (ADMIN)

```bash
DELETE http://localhost:8080/api/currencies/6
Authorization: Bearer {admin_token}
```

**Response 204 No Content**

---

## 🔄 Conversões (Conversions)

### 10. Realizar Conversão de Moeda

```bash
POST http://localhost:8080/api/conversions
Authorization: Bearer {seu_token}
Content-Type: application/json

{
  "sourceCurrencyCode": "BRL",
  "targetCurrencyCode": "USD",
  "amount": 5000.00
}
```

**Response 201:**
```json
{
  "id": 15,
  "sourceCurrency": "BRL",
  "targetCurrency": "USD",
  "sourceAmount": 5000.00,
  "exchangeRate": 5.4523,
  "taxRate": 0.0380,
  "taxAmount": 34.80,
  "targetAmount": 951.45,
  "conversionDate": "2025-10-06T16:30:00",
  "createdAt": "2025-10-06T16:30:05"
}
```

**Cálculo:**
- Valor em BRL: R$ 5.000,00
- Taxa de câmbio BCB: 5.4523 (BRL por USD)
- Valor convertido: 5000 / 5.4523 = 916.65 USD
- Taxa de imposto (3.8%): 916.65 × 0.038 = 34.80 USD
- **Valor final**: 951.45 USD

---

### 11. Listar Conversões do Usuário

```bash
GET http://localhost:8080/api/conversions
Authorization: Bearer {seu_token}
```

**Response 200:**
```json
[
  {
    "id": 15,
    "sourceCurrency": "BRL",
    "targetCurrency": "USD",
    "sourceAmount": 5000.00,
    "exchangeRate": 5.4523,
    "taxRate": 0.0380,
    "taxAmount": 34.80,
    "targetAmount": 951.45,
    "conversionDate": "2025-10-06T16:30:00",
    "createdAt": "2025-10-06T16:30:05"
  },
  {
    "id": 14,
    "sourceCurrency": "BRL",
    "targetCurrency": "EUR",
    "sourceAmount": 2000.00,
    "exchangeRate": 5.8910,
    "taxRate": 0.0380,
    "taxAmount": 12.92,
    "targetAmount": 352.55,
    "conversionDate": "2025-10-06T14:15:00",
    "createdAt": "2025-10-06T14:15:10"
  }
]
```

---

### 12. Buscar Conversão por ID

```bash
GET http://localhost:8080/api/conversions/15
Authorization: Bearer {seu_token}
```

**Response 200:**
```json
{
  "id": 15,
  "sourceCurrency": "BRL",
  "targetCurrency": "USD",
  "sourceAmount": 5000.00,
  "exchangeRate": 5.4523,
  "taxRate": 0.0380,
  "taxAmount": 34.80,
  "targetAmount": 951.45,
  "conversionDate": "2025-10-06T16:30:00",
  "createdAt": "2025-10-06T16:30:05"
}
```

---

## 👥 Usuários (Users)

### 13. Obter Perfil do Usuário Logado

```bash
GET http://localhost:8080/api/users/me
Authorization: Bearer {seu_token}
```

**Response 200:**
```json
{
  "id": 2,
  "email": "user@cambio.com",
  "fullName": "Usuário Teste",
  "role": "USER",
  "active": true,
  "createdAt": "2025-10-05T10:00:00"
}
```

---

### 14. Listar Todos os Usuários (ADMIN)

```bash
GET http://localhost:8080/api/users
Authorization: Bearer {admin_token}
```

**Response 200:**
```json
[
  {
    "id": 1,
    "email": "admin@cambio.com",
    "fullName": "Administrador",
    "role": "ADMIN",
    "active": true,
    "createdAt": "2025-10-05T10:00:00"
  },
  {
    "id": 2,
    "email": "user@cambio.com",
    "fullName": "Usuário Teste",
    "role": "USER",
    "active": true,
    "createdAt": "2025-10-05T10:00:00"
  }
]
```

---

### 15. Ativar/Desativar Usuário (ADMIN)

```bash
PATCH http://localhost:8080/api/users/3/toggle
Authorization: Bearer {admin_token}
```

**Response 200:**
```json
{
  "id": 3,
  "email": "joao@example.com",
  "fullName": "João Silva",
  "role": "USER",
  "active": false,
  "createdAt": "2025-10-06T15:30:00"
}
```

---

## ❌ Exemplos de Erros

### Erro de Validação (400)

```bash
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "email-invalido",
  "password": "123",
  "fullName": "AB"
}
```

**Response 400:**
```json
{
  "status": 400,
  "message": "Erro de validação",
  "timestamp": "2025-10-06T16:45:00",
  "errors": {
    "email": "Email inválido",
    "password": "Senha deve ter no mínimo 6 caracteres",
    "fullName": "Nome deve ter entre 3 e 100 caracteres"
  }
}
```

---

### Credenciais Inválidas (401)

```bash
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "user@cambio.com",
  "password": "senha_errada"
}
```

**Response 401:**
```json
{
  "status": 401,
  "message": "Email ou senha incorretos",
  "timestamp": "2025-10-06T16:50:00"
}
```

---

### Token Inválido ou Expirado (401)

```bash
GET http://localhost:8080/api/conversions
Authorization: Bearer token_invalido
```

**Response 401:**
```json
{
  "status": 401,
  "message": "Token inválido ou expirado",
  "timestamp": "2025-10-06T16:55:00"
}
```

---

### Acesso Negado (403)

```bash
POST http://localhost:8080/api/currencies
Authorization: Bearer {user_token_nao_admin}
Content-Type: application/json

{
  "code": "JPY",
  "name": "Iene",
  "symbol": "¥"
}
```

**Response 403:**
```json
{
  "status": 403,
  "message": "Acesso negado",
  "timestamp": "2025-10-06T17:00:00"
}
```

---

### Recurso Não Encontrado (404)

```bash
GET http://localhost:8080/api/currencies/999
Authorization: Bearer {seu_token}
```

**Response 404:**
```json
{
  "status": 404,
  "message": "Moeda não encontrada",
  "timestamp": "2025-10-06T17:05:00"
}
```

---

## 🧪 Postman Collection (JSON)

Importe este JSON no Postman:

```json
{
  "info": {
    "name": "Currency Exchange API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api"
    },
    {
      "key": "token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "Auth",
      "item": [
        {
          "name": "Register",
          "request": {
            "method": "POST",
            "header": [],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"test@example.com\",\n  \"password\": \"password123\",\n  \"fullName\": \"Test User\"\n}",
              "options": {
                "raw": {
                  "language": "json"
                }
              }
            },
            "url": {
              "raw": "{{baseUrl}}/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "register"]
            }
          }
        }
      ]
    }
  ]
}
```

---

## 💡 Dicas para Testes

1. **Salve o token**: Após login, salve o `accessToken` em uma variável do Postman
2. **Environment**: Crie environments separados (Dev, Staging, Prod)
3. **Tests**: Use scripts Postman para validar responses
4. **Collections**: Organize por funcionalidade
5. **Monitor**: Configure monitors para testes automatizados

---

**Happy Testing! 🎉**