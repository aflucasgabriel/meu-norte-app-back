# 🆘 Troubleshooting & FAQ

## Problemas Comuns

### 1. Docker / Docker Compose

#### ❌ "docker-compose: command not found"
```
Docker Desktop não está instalado ou não está no PATH
```
**Solução:**
- Instale Docker Desktop: https://www.docker.com/products/docker-desktop
- Reinicie o terminal após instalar
- Verifique: `docker --version` e `docker-compose --version`

---

#### ❌ "Port 5432 already in use"
```
Outro serviço PostgreSQL já está rodando na porta 5432
```
**Soluções:**

A) Usar outra porta - edite `docker-compose.yml`:
```yaml
services:
  postgres:
    ports:
      - "5433:5432"  # Mude para 5433
```

B) Parar o serviço existente:
```powershell
# Se foi outro Docker:
docker-compose down

# Se foi PostgreSQL instalado localmente:
# Windows: Services > PostgreSQL > Stop
```

---

#### ❌ "Port 8080 already in use"
```
Outra aplicação Java está usando a porta 8080
```
**Solução:**
- Edite `docker-compose.yml`:
```yaml
services:
  app:
    ports:
      - "8081:8080"  # Mude para 8081
```

Então acesse: `http://localhost:8081/api`

---

#### ❌ "Postgres connection refused"
```
Banco de dados não está respondendo
```
**Debug:**
```powershell
# Ver status dos containers
docker-compose ps

# Ver logs do PostgreSQL
docker-compose logs postgres

# Reiniciar
docker-compose restart postgres
```

---

#### ❌ "Build failed" ou "Gradle build error"
```
Erro durante a compilação do JAR
```
**Soluções:**
```powershell
# Limpar cache e tentar novamente
docker-compose down -v
docker-compose up --build

# Ou build manual primeiro
.\gradlew.bat clean build -x test
docker-compose up
```

---

### 2. Banco de Dados

#### ❌ "Erro: Database 'finance' does not exist"
```
O banco não foi criado ou foi deletado
```
**Solução:**
```powershell
# Remover volumes e recriar
docker-compose down -v
docker-compose up
```

O banco será criado automaticamente na primeira execução.

---

#### ❌ Conectar ao PostgreSQL direto (DBeaver, pgAdmin)

**Detalhes de conexão:**
```
Host: localhost
Port: 5432
Database: finance
User: postgres
Password: postgres123
```

**Teste a conexão:**
```powershell
# Instale psql (cliente PostgreSQL)
# Windows: https://www.postgresql.org/download/windows/

# Conecte:
psql -h localhost -U postgres -d finance

# Ou via Docker:
docker exec -it finance-app-db psql -U postgres -d finance
```

---

### 3. API / Endpoints

#### ❌ "Connection refused" ao chamar API
```
API não está rodando
```
**Verificação:**
```powershell
# Checar se container está rodando
docker-compose ps

# Testar se API responde
curl http://localhost:8080/api/users/register
```

**Se não responder:** `docker-compose logs app`

---

#### ❌ "401 Unauthorized"
```
Token ausente, inválido ou expirado
```
**Soluções:**

1. **Token não enviado:**
   ```
   Authorization: Bearer seu_token_aqui
   ```

2. **Token expirado (24h):**
   - Faça login novamente para obter novo token

3. **Token inválido:**
   - Verifique se (copiou corretamente - sem espaços extras)

---

#### ❌ "CORS error" no Angular
```
Angular não consegue chamar a API
```
**Mensagem típica:**
```
Access to XMLHttpRequest at 'http://localhost:8080/api/...'
from origin 'http://localhost:4200' has been blocked by CORS policy
```

**Soluções:**

A) **Verificar se CORS está ativado:**
- Arquivo: `src/main/java/com/ufu/finance/config/CorsConfig.java`
- Deve ter `@Configuration` e métodos de CORS

B) **Adicionar mais origens** (se necessário):
```java
.allowedOrigins(
    "http://localhost:4200",
    "http://localhost:3000",
    "http://seu-dominio.com"  // Adicione aqui
)
```

C) **Rebuildar a API:**
```powershell
docker-compose down
docker-compose up --build
```

D) **Verificar headers de resposta:**
```powershell
curl -i http://localhost:8080/api/users
# Procure por: Access-Control-Allow-...
```

---

#### ❌ "400 Bad Request" com validação

Exemplo de erro:
```json
{
  "status": 400,
  "erro": "Dados inválidos",
  "campos": {
    "email": "Email deve ser válido",
    "nome": "Nome deve ter entre 3 e 100 caracteres"
  }
}
```

