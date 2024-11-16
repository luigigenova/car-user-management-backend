# Car User Management Backend

Este repositório contém o backend do sistema de gerenciamento de usuários e carros, desenvolvido com **Spring Boot**. Ele fornece uma API RESTful para operações de autenticação, cadastro, consulta e remoção de usuários e carros.

---

## **Descrição**

Este projeto é um backend estruturado para atender as seguintes funcionalidades:
- **Autenticação JWT** para segurança e controle de acesso.
- **Gerenciamento de Usuários**: Cadastro, consulta, atualização e remoção.
- **Gerenciamento de Carros**: Cadastro, consulta e exclusão de veículos associados a usuários.
- **Banco de Dados em Memória (H2)**: Utilizado para persistência durante o desenvolvimento.
- **Padrões RESTful** para todas as rotas e respostas em JSON.

---

## **Requisitos**

- **Java**: JDK 17 ou superior.
- **Maven**: Versão 3.6 ou superior.
- Ferramentas de teste de API como **Postman** ou **Insomnia** (opcional).

---

## **Instalação e Execução**

1. **Clone o repositório**
   ```bash
   git clone https://github.com/luigigenova/car-user-management-backend.git
   cd car-user-management-backend
   ```

2. **Compile o projeto**
   ```bash
   mvn clean install
   ```
3. **Execute a aplicação**
   ```bash
   mvn spring-boot:run
   ```
4. **Acesse a aplicação em** `http://localhost:8080.`

---

# Endpoints da API

## Autenticação

- **POST** `/api/signin`
  - **Descrição**: Realiza o login do usuário.
  - **Exemplo de Requisição**:

```json
{
  "username": "luigigenova",
  "password": "senha123"
}
```

  - **Resposta (200)**:

```json
{
  "token": "jwt-token-gerado"
}
```

- **POST** `/api/signup`
  - **Descrição**: Registra um novo usuário.
  - **Exemplo de Requisição**:

```json
{
  "firstName": "Luigi",
  "lastName": "Genova",
  "email": "systemasjava@gmail.com",
  "login": "luigigenova",
  "password": "senha123",
  "phone": "81999991871",
  "birthday": "1973-08-06"
}
```
  - **Resposta (200)**:

 ```json
{
  "message": "Usuário cadastrado com sucesso!"
}
```

---

## Usuários

- **GET** `/api/users`
  - **Descrição**: Retorna a lista de todos os usuários. (Protegido)
  - **Resposta (200):**

```json
[
  {
    "id": 1,
    "firstName": "Luigi",
    "lastName": "Genova",
    "email": "systemasjava@gmail.com",
    "login": "luigigenova",
    "phone": "81999991871",
    "birthday": "1973-08-06"
  }
]
```

- **DELETE** `/api/users/{id}`
  - **Descrição**: Remove um usuário pelo ID. (Protegido)
  - **Resposta (204)**: No Content.

---

## Documentação

- A documentação Swagger está disponível em: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html).

---

## Banco de Dados H2

- **Console do H2**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
- **Configuração**:
  - **JDBC URL**: `jdbc:h2:mem:testdb`
  - **Usuário**: `sa`
  - **Senha**: vazio

---

## Testes

Para rodar os testes unitários, execute:

```bash
mvn test
```

---

## Deploy

### Docker

1. Construa a imagem Docker:

```bash
docker build -t car-user-management-backend .
```

2. Execute o container:

```bash
docker run -p 8080:8080 car-user-management-backend
```

---

## Futuras Melhorias

- Integração com banco de dados PostgreSQL ou MySQL.
- Implementação de níveis de acesso (admin e usuário comum).
- Relatórios gerados via API.
