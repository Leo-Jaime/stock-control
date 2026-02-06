# Sistema de Controle de Estoque

Sistema para controlar estoque de matérias-primas e produtos de uma indústria.

## Tecnologias utilizadas

- Java 17
- Quarkus 3.31.2
- Hibernate ORM + Panache
- RESTEasy Reactive (Quarkus REST)
- Jackson (JSON)
- H2 Database (desenvolvimento)
- PostgreSQL (produção)
- Maven

## Como executar

1. Clone o repositório
2. Execute o comando:

```bash
./mvnw quarkus:dev
```

3. Acesse: http://localhost:8080

## Endpoints disponíveis

### Produtos

- `GET /products` - Lista todos os produtos
- `GET /products/{id}` - Busca produto por ID
- `POST /products` - Cria novo produto

### Exemplo de requisição POST:

```json
{
  "code": "PROD001",
  "name": "Cadeira Gamer",
  "value": 899.9
}
```

## Documentação da API

Acesse: http://localhost:8080/q/swagger-ui

## Status do projeto

🚧 Em desenvolvimento
