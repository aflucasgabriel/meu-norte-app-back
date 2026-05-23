# 🚀 Deploy em Railway (Opcional)

Se você quiser um link online para compartilhar com o professor, use **Railway** (grátis para projetos acadêmicos).

## ⚡ 5 Minutos no Railway

### 1. Pré-requisitos
- Conta no GitHub com seu repo `finance-app-back`
- Conta Railway (grátis)

### 2. Criar Deploy

1. Acesse: https://railway.app
2. Clique em **"Create New Project"**
3. Selecione **"Deploy from GitHub"**
4. Conecte sua conta GitHub
5. Selecione o repo `finance-app-back`
6. Clique em **Deploy**

**Pronto!** Railway vai:
- Detectar Spring Boot
- Fazer build automaticamente
- Rodar seu app

### 3. Configurar Banco de Dados

1. No painel do Railway, clique em **"Add Plugin"**
2. Selecione **"PostgreSQL"**
3. Railway vai criar DB automaticamente

### 4. Configurar Variáveis de Ambiente

1. Vá em **Variables** (no painel)
2. Adicione:
```
SPRING_DATASOURCE_URL=postgresql://<host>:<port>/<database>
SPRING_DATASOURCE_USERNAME=<username>
SPRING_DATASOURCE_PASSWORD=<password>
JWT_SECRET=seu_secret_aqui
JWT_EXPIRATION=86400000
```

**Railway injeta automaticamente** as credenciais do PostgreSQL. Basta confirmar.

### 5. Pronto! 🎉

Sua API está online em:
```
https://seu-projeto-XXXXX.railway.app/api
```

---

## 🔗 Testar URL Online

```powershell
# Teste se está respondendo
curl https://seu-projeto-XXXXX.railway.app/api/users/register

# Exemplo com jq (pretty print):
curl https://seu-projeto-XXXXX.railway.app/api/auth/login `
  -H "Content-Type: application/json" `
  -d '{"email":"teste@test.com","senha":"123"}'
```

---

## 📝 Compartilhar URL com Professor

Copie o link e compartilhe:
```
https://seu-projeto-XXXXX.railway.app/api
```

Seu Angular pode chamar de qualquer lugar agora! 🌍

---

## 💡 Notas

- Railway oferece **free tier generoso** para estudiantes
- Se acabar crédito gratuito, você pode adicionar cartão de crédito
- Alternativamente, tente **Render** ou **Heroku**
- Lembre-se: dados no banco de dados persistem entre deploys

---

## 🆚 Docker Local vs Railway Online

| Aspecto | Docker Local | Railway |
|--------|-------------|---------|
| Setup | 5 min | 3 min |
| URL | localhost:8080 | https://... |
| Persistência | Local | Nuvem |
| Acesso | Apenas local | Qualquer lugar |
| Custo | 0 | 0 (free tier) |
| Ideal para | Desenvolvimento | Apresentação |

---

## 🚨 Se der problema no Railway

Verifique:
1. GitHub está conectado à Railway
2. PostgreSQL foi adicionado como plugin
3. Variáveis de ambiente estão configuradas
4. Build logs: clique em **View Build Logs**

Se tiver erro, consulte [TROUBLESHOOTING.md](./TROUBLESHOOTING.md).

