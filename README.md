# Sistema de Controle de Estoque

Sistema para controlar estoque de matérias-primas e produtos de uma indústria.

## Tecnologias utilizadas

- Java 17
- Quarkus 3.31.2
- Hibernate ORM + Panache
- RESTEasy Reactive (Quarkus REST)
- Jackson (JSON)
- PostgreSQL 15 (Docker)
- Maven

## Como executar

1. Clone o repositório
2. Suba o banco de dados:

```bash
docker compose up -d
```

3. Execute a aplicação:

```bash
./mvnw quarkus:dev
```

4. Acesse: http://localhost:8080

## Endpoints disponíveis

### Produtos

- `GET /products` - Lista todos os produtos
- `GET /products/{id}` - Busca produto por ID
- `POST /products` - Cria novo produto
- `PUT /products/{id}` - Atualiza produto
- `DELETE /products/{id}` - Deleta produto

**Exemplo de requisição POST:**

```json
{
  "code": "PROD001",
  "name": "Cadeira Gamer",
  "value": 899.9
}
```

### Matérias-primas

- `GET /raw-materials` - Lista todas as matérias-primas
- `GET /raw-materials/{id}` - Busca matéria-prima por ID
- `POST /raw-materials` - Cria nova matéria-prima
- `PUT /raw-materials/{id}` - Atualiza matéria-prima
- `DELETE /raw-materials/{id}` - Deleta matéria-prima

**Exemplo de requisição POST:**

```json
{
  "code": "MAT001",
  "name": "Madeira",
  "stockQuantity": 100
}
```

### Associação Produto-Matéria Prima

- `GET /products/{productId}/raw-materials` - Lista matérias-primas de um produto
- `POST /products/{productId}/raw-materials` - Associa matéria-prima ao produto
- `PUT /products/{productId}/raw-materials/{associationId}` - Atualiza quantidade necessária
- `DELETE /products/{productId}/raw-materials/{associationId}` - Remove associação

**Exemplo de requisição POST:**

```json
{
  "rawMaterialId": 1,
  "requiredQuantity": 5
}
```

### Cálculo de Produção

- `GET /production/calculate` - Calcula produtos que podem ser produzidos com estoque atual

**Resposta exemplo:**

```json
{
  "suggestions": [
    {
      "productId": 1,
      "productCode": "PROD001",
      "productName": "Cadeira Gamer",
      "productValue": 899.9,
      "quantityCanProduce": 20,
      "totalValue": 17998.0
    }
  ],
  "totalProductionValue": 17998.0,
  "totalProducts": 1
}
```

## Documentação da API

Acesse: http://localhost:8080/q/swagger-ui

## Estrutura do projeto

```
src/
├── main/
│   ├── java/com/stockcontrol/
│   │   ├── dto/             # Data Transfer Objects
│   │   ├── entity/          # Entidades JPA
│   │   ├── exception/       # Exceções customizadas
│   │   └── resource/        # Endpoints REST
│   └── resources/
│       └── application.properties
docker-compose.yaml              # PostgreSQL
```

## Status do projeto

✅ Backend completo - API REST pronta para integração com frontend
