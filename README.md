# car-user-management-backend
Este repositório será responsável pela implementação do backend em Spring Boot, incluindo a API RESTful para gerenciamento de usuários e carros, autenticação via JWT, persistência com H2, e integração com testes unitários.

## Estórias de Usuário

1. Cadastro de Usuário: Como um usuário, quero me cadastrar no sistema informando nome, e-mail, login, senha, telefone e data de nascimento.
2. Autenticação: Como um usuário, quero fazer login no sistema usando login e senha para receber um token de acesso.
3. Cadastro de Carro: Como um usuário autenticado, quero cadastrar meus carros com informações de ano, placa, modelo e cor.
4. Consulta de Usuários: Como um administrador, quero listar todos os usuários cadastrados no sistema.
5. Consulta de Carros: Como um usuário autenticado, quero listar todos os meus carros cadastrados.
6. Atualização de Perfil: Como um usuário, quero atualizar meus dados pessoais e minha lista de carros.
7. Remoção de Usuário e Carro: Como um usuário, quero remover minha conta ou um carro específico.

## Solução

Esta aplicação foi desenvolvida usando Spring Boot com as seguintes características:
- Autenticação JWT para segurança e acesso às rotas protegidas.
- Banco de Dados H2 para persistência de dados em memória.
- JPA/Hibernate para mapeamento objeto-relacional.
- Testes Unitários para garantir a qualidade e a cobertura de código.
- REST API estruturada seguindo os princípios RESTful para operações de CRUD de usuários e carros.
- Mensagens de Erro padronizadas em JSON com formato `{ "message": "mensagem de erro", "errorCode": código }`.

## Execução e Build

1. Clone o repositório: `git clone https://github.com/luigigenova/car-user-management-backend.git`
2. Navegue até o diretório do projeto: `cd car-user-management-backend`
3. Execute o build com Maven: `mvn clean install`
4. Inicie a aplicação: `mvn spring-boot:run`
5. Acesse a API em `http://localhost:8080`

## Deploy e Testes

- Deploy: As instruções de deploy serão adicionadas para o host escolhido (Heroku, AWS, etc.).
- Testes Unitários: Para executar os testes, utilize o comando: `mvn test`
