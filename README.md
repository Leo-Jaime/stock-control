# Sistema de Controle de Estoque (Stock Control)

[![React](https://img.shields.io/badge/Frontend-React%20%2B%20Vite-61DAFB?logo=react)](https://stock-control-five.vercel.app/)
[![Java](https://img.shields.io/badge/Backend-Java%2017%20%2B%20Quarkus-4695EB?logo=openjdk)](https://stock-control-api-me9f.onrender.com/q/swagger-ui/)
[![PostgreSQL](https://img.shields.io/badge/Database-PostgreSQL-336791?logo=postgresql)](https://www.postgresql.org/)

> **Acesse a aplicação online:**  
> 🔗 **[https://stock-control-five.vercel.app](https://stock-control-five.vercel.app)**
>
> **Credenciais de Acesso (Demo):**  
> 👤 Usuário: `admin`  
> 🔑 Senha: `admin123`

---

## 📌 Sobre o Projeto

Sistema completo para controle de estoque de matérias-primas e produtos de uma indústria, com cálculo automático de potencial de produção.

Desenvolvido como **Teste Full Stack**, utilizando as melhores práticas como **Clean Architecture**, **DTOs**, **JWT Authentication** e **Design System** customizado.

### 🚀 Stack Tecnológico

**Frontend (Vercel):**
- React + TypeScript + Vite
- Context API (Auth, Theme, Toast)
- CSS Modules (Design System próprio, responsivo)
- React Router DOM (Rotas protegidas)

**Backend (Render):**
- Java 17 + Quarkus 3.31
- Hibernate ORM + Panache
- RESTEasy Reactive
- SmallRye JWT (Autenticação RBAC)
- PostgreSQL (Docker em Dev / Render em Prod)

---

## 📸 Screenshots

| Login | Dashboard |
|---|---|
| ![Login](/docs/images/login.png) | ![Dashboard](/docs/images/dashboard.png) |

| Produtos | Matérias-Primas |
|---|---|
| ![Produtos](/docs/images/produtos.png) | ![Matérias-Primas](/docs/images/materias-primas.png) |

| Associação Matéria-Prima | Plano de Produção |
|---|---|
| ![Associação](/docs/images/associar-materia-prima.png) | ![Produção](/docs/images/plano-de-producao.png) |

---

## ✨ Funcionalidades

- **Autenticação Segura**: Login com JWT, proteção de rotas, auto-logout e persistência de sessão.
- **Dashboard**: Visão geral com cards estatísticos e tabelas de alertas (estoque baixo).
- **CRUD de Produtos**: Cadastro, edição e remoção com validações.
- **CRUD de Matérias-primas**: Gestão de estoque com badges de status.
- **Associação Inteligente**: Vincular matérias-primas a produtos (NxN) com quantidade necessária.
- **Cálculo de Produção**: Algoritmo que analisa o estoque atual e sugere o máximo de produtos possíveis de serem fabricados.
- **Dark Mode**: Tema claro/escuro persistente.

---

## 🛠️ Como executar localmente

### Pré-requisitos
- Java 17+
- Node.js 18+
- Docker

### 1. Banco de Dados
Suba o PostgreSQL via Docker:
```bash
docker compose up -d
```

### 2. Backend (Quarkus)
```bash
cd backend
./mvnw quarkus:dev
```
O servidor iniciará em `http://localhost:8080`.
*Documentação Swagger disponível em: `http://localhost:8080/q/swagger-ui`*

### 3. Frontend (React)
```bash
cd frontend
npm install
npm run dev
```
Acesse a aplicação em `http://localhost:5173`.

---

## 📂 Estrutura do Projeto

```
stock-control/
├── backend/                 # API Quarkus
│   ├── src/main/java/com/stockcontrol/
│   │   ├── config/          # Configurações (CORS, JWT)
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA (User, Product...)
│   │   ├── resource/        # Controllers REST
│   │   └── service/         # Regras de Negócio e Auth
│   └── Dockerfile           # Build Multi-stage para Prod
│
├── frontend/                # SPA React
│   ├── src/
│   │   ├── api/             # Camada de HTTP (Axios/Fetch)
│   │   ├── components/      # UI Kit reutilizável
│   │   ├── contexts/        # Estado Global (Auth, Theme)
│   │   └── pages/           # Telas do sistema
│   └── vite.config.ts       # Proxy reverso para Dev
│
└── docker-compose.yaml      # Infraestrutura local
```

---

## 🔒 Segurança

O sistema implementa segurança em camadas:
1. **Frontend**: Rotas protegidas (`ProtectedRoute`), redirecionamento automático se token expirar (401).
2. **Backend**: Endpoints protegidos com `@RolesAllowed("user")`, senhas com **Bcrypt**, tokens assinados com **RSA-256**.
3. **Infra**: Variáveis de ambiente para credenciais de banco e chaves secretas.

---

Desenvolvido por **[Leo Jaime](https://leo-portifolio.vercel.app/)**
