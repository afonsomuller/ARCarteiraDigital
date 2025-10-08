# 📮 Guia Completo de Testes no Postman - ARCarteiraDigital

## 🔧 Configuração Inicial

### Base URL
```
http://localhost:8080/api
```

---

## 1️⃣ REGISTRAR USUÁRIO

### Request
```
POST http://localhost:8080/api/auth/register
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "email": "joao@example.com",
  "password": "senha123",
  "nome": "João Silva"
}
```

### Response Esperada (201 Created)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJqb2FvQGV4YW1wbGUuY29tIiwiaWF0IjoxNzMwOTk5MDAwLCJleHAiOjE3MzEwODU0MDB9.signature",
  "tipo": "Bearer",
  "userId": 1,
  "email": "joao@example.com",
  "nome": "João Silva"
}
```

**⚠️ IMPORTANTE:** Copie o `token` da resposta! Você vai precisar dele.

---

## 2️⃣ LOGIN

### Request
```
POST http://localhost:8080/api/auth/login
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

### Response Esperada (200 OK)
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "userId": 1,
  "email": "joao@example.com",
  "nome": "João Silva"
}
```

---

## 3️⃣ CRIAR CARTEIRA

### Request
```
POST http://localhost:8080/api/carteiras
Content-Type: application/json
Authorization: Bearer {SEU_TOKEN_AQUI}
```

### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "nome": "Carteira Principal",
  "saldo": 1000.00
}
```

### Response Esperada (201 Created)
```json
{
  "id": 1,
  "nome": "Carteira Principal",
  "saldo": 1000.00,
  "ativa": true,
  "criadaEm": "2025-10-07T14:30:00",
  "atualizadaEm": "2025-10-07T14:30:00"
}
```

---

## 4️⃣ LISTAR CARTEIRAS

### Request
```
GET http://localhost:8080/api/carteiras
Authorization: Bearer {SEU_TOKEN_AQUI}
```

### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Esperada (200 OK)
```json
[
  {
    "id": 1,
    "nome": "Carteira Principal",
    "saldo": 1000.00,
    "ativa": true,
    "criadaEm": "2025-10-07T14:30:00",
    "atualizadaEm": "2025-10-07T14:30:00"
  },
  {
    "id": 2,
    "nome": "Carteira Secundária",
    "saldo": 500.00,
    "ativa": true,
    "criadaEm": "2025-10-07T14:35:00",
    "atualizadaEm": "2025-10-07T14:35:00"
  }
]
```

---

## 5️⃣ BUSCAR CARTEIRA POR ID

### Request
```
GET http://localhost:8080/api/carteiras/1
Authorization: Bearer {SEU_TOKEN_AQUI}
```

### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Esperada (200 OK)
```json
{
  "id": 1,
  "nome": "Carteira Principal",
  "saldo": 1000.00,
  "ativa": true,
  "criadaEm": "2025-10-07T14:30:00",
  "atualizadaEm": "2025-10-07T14:30:00"
}
```

---

## 6️⃣ ATUALIZAR CARTEIRA

### Request
```
PUT http://localhost:8080/api/carteiras/1
Content-Type: application/json
Authorization: Bearer {SEU_TOKEN_AQUI}
```

### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```

### Body (raw JSON)
```json
{
  "nome": "Carteira Principal Atualizada",
  "saldo": 1500.00
}
```

### Response Esperada (200 OK)
```json
{
  "id": 1,
  "nome": "Carteira Principal Atualizada",
  "saldo": 1500.00,
  "ativa": true,
  "criadaEm": "2025-10-07T14:30:00",
  "atualizadaEm": "2025-10-07T15:00:00"
}
```

---

## 7️⃣ DELETAR CARTEIRA

### Request
```
DELETE http://localhost:8080/api/carteiras/1
Authorization: Bearer {SEU_TOKEN_AQUI}
```

### Headers
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Response Esperada (204 No Content)
```
(corpo vazio)
```

---

## ❌ EXEMPLOS DE ERROS

### Erro 1: Sem Token (401)

**Request:**
```
GET http://localhost:8080/api/carteiras
(sem header Authorization)
```

**Response:**
```json
{
  "status": 401,
  "message": "Full authentication is required",
  "timestamp": "2025-10-07T15:30:00"
}
```

---

### Erro 2: Email já cadastrado (400)

**Request:**
```
POST http://localhost:8080/api/auth/register
{
  "email": "joao@example.com",
  "password": "senha123",
  "nome": "Outro João"
}
```

**Response:**
```json
{
  "status": 400,
  "message": "Email já cadastrado",
  "timestamp": "2025-10-07T15:35:00"
}
```

---

### Erro 3: Credenciais Inválidas (401)

**Request:**
```
POST http://localhost:8080/api/auth/login
{
  "email": "joao@example.com",
  "password": "senha_errada"
}
```

**Response:**
```json
{
  "status": 401,
  "message": "Email ou senha incorretos",
  "timestamp": "2025-10-07T15:40:00"
}
```

---

### Erro 4: Validação (400)

**Request:**
```
POST http://localhost:8080/api/carteiras
Authorization: Bearer {token}
{
  "nome": "",
  "saldo": -100
}
```

**Response:**
```json
{
  "status": 400,
  "message": "Erro de validação",
  "timestamp": "2025-10-07T15:45:00",
  "errors": {
    "nome": "Nome é obrigatório",
    "saldo": "Saldo não pode ser negativo"
  }
}
```

---

## 🎯 FLUXO COMPLETO DE TESTE

### 1. Registrar
```bash
POST /api/auth/register
```

### 2. Fazer Login (pegar token)
```bash
POST /api/auth/login
```

### 3. Criar 2 Carteiras
```bash
POST /api/carteiras (Carteira 1)
POST /api/carteiras (Carteira 2)
```

### 4. Listar Carteiras
```bash
GET /api/carteiras
```

### 5. Buscar Carteira por ID
```bash
GET /api/carteiras/1
```

### 6. Atualizar Carteira
```bash
PUT /api/carteiras/1
```

### 7. Deletar Carteira
```bash
DELETE /api/carteiras/2
```

### 8. Listar Novamente (verificar exclusão)
```bash
GET /api/carteiras
```

---

## 📦 COLLECTION POSTMAN (JSON)

Para importar no Postman, salve como `ARCarteiraDigital.postman_collection.json`:

```json
{
  "info": {
    "name": "ARCarteiraDigital",
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
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"joao@example.com\",\n  \"password\": \"senha123\",\n  \"nome\": \"João Silva\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/auth/register",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "register"]
            }
          }
        },
        {
          "name": "Login",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"email\": \"joao@example.com\",\n  \"password\": \"senha123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/auth/login",
              "host": ["{{baseUrl}}"],
              "path": ["auth", "login"]
            }
          }
        }
      ]
    },
    {
      "name": "Carteiras",
      "item": [
        {
          "name": "Criar Carteira",
          "request": {
            "method": "POST",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"nome\": \"Carteira Principal\",\n  \"saldo\": 1000.00\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/carteiras",
              "host": ["{{baseUrl}}"],
              "path": ["carteiras"]
            }
          }
        },
        {
          "name": "Listar Carteiras",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/carteiras",
              "host": ["{{baseUrl}}"],
              "path": ["carteiras"]
            }
          }
        }
      ]
    }
  ]
}
```

---

## 💡 DICAS POSTMAN

### Salvar Token Automaticamente

No request de **Login**, vá em **Tests** e adicione:

```javascript
var jsonData = pm.response.json();
pm.environment.set("token", jsonData.token);
```

Assim o token é salvo automaticamente após o login!

---

**Pronto para testar! 🚀**