# Finance App - Backend 🏦

Backend do Finance App desenvolvido em **Spring Boot 4.0.2** com **PostgreSQL**.

## 📋 Pré-requisitos

- **Docker & Docker Compose** ([instalar aqui](https://www.docker.com/products/docker-desktop))
- **Git**

## 🚀 Início Rápido com Docker Compose

### 1️⃣ Clone e navegue para o projeto
```bash
cd C:finance-app-back
```

### 2️⃣ Inicie os containers (API + PostgreSQL)
```powershell
docker-compose up --build
```

**Esperado:**
- PostgreSQL rodando em `localhost:5432`
- API rodando em `http://localhost:8080`
- Banco `finance` criado automaticamente
- Segmentação de dados com 7 categorias iniciais (transport, salary, etc)

**Para rodar em background:**
```powershell
docker-compose up -d --build
```

### 3️⃣ Parar os containers
```powershell
docker-compose down
```

**Remover dados (limpar volumes):**
```powershell
docker-compose down -v
```

---

## 🔗 Integração com Angular

### URL Base da API:
```
http://localhost:8080/api
```

### CORS Configurado para:
- `http://localhost:4200` (Angular dev)
- `http://localhost:3000` (alternativa)

### Exemplo: Interceptador Axios (TypeScript/Angular)

```typescript
import axios, { AxiosInstance } from 'axios';

const api: AxiosInstance = axios.create({
  baseURL: 'http://localhost:8080/api',
  timeout: 10000,
});

// Interceptador para adicionar JWT token
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Interceptador para tratar erros
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

## 📚 Fluxo de Authenticação

### 1️⃣ Registrar Usuário
```http
POST /api/users/register
Content-Type: application/json

{
  "nome": "João Silva",
  "email": "joao@example.com",
  "senha": "senha123"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "nome": "João Silva",
  "email": "joao@example.com",
  "dataInclusao": "2025-02-21T10:30:00"
}
```

### 2️⃣ Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "joao@example.com",
  "senha": "senha123"
}
```

**Response (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tipo": "Bearer",
  "userId": 1,
  "nome": "João Silva"
}
```

**Guarde o `token` no `localStorage`:**
```typescript
const response = await api.post('/auth/login', {
  email: 'joao@example.com',
  senha: 'senha123'
});

localStorage.setItem('token', response.data.token);
```

### 3️⃣ Usar Token em Requisições
```http
GET /api/users/me
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

---

## 💰 Endpoints Principais

### 📊 Transações

#### Criar Transação
```http
POST /api/transaction
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json

{
  "tipo": "D",
  "valor": 150.50,
  "idCategoria": 1,
  "descricao": "Uber para o trabalho",
  "dataTransacao": "2025-02-21"
}
```

#### Listar todas as transações
```http
GET /api/transaction
Authorization: Bearer {JWT_TOKEN}
```

#### Filtrar por tipo (Receita ou Despesa)
```http
GET /api/transaction/tipo/D
Authorization: Bearer {JWT_TOKEN}
```

#### Filtrar por mês/ano
```http
GET /api/transaction/mes?mes=2&ano=2025
Authorization: Bearer {JWT_TOKEN}
```

#### Filtrar por período
```http
GET /api/transaction/periodo?inicio=2025-01-01&fim=2025-02-28
Authorization: Bearer {JWT_TOKEN}
```

#### Filtrar por categoria
```http
GET /api/transaction/categoria/1
Authorization: Bearer {JWT_TOKEN}
```

#### Deletar transação
```http
DELETE /api/transaction/5
Authorization: Bearer {JWT_TOKEN}
```

### 📈 Dashboard

#### Resumo do mês
```http
GET /api/transaction/dashboard/resumo?mes=2&ano=2025
Authorization: Bearer {JWT_TOKEN}

Response:
{
  "mes": 2,
  "ano": 2025,
  "totalReceitas": 5000.00,
  "totalDespesas": 1850.50,
  "saldo": 3149.50,
  "despesasPorCategoria": [
    { "categoria": "transport", "total": 450.00 },
    { "categoria": "entertainment", "total": 230.50 }
  ],
  "receitasPorCategoria": [
    { "categoria": "salary", "total": 5000.00 }
  ]
}
```

#### Evolução anual (mês a mês)
```http
GET /api/transaction/dashboard/evolucao?ano=2025
Authorization: Bearer {JWT_TOKEN}

Response:
{
  "ano": 2025,
  "receitas": [
    { "mes": 1, "total": 5000.00 },
    { "mes": 2, "total": 5000.00 }
  ],
  "despesas": [
    { "mes": 1, "total": 1200.00 },
    { "mes": 2, "total": 1850.50 }
  ]
}
```

---

## 🗂️ Categorias Disponíveis

As categorias são pré-carregadas via `data.sql`:

1. **transport** - Transportes (Uber, ônibus, gasolina)
2. **entertainment** - Lazer (cinema, streaming)
3. **utilities** - Contas (luz, água, internet)
4. **healthcare** - Saúde (farmácia, médico)
5. **shopping** - Compras em geral
6. **salary** - Salário (receita)
7. **freelance** - Trabalho freelancer (receita)

**Para adicionar nova categoria (via SQL direto):**
```sql
INSERT INTO categorias (id, nome) VALUES (8, 'investment') ON CONFLICT (id) DO NOTHING;
```

Ou via interface SQL (DBeaver, pgAdmin) conectando em `localhost:5432`:
- **Database:** finance
- **User:** postgres
- **Password:** postgres123

---

## 🧪 Testar com cURL

### Registrar
```powershell
$body = @{
    nome = "João"
    email = "joao@test.com"
    senha = "senha123"
} | ConvertTo-Json

curl -X POST http://localhost:8080/api/users/register `
  -H "Content-Type: application/json" `
  -d $body
```

### Login
```powershell
$body = @{
    email = "joao@test.com"
    senha = "senha123"
} | ConvertTo-Json

$response = curl -X POST http://localhost:8080/api/auth/login `
  -H "Content-Type: application/json" `
  -d $body

$token = ($response | ConvertFrom-Json).token
Write-Host "Token: $token"
```

### Listar transações (com token)
```powershell
curl -X GET http://localhost:8080/api/transaction `
  -H "Authorization: Bearer $token"
```

---

## 📊 Estrutura do Banco de Dados

```
usuarios (id, nome, email, senha, data_inclusao)
  ↓
transacoes (id_transacao, id_usuario, id_categoria, valor, tipo, descricao, dh_transacao)
  ↓
categorias (id, nome)
```

**Tipos de Transação:**
- `R` = Receita (entrada)
- `D` = Despesa (saída)

---

## ⚙️ Variáveis de Ambiente (Configuração)

Se precisar customizar, edite o `docker-compose.yml`:

```yaml
environment:
  SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/finance
  SPRING_DATASOURCE_USERNAME: postgres
  SPRING_DATASOURCE_PASSWORD: postgres123
  JWT_SECRET: fHkl980UGl8cOfVCyfu518RnI37PtZt4C0xbJj1vw4IqdCMWlaXwgv8zGsIY8zxE
  JWT_EXPIRATION: 86400000  # 24 horas em ms
```

---

## 🐛 Troubleshooting

### "Postgres connection refused"
```powershell
# Verificar se o container está rodando
docker-compose ps

# Ver logs
docker-compose logs postgres
```

### "Port 5432 already in use"
```powershell
# Mudar porta no docker-compose.yml
ports:
  - "5433:5432"  # Usa 5433 localmente
```

### "API não responde"
```powershell
# Reiniciar
docker-compose restart app

# Ver logs da API
docker-compose logs -f app
```

### "Token expirado"
Tokens expiram em 24 horas. Faça login novamente para obter novo token.

---

## 📝 Notas

- Banco de dados **persiste** em volumes Docker (dados não são perdidos ao reiniciar)
- Para **limpar tudo**, use: `docker-compose down -v`
- JWT expire em **24h** (86400000 ms) — ajuste em `JWT_EXPIRATION` se necessário
- CORS está configurado apenas para `localhost:4200` (Angular) — adicione outras origens conforme necessário em `CorsConfig.java`

---

## 📞 Suporte

Dúvidas? Verifique:
1. Se Postgres está rodando: `docker-compose ps`
2. Logs: `docker-compose logs app` e `docker-compose logs postgres`
3. Testes com cURL (exemplos acima)

**Bom desenvolvimento!** 🚀