**Soluções:**
- Verifique os campos obrigatórios
- Valide os tipos de dados
- Consulte a documentação de DTOs em [README.md](./README.md#-data-transfer-objects-dtos)

---

### 4. Frontend Angular

#### ❌ "CORS Headers não aparecem"

Teste manualmente:
```powershell
curl -X OPTIONS http://localhost:8080/api/transaction \
  -H "Origin: http://localhost:4200" \
  -H "Access-Control-Request-Method: GET" \
  -v
```

Deve retornar headers como:
```
Access-Control-Allow-Origin: http://localhost:4200
Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS
```

---

#### ❌ "Token não persiste" no Angular

Verifique localStorage:
```typescript
// No console do navegador (F12)
localStorage.getItem('token')
```

Se estiver nulo, significa:
- Token não foi salvo após login
- Verifique seu serviço Angular: `localStorage.setItem('token', response.data.token)`

---

### 5. Performance / Lentidão

#### ⚠️ API lenta para responder

**Diagnóstico:**
```powershell
# Ver logs em tempo real
docker-compose logs -f app

# Ver recursos dos containers
docker stats
```

**Possíveis causas:**
- BD ainda inicializando na primeira execução
- Falta de memória/CPU
- Query de banco pesada

**Soluções:**
```powershell
# Aumentar limites de memória no docker-compose.yml:
services:
  app:
    environment:
      JAVA_OPTS: "-Xmx1g -Xms512m"
```

---

## ❓ FAQ

### P: Como adicionar uma nova categoria?

**R:** Via SQL direto no banco:
```sql
INSERT INTO categorias (id, nome) VALUES (8, 'investment') ON CONFLICT (id) DO NOTHING;
```

Ou edite `src/main/resources/data.sql` e reinicie.

---

### P: Como mudar a senha de um usuário?

**R:** Atualmente não há endpoint. Opções:

1. **SQL direto:**
   ```sql
   UPDATE usuarios SET senha = 'nova_hash' WHERE id = 1;
   ```

2. **Adicionar endpoint:**
   - Criar `/api/users/{id}/change-password`
   - Solicitar senha antiga, validar, atualizar com BCrypt

---

### P: Como fazer backup do banco?

**R:**
```powershell
# Backup
docker exec finance-app-db pg_dump -U postgres finance > backup.sql

# Restaurar
docker exec -i finance-app-db psql -U postgres finance < backup.sql
```

---

### P: Posso usar outro BD que não PostgreSQL?

**R:** Sim, mas é necessário:
1. Adicionar driver no `build.gradle`
2. Atualizar `application.yaml` com URL/credenciais
3. Mudar `database-platform` em `application.yaml`
4. Testar queries SQL (que usam PostgreSQL agora)

**Exemplo para H2 (em memória, só para testes):**
```gradle
runtimeOnly 'com.h2database:h2'
```

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:finance
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

---

### P: O token JWT expira muito rápido/lento. Como mudar?

**R:** Edite `docker-compose.yml`:
```yaml
environment:
  JWT_EXPIRATION: 3600000  # 1 hora (em ms)
  # 86400000 = 24 horas (padrão)
  # 604800000 = 7 dias
```

Ou edite `application.yaml`:
```yaml
jwt:
  expiration: 3600000
```

---

### P: Como colocar a API em produção?

**R:** Arquitetura recomendada:
1. **Dockerfile** já pronto em `./Dockerfile`
2. Deploy para:
   - **Railway.app** (mais simples para acadêmico)
   - **Heroku** (pode custar)
   - **AWS ECS** / **Google Cloud Run** / **Azure Container Instances**
   - **VPS com Docker**

**Para Railway (5 min):**
- Push no GitHub
- Conecte repo em railway.app
- Railway detecta Spring Boot + cria deploy
- Pronto! URL públicanastematica

---

### P: Posso usar sem Docker?

**R:** Sim:
1. Instale PostgreSQL 12+
2. Crie banco: `CREATE DATABASE finance;`
3. Configure `application.yaml` com credenciais locais
4. Execute: `.\gradlew.bat bootRun`

---

### P: Preciso de documentação Swagger?

**R:** Sim! Pode adiciona:

1. Adicione ao `build.gradle`:
```gradle
implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.0.0'
```

2. Acesse: `http://localhost:8080/swagger-ui.html`

---

### P: Como adicionar testes unitários?

**R:** Exemplo básico em `src/test/java`:
```java
@SpringBootTest
public class UsuarioServiceTest {

    @Autowired
    private UsuarioService usuarioService;

    @Test
    public void testCadastrarUsuario() {
        UserDTO dto = new UserDTO("João", "joao@test.com", "senha123");
        UserResponseDTO result = usuarioService.cadastrarUsuario(dto);
        
        assertNotNull(result.getId());
        assertEquals("João", result.getNome());
    }
}
```

---

## 📞 Ainda com dúvidas?

1. Consulte [DOCKER_SETUP.md](./DOCKER_SETUP.md)
2. Consulte [QUICK_START.md](./QUICK_START.md)
3. Consulte [README.md](./README.md)
4. Verifique logs: `docker-compose logs -f`

**Bom debugging! 🔍**

