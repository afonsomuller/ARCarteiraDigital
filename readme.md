# 💱 Currency Exchange API

API RESTful completa para conversão de moedas com integração ao Banco Central do Brasil, autenticação JWT e auditoria de operações.

## 🚀 Tecnologias

- **Java 17+**
- **Spring Boot 3.2.0**
- **Spring Security + JWT**
- **Spring Data JPA**
- **MySQL 8.0**
- **WebClient** (chamadas HTTP reativas)
- **Swagger/OpenAPI** (documentação automática)
- **Lombok** (redução de boilerplate)
- **MapStruct** (mapeamento de DTOs)
- **JUnit 5 + Mockito** (testes)

## 📋 Funcionalidades

### ✅ Autenticação e Autorização
- Registro de usuários
- Login com JWT
- Refresh token
- Controle de acesso baseado em roles (USER/ADMIN)

### 💰 Gestão de Moedas (CRUD completo)
- Criar, listar, atualizar e deletar moedas
- Ativar/desativar moedas
- Buscar por código ou ID

### 🔄 Conversão de Moedas
- Consulta em tempo real à API do Banco Central
- Cálculo automático de impostos
- Histórico de conversões por usuário
- Registro de cotação e data/hora

### 👥 Gestão de Usuários
- CRUD completo (apenas ADMIN)
- Perfil do usuário logado
- Ativar/desativar usuários

### 📊 Auditoria (Log de Operações)
- Registro automático de todas as operações
- Rastreamento de endpoint, método HTTP, status
- IP, User-Agent e tempo de execução
- Armazenamento de request/response (opcional)

## 🏗️ Arquitetura

```
src/main/java/com/cambio/
├── config/              # Configurações (Security, Swagger, Web)
├── controller/          # Controllers REST
├── dto/                 # Data Transfer Objects
├── exception/           # Tratamento de exceções
├── model/               # Entidades JPA
├── repository/          # Repositories JPA
├── security/            # JWT Service e Filtros
└── service/             # Lógica de negócio
```

### 📦 Entidades e Relacionamentos

```
User (1) ----< (N) Conversion
User (1) ----< (N) LogOperation
Currency (1) ----< (N) Conversion (source)
Currency (1) ----< (N) Conversion (target)
```

## 🔧 Configuração

### 1. Banco de Dados MySQL

Crie o banco de dados:

```sql
CREATE DATABASE currency_exchange_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. Configurar application.yml

Edite as credenciais do banco em `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/currency_exchange_db
    username: seu_usuario
    password: sua_senha
```

### 3. Executar o Projeto

```bash
# Com Maven
./mvnw spring-boot:run

# Ou compilar e executar
./mvnw clean package
java -jar target/currency-exchange-api-1.0.0.jar
```

A aplicação estará rodando em: **http://localhost:8080/api**

## 📚 Documentação da API (Swagger)

Acesse a documentação interativa:

**http://localhost:8080/api/swagger-ui.html**

## 🔑 Usuários Iniciais

O sistema cria automaticamente dois usuários:

| Email | Senha | Role |
|-------|-------|------|
| admin@cambio.com | admin123 | ADMIN |
| user@cambio.com | user123 | USER |

## 🌐 Endpoints Principais

### Autenticação

```http
POST /api/auth/register
POST /api/auth/login
```

### Conversões

```http
POST   /api/conversions              # Realizar conversão
GET    /api/conversions              # Listar conversões do usuário
GET    /api/conversions/{id}         # Buscar conversão por ID
```

### Moedas

```http
GET    /api/currencies               # Listar todas
GET    /api/currencies/active        # Listar ativas
GET    /api/currencies/{id}          # Buscar por ID
GET    /api/currencies/code/{code}   # Buscar por código
POST   /api/currencies               # Criar (ADMIN)
PUT    /api/currencies/{id}          # Atualizar (ADMIN)
PATCH  /api/currencies/{id}/toggle   # Ativar/Desativar (ADMIN)
DELETE /api/currencies/{id}          # Deletar (ADMIN)
```

### Usuários

```http
GET    /api/users/me                 # Perfil do usuário logado
GET    /api/users                    # Listar todos (ADMIN)
GET    /api/users/{id}               # Buscar por ID (ADMIN)
PATCH  /api/users/{id}/toggle        # Ativar/Desativar (ADMIN)
DELETE /api/users/{id}               # Deletar (ADMIN)
```

## 🧪 Exemplos de Uso

### 1. Registrar Usuário

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "novo@example.com",
    "password": "senha123",
    "fullName": "Novo Usuário"
  }'
```

### 2. Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@cambio.com",
    "password": "user123"
  }'
```

Resposta:
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 86400000,
  "user": {
    "id": 2,
    "email": "user@cambio.com",
    "fullName": "Usuário Teste",
    "role": "USER"
  }
}
```

### 3. Converter Moeda

```bash
curl -X POST http://localhost:8080/api/conversions \
  -H "Authorization: Bearer SEU_TOKEN_AQUI" \
  -H "Content-Type: application/json" \
  -d '{
    "sourceCurrencyCode": "BRL",
    "targetCurrencyCode": "USD",
    "amount": 1000.00
  }'
```

Resposta:
```json
{
  "id": 1,
  "sourceCurrency": "BRL",
  "targetCurrency": "USD",
  "sourceAmount": 1000.00,
  "exchangeRate": 5.4523,
  "taxRate": 0.0380,
  "taxAmount": 6.96,
  "targetAmount": 190.29,
  "conversionDate": "2025-10-06T15:30:00",
  "createdAt": "2025-10-06T15:30:01"
}
```

## 🔐 Segurança

- Senhas criptografadas com **BCrypt**
- Autenticação stateless com **JWT**
- Proteção CSRF desabilitada (API REST)
- Tokens com expiração configurável
- Validação de entrada com **Bean Validation**

## 📊 Integração com Banco Central

A API consulta cotações em tempo real da API do Banco Central:

**Endpoint Base:** `https://olinda.bcb.gov.br/olinda/servico/PTAX/versao/v1/odata`

**Códigos de Moeda (BCB):**
- USD: Dólar Americano
- EUR: Euro
- GBP: Libra Esterlina
- ARS: Peso Argentino

## 🧪 Testes

Executar todos os testes:

```bash
./mvnw test
```

Executar com coverage:

```bash
./mvnw test jacoco:report
```

## 🚀 Melhorias Futuras

### Funcionalidades
- [ ] Cache de cotações (Redis)
- [ ] Autenticação OAuth2/Google
- [ ] Two-Factor Authentication (2FA)
- [ ] Notificações por email
- [ ] Webhooks para conversões
- [ ] Exportação de relatórios (PDF/Excel)
- [ ] Dashboard com gráficos

### Performance
- [ ] Paginação nos endpoints de listagem
- [ ] Compressão de responses (GZIP)
- [ ] Rate limiting por usuário
- [ ] Circuit breaker na integração BCB

### DevOps
- [ ] Docker e Docker Compose
- [ ] CI/CD com GitHub Actions
- [ ] Monitoramento com Prometheus/Grafana
- [ ] Logs centralizados (ELK Stack)

## 📝 Licença

Este projeto está sob a licença MIT.

## 👨‍💻 Autor

Desenvolvido com ☕ e ❤️