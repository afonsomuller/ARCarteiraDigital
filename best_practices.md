# 🏛️ Arquitetura e Boas Práticas

## 📐 Arquitetura do Sistema

### Padrão em Camadas (Layered Architecture)

```
┌─────────────────────────────────────┐
│         CONTROLLER LAYER            │  ← Endpoints REST
│  (Validação de entrada, HTTP)       │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          SERVICE LAYER              │  ← Lógica de negócio
│  (Regras, orquestração, transações) │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│        REPOSITORY LAYER             │  ← Acesso a dados
│     (JPA, Queries, Persistência)    │
└──────────────┬──────────────────────┘
               │
┌──────────────▼──────────────────────┐
│          DATABASE (MySQL)           │
└─────────────────────────────────────┘
```

### Responsabilidades de Cada Camada

#### **Controller Layer**
- Receber requisições HTTP
- Validar entrada com `@Valid`
- Delegar para o Service
- Retornar responses HTTP apropriados
- **NÃO contém lógica de negócio**

#### **Service Layer**
- Implementar regras de negócio
- Orquestrar múltiplos repositories
- Gerenciar transações (`@Transactional`)
- Tratar exceções de negócio
- Chamar APIs externas

#### **Repository Layer**
- Acesso direto ao banco de dados
- Queries personalizadas
- Métodos CRUD do JPA

---

## 🔒 Segurança Implementada

### 1. Autenticação JWT

```java
// Fluxo de autenticação:
1. User faz login → AuthController
2. AuthService valida credenciais
3. JwtService gera access token + refresh token
4. Cliente armazena tokens
5. Requisições incluem: Authorization: Bearer {token}
6. JwtAuthenticationFilter valida token
7. SecurityContext é populado
```

### 2. Autorização por Roles

```java
@PreAuthorize("hasRole('ADMIN')")
public void adminOnlyMethod() { }

@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public void authenticatedMethod() { }
```

### 3. Proteções Implementadas

- ✅ Senhas criptografadas (BCrypt)
- ✅ Tokens com expiração
- ✅ CORS configurado
- ✅ CSRF desabilitado (API stateless)
- ✅ Validação de entrada
- ✅ SQL Injection prevenido (JPA)

---

## 🎯 Princípios SOLID Aplicados

### **S** - Single Responsibility Principle
Cada classe tem uma única responsabilidade:
- `AuthService` → Autenticação
- `ConversionService` → Conversões
- `BcbApiService` → Integração BCB

### **O** - Open/Closed Principle
Extensível sem modificação:
- Interfaces de Repository
- Strategy pattern em validações

### **L** - Liskov Substitution Principle
Implementações de `UserDetails` são intercambiáveis

### **I** - Interface Segregation Principle
Interfaces focadas e específicas (JpaRepository)

### **D** - Dependency Inversion Principle
Injeção de dependências via construtor:
```java
@RequiredArgsConstructor
public class ConversionService {
    private final ConversionRepository repository;
    private final BcbApiService bcbService;
}
```

---

## 📊 Padrões de Projeto Utilizados

### 1. **Repository Pattern**
```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

### 2. **DTO Pattern**
Separação entre entidades de banco e objetos de transferência:
```java
Entity → Service (usa Entity) → Controller (retorna DTO)
```

### 3. **Builder Pattern**
```java
User user = User.builder()
    .email("test@example.com")
    .password(encodedPassword)
    .build();
```

### 4. **Strategy Pattern**
Diferentes estratégias de logging conforme status

### 5. **Interceptor Pattern**
`LoggingInterceptor` intercepta requests para auditoria

### 6. **Facade Pattern**
Services atuam como facade para operações complexas

---

## 🧪 Estratégia de Testes

### Pirâmide de Testes

```
        /\
       /  \      E2E Tests (poucos)
      /____\
     /      \    Integration Tests (alguns)
    /________\
   /          \  Unit Tests (muitos)
  /____________\
```

### Testes Unitários

```java
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private AuthService authService;
    
    @Test
    void shouldRegisterUser_WhenEmailNotExists() {
        // Arrange, Act, Assert
    }
}
```

### Boas Práticas de Testes
- ✅ Use nomenclatura descritiva: `should...When...`
- ✅ Arrange-Act-Assert (AAA)
- ✅ Mock dependências externas
- ✅ Teste casos felizes e tristes
- ✅ Coverage mínimo: 80%

---

## 🚀 Performance e Otimização

### 1. **Queries Otimizadas**

```java
// Evite N+1 queries
@Query("SELECT u FROM User u LEFT JOIN FETCH u.conversions WHERE u.id = :id")
Optional<User> findByIdWithConversions(@Param("id") Long id);
```

### 2. **Lazy Loading**
```java
@ManyToOne(fetch = FetchType.LAZY) // Padrão, sempre use quando possível
private Currency currency;
```

### 3. **Índices no Banco**
```sql
CREATE INDEX idx_conversions_user_id ON conversions(user_id);
CREATE INDEX idx_conversions_created_at ON conversions(created_at);
```

### 4. **Paginação**
```java
Page<Conversion> findByUserId(Long userId, Pageable pageable);
```

### 5. **Cache (Futuro)**
```java
@Cacheable("exchange-rates")
public BigDecimal getExchangeRate(String currencyCode) { }
```

---

## 🔧 Tratamento de Erros

### Hierarquia de Exceções

```
Exception
  └── RuntimeException
       └── BusinessException (custom)
            ├── EntityNotFoundException
            ├── InvalidOperationException
            └── ValidationException
```

### Global Exception Handler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handle(BusinessException ex) {
        // Retorna 400 com mensagem amigável
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex) {
        // Log detalhado, retorna mensagem genérica ao cliente
    }
}
```

---

## 📝 Validações

### Bean Validation (JSR-303)

```java
public class RegisterRequest {
    @NotBlank(message = "Email é obrigatório")
    @Email(message = "Email inválido")
    private String email;
    
    @Size(min = 6, max = 100)
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d).*$")
    private String password;
}
```

### Validações Customizadas

```java
@Component
public class CurrencyValidator {
    public void validateConversion(ConversionRequest request) {
        if (request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Valor deve ser positivo");
        }
    }
}
```

---

## 🔄 Transações

### Quando Usar @Transactional

```java
@Transactional // ✅ Métodos que modificam dados
public void createConversion(ConversionRequest request) {
    // Salva conversão + log em uma transação
}

// ❌ NÃO use em métodos de leitura simples
public List<Currency> getAll() {
    return repository.findAll();
}
```

### Propagação de Transações

```java
@Transactional(propagation = Propagation.REQUIRED)     // Padrão
@Transactional(propagation = Propagation.REQUIRES_NEW) // Nova transação
@Transactional(propagation = Propagation.NESTED)       // Savepoint
```

---

## 📊 Logs e Monitoramento

### Níveis de Log

```java
log.trace("Detalhes muito granulares");
log.debug("Informação de debug - desenvolvimento");
log.info("Eventos importantes - produção");
log.warn("Situações não ideais mas não críticas");
log.error("Erros que precisam atenção", exception);
```

### Structured Logging

```java
log.info("User logged in: userId={}, ip={}", userId, ipAddress);
```

### Monitoramento Recomendado
- **Métricas**: Prometheus + Grafana
- **Logs**: ELK Stack (Elasticsearch, Logstash, Kibana)
- **APM**: New Relic, Datadog
- **Health Check**: Spring Actuator

---

## 🔐 Segurança Adicional (Recomendações)

### 1. **Rate Limiting**
```java
@RateLimiter(name = "conversion-api", fallbackMethod = "rateLimitFallback")
public ConversionResponse convert(ConversionRequest request) { }
```

### 2. **Input Sanitization**
```java
String sanitized = StringEscapeUtils.escapeHtml4(input);
```

### 3. **HTTPS Only (Produção)**
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: password
```

### 4. **Secrets Management**
- Use variáveis de ambiente
- Nunca commite senhas no código
- Use AWS Secrets Manager, Vault, etc.

```yaml
jwt:
  secret: ${JWT_SECRET:default-dev-secret}
```

---

## 🐳 Deploy e DevOps

### Docker Compose Exemplo

```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/currency_exchange_db
    depends_on:
      - db
  
  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=root
      - MYSQL_DATABASE=currency_exchange_db
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

### CI/CD Pipeline (GitHub Actions)

```yaml
name: CI/CD

on: [push]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Build with Maven
        run: mvn clean package
      - name: Run tests
        run: mvn test
```

---

## 📈 Melhorias de Escalabilidade

### 1. **Stateless Architecture**
✅ Já implementado! (JWT sem sessão)

### 2. **Database Optimization**
- Read replicas para leitura
- Particionamento de tabelas grandes
- Connection pooling (HikariCP)

### 3. **Caching Strategy**
```java
// Cache de cotações (1 minuto)
@Cacheable(value = "exchange-rates", key = "#currencyCode")
@CacheEvict(value = "exchange-rates", allEntries = true)
```

### 4. **Async Processing**
```java
@Async
public CompletableFuture<ExchangeRate> getExchangeRateAsync(String code) {
    // Processar de forma assíncrona
}
```

### 5. **Message Queue**
Para operações pesadas, use RabbitMQ/Kafka:
```
Request → API → Queue → Worker → Database
```

---

## ✅ Checklist de Produção

### Antes de Deploy
- [ ] Testes passando (>80% coverage)
- [ ] Logs configurados corretamente
- [ ] Secrets em variáveis de ambiente
- [ ] HTTPS habilitado
- [ ] CORS configurado para domínios corretos
- [ ] Rate limiting implementado
- [ ] Health checks funcionando
- [ ] Backup automático do banco
- [ ] Monitoramento configurado
- [ ] Documentação atualizada

### Performance
- [ ] Índices criados no banco
- [ ] Queries otimizadas (sem N+1)
- [ ] Connection pool configurado
- [ ] Timeout configurado em chamadas externas
- [ ] Cache implementado onde necessário

### Segurança
- [ ] Dependency check (vulnerabilidades)
- [ ] Input validation em todos os endpoints
- [ ] SQL Injection prevenido
- [ ] XSS prevenido
- [ ] CSRF configurado apropriadamente
- [ ] Logs não expõem dados sensíveis

---

## 🎓 Referências e Recursos

### Documentação Oficial
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)

### Boas Práticas
- [12 Factor App](https://12factor.net/)
- [REST API Best Practices](https://restfulapi.net/)
- [Clean Code](https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882)

### Arquitetura
- [Domain-Driven Design](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Microservices Patterns](https://microservices.io/patterns/)

---

## 💡 Dicas Finais

1. **Mantenha o código simples**: Complexidade é inimiga da manutenção
2. **Documente decisões**: README, comentários em código complexo
3. **Teste primeiro**: TDD quando possível
4. **Refatore constantemente**: Technical debt acumula rápido
5. **Monitore em produção**: Logs e métricas são essenciais
6. **Aprenda continuamente**: Tecnologia evolui rápido

---

**Pronto para produção! 🚀**