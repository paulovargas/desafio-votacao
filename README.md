# Desafio Votacao

API REST para gerenciamento de pautas e sessoes de votacao, desenvolvida em Java com Spring Boot.

## Funcionalidades

- Cadastrar pautas.
- Abrir sessao de votacao para uma pauta.
- Definir duracao da sessao ou usar o default de 1 minuto.
- Registrar votos `SIM` ou `NAO` por associado.
- Impedir voto duplicado do mesmo associado na mesma pauta.
- Impedir voto quando a sessao nao existe ou ja encerrou.
- Contabilizar votos e retornar o resultado da pauta.
- Persistir dados em PostgreSQL com migrations Flyway.

## Tecnologias

- Java 11
- Spring Boot 2.7.18
- Spring Web
- Spring Data JPA
- Bean Validation
- PostgreSQL
- Flyway
- Lombok
- Springdoc OpenAPI
- JUnit 4 e Mockito

## Estrutura

```text
desafio-votacao-api/
  src/main/java/com/sicredi/votacao
    controller/   Endpoints REST
    dto/          Objetos de request e response
    entity/       Entidades JPA
    exception/    Excecoes e handler global
    repository/   Repositorios Spring Data
    service/      Regras de negocio
  src/main/resources/db/migration
    V1__create_table_pautas.sql
    V2__create_table_sessoes.sql
    V3__create_table_votos.sql
    V4__create_index_votos_pauta_voto.sql
```

## Requisitos para executar

- Java 11
- Maven 3.8+
- PostgreSQL 12+

Opcional:

- Docker, caso prefira subir o PostgreSQL em container.
- Docker Compose, caso prefira subir API e PostgreSQL juntos.

## Execucao rapida recomendada

O caminho principal de validacao da entrega e subir tudo pela raiz do repositorio:

```bash
docker-compose up --build
```

Em ambientes com Docker Compose v2, o comando equivalente e:

```bash
docker compose up --build
```

Depois que a API iniciar, valide com:

```bash
curl http://localhost:8080/api/v1/pautas
```

O retorno esperado em uma base vazia e `[]`.

## Configuracao local

Clone o repositorio, entre na pasta raiz do projeto e depois no modulo da API:

```bash
cd desafio-votacao-api
```

Crie um arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

No Windows PowerShell:

```powershell
Copy-Item .env.example .env
```

Conteudo esperado:

```env
DB_URL=jdbc:postgresql://localhost:5432/desafio_votacao
DB_USERNAME=postgres
DB_PASSWORD=senha
SERVER_PORT=8080
APP_CALLBACK_BASE_URL=http://localhost:8080
```

A aplicacao usa o profile `local` por default. As configuracoes ficam em:

- `src/main/resources/application.yml`
- `src/main/resources/application-local.yml`

## Executando com Docker Compose

Na raiz do repositorio, suba a API e o PostgreSQL juntos:

```bash
docker compose up --build
```

Se sua instalacao usar o Compose legado:

```bash
docker-compose up --build
```

Esse comando cria:

- `desafio-votacao-postgres`: banco PostgreSQL com database `desafio_votacao`.
- `desafio-votacao-api`: API Spring Boot acessivel em `http://localhost:8080`.

As migrations do Flyway sao executadas automaticamente quando a API inicia.

No Compose, a API usa o profile `docker`, definido em `application-docker.yml`, e recebe as credenciais do PostgreSQL por variaveis de ambiente. Nao e necessario criar `.env` para esse modo.

Para parar os containers:

```bash
docker compose down
```

Ou, usando Compose legado:

```bash
docker-compose down
```

Para parar e remover tambem o volume do banco:

```bash
docker compose down -v
```

Ou:

```bash
docker-compose down -v
```

## Subindo somente o PostgreSQL com Docker

Use esta opcao se quiser rodar a API localmente com Maven e subir apenas o banco em container.

No Linux/macOS:

```bash
docker run --name desafio-votacao-postgres \
  -e POSTGRES_DB=desafio_votacao \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=senha \
  -p 5432:5432 \
  -d postgres:16
```

No Windows PowerShell:

```powershell
docker run --name desafio-votacao-postgres `
  -e POSTGRES_DB=desafio_votacao `
  -e POSTGRES_USER=postgres `
  -e POSTGRES_PASSWORD=senha `
  -p 5432:5432 `
  -d postgres:16
```

Se usar outra senha, atualize `DB_PASSWORD` no `.env`.

## Executando a API

Use esta opcao quando o PostgreSQL ja estiver rodando, seja localmente ou via Docker.

Com o PostgreSQL rodando:

```bash
mvn spring-boot:run
```

A API sobe, por default, em:

```text
http://localhost:8080
```

As migrations do Flyway sao executadas automaticamente durante a inicializacao.

## Executando os testes

```bash
mvn test
```

Estado atual verificado:

```text
Tests run: 34, Failures: 0, Errors: 0, Skipped: 0
```

## App mobile auxiliar

O cliente mobile nao faz parte da avaliacao principal do desafio, mas este repositorio inclui um app Ionic + Angular para demonstrar o fluxo de votacao consumindo a API.

Para executar:

```powershell
cd desafio-votacao-mobile
npm.cmd install
npm.cmd start
```

Acesse:

```text
http://localhost:8100
```

Com a API rodando em `http://localhost:8080`, o app permite listar pautas, cadastrar pauta, abrir sessao, votar e consultar resultado.

As instrucoes para gerar APK Android com Capacitor estao em:

```text
desafio-votacao-mobile/README.md
```

## Swagger / OpenAPI

O projeto possui a dependencia `springdoc-openapi-ui`.

Com a aplicacao em execucao, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

Especificacao OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

## URL base

```text
/api/v1
```

## Contrato mobile do Anexo 1

O cliente mobile nao faz parte da entrega avaliada, mas a API tambem disponibiliza descricoes JSON de telas para atender ao modelo do Anexo 1.

As URLs das acoes dessas telas podem ser montadas com dominio configuravel:

```env
APP_CALLBACK_BASE_URL=http://localhost:8080
```

Se `APP_CALLBACK_BASE_URL` nao for informado, as telas retornam URLs relativas.

Endpoints de telas:

```text
GET ou POST /api/v1/mobile/telas/opcoes
GET ou POST /api/v1/mobile/telas/nova-pauta
GET ou POST /api/v1/mobile/telas/abrir-sessao
GET ou POST /api/v1/mobile/telas/votar
GET ou POST /api/v1/mobile/telas/consultar-resultado
```

As telas retornam acoes com metodo `POST`, seguindo o Anexo 1. Campos informados pelo usuario sao enviados no body da acao.

Endpoints de acoes mobile:

```text
POST /api/v1/mobile/acoes/nova-pauta
POST /api/v1/mobile/acoes/abrir-sessao
POST /api/v1/mobile/acoes/votar
POST /api/v1/mobile/acoes/consultar-resultado
```

Exemplo de tela `FORMULARIO`:

```json
{
  "id": "nova-pauta",
  "tipo": "FORMULARIO",
  "titulo": "Nova pauta",
  "descricao": "Cadastro de pauta para votacao.",
  "itens": [
    {
      "id": "titulo",
      "tipo": "TEXTO",
      "rotulo": "Titulo",
      "obrigatorio": true
    }
  ],
  "acoes": [
    {
      "id": "cadastrar",
      "rotulo": "Cadastrar",
      "metodo": "POST",
      "url": "http://localhost:8080/api/v1/mobile/acoes/nova-pauta",
      "body": {}
    }
  ]
}
```

Exemplo de tela `SELECAO`:

```json
{
  "id": "opcoes",
  "tipo": "SELECAO",
  "titulo": "Menu de votacao",
  "opcoes": [
    {
      "id": "nova-pauta",
      "rotulo": "Cadastrar pauta",
      "metodo": "POST",
      "url": "http://localhost:8080/api/v1/mobile/telas/nova-pauta",
      "body": {
        "tela": "nova-pauta"
      }
    }
  ]
}
```

Telas que dependem de uma pauta recebem `pautaId` como campo da tela. O cliente envia esse valor no body da acao, por exemplo:

```json
{
  "pautaId": 1,
  "associadoId": 100,
  "cpf": "12345678901",
  "voto": "SIM"
}
```

## Endpoints

### Listar pautas

```http
GET /api/v1/pautas
```

Response `200 OK`:

```json
[
  {
    "id": 1,
    "titulo": "Aprovar novo regulamento",
    "descricao": "Pauta para votacao do novo regulamento interno",
    "dataCriacao": "2026-08-08T09:00:00"
  }
]
```

Esse endpoint foi adicionado para facilitar clientes de consulta e o app mobile auxiliar.

### Cadastrar pauta

```http
POST /api/v1/pautas
Content-Type: application/json
```

Request:

```json
{
  "titulo": "Aprovar novo regulamento",
  "descricao": "Pauta para votacao do novo regulamento interno"
}
```

Response `201 Created`:

```json
{
  "id": 1,
  "titulo": "Aprovar novo regulamento",
  "descricao": "Pauta para votacao do novo regulamento interno",
  "dataCriacao": "2026-08-08T09:00:00"
}
```

### Abrir sessao de votacao

```http
POST /api/v1/pautas/{id}/sessao
Content-Type: application/json
```

Request com duracao informada:

```json
{
  "duracaoMinutos": 5
}
```

Request sem body tambem e aceito. Nesse caso, a sessao fica aberta por 1 minuto.

Response `200 OK`:

```json
{
  "id": 1,
  "pautaId": 1,
  "dataHoraAbertura": "2026-08-08T09:00:00",
  "dataHoraEncerramento": "2026-08-08T09:05:00"
}
```

Regras:

- `duracaoMinutos` deve ser maior que zero quando informado.
- So pode existir uma sessao por pauta.

### Registrar voto

```http
POST /api/v1/pautas/{id}/votos
Content-Type: application/json
```

Request:

```json
{
  "associadoId": 100,
  "cpf": "12345678901",
  "voto": "SIM"
}
```

Valores aceitos para `voto`:

- `SIM`
- `NAO`

Response `200 OK`:

```json
{
  "id": 1,
  "pautaId": 1,
  "associadoId": 100,
  "voto": "SIM",
  "dataHora": "2026-08-08T09:01:00"
}
```

Regras:

- A pauta precisa existir.
- A pauta precisa ter sessao aberta.
- A sessao nao pode estar encerrada.
- O CPF e validado por um client fake deterministico.
- CPF invalido retorna `404 Not Found`.
- CPF valido pode retornar `ABLE_TO_VOTE` ou `UNABLE_TO_VOTE`.
- Quando o retorno for `UNABLE_TO_VOTE`, o voto e rejeitado com `403 Forbidden`.
- O mesmo associado so pode votar uma vez por pauta.

Para facilitar testes manuais, o fake considera CPFs com 11 digitos como validos. CPFs terminados em digito par retornam `ABLE_TO_VOTE`; CPFs terminados em digito impar retornam `UNABLE_TO_VOTE`.

### Obter resultado da votacao

```http
GET /api/v1/pautas/{id}/resultado
```

Response `200 OK`:

```json
{
  "pautaId": 1,
  "titulo": "Aprovar novo regulamento",
  "votosSim": 3,
  "votosNao": 1,
  "totalVotos": 4,
  "resultado": "APROVADA"
}
```

Possiveis valores de `resultado`:

- `APROVADA`
- `REPROVADA`
- `EMPATE`

## Tratamento de erros

Erros de negocio e validacao retornam um payload padronizado:

```json
{
  "timestamp": "2026-08-08T09:00:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "A duracao da sessao deve ser maior que zero",
  "path": "uri=/api/v1/pautas/1/sessao"
}
```

Mapeamentos principais:

- `400 Bad Request`: payload invalido ou duracao de sessao invalida.
- `403 Forbidden`: CPF valido, mas nao habilitado para votar.
- `404 Not Found`: pauta, sessao ou CPF nao encontrado/invalido.
- `409 Conflict`: sessao duplicada, sessao encerrada ou voto duplicado.

## Exemplos com curl

Cadastrar pauta:

```bash
curl -X POST http://localhost:8080/api/v1/pautas \
  -H "Content-Type: application/json" \
  -d '{"titulo":"Aprovar novo regulamento","descricao":"Pauta de teste"}'
```

Abrir sessao:

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/sessao \
  -H "Content-Type: application/json" \
  -d '{"duracaoMinutos":5}'
```

Registrar voto:

```bash
curl -X POST http://localhost:8080/api/v1/pautas/1/votos \
  -H "Content-Type: application/json" \
  -d '{"associadoId":100,"cpf":"12345678901","voto":"SIM"}'
```

Consultar resultado:

```bash
curl http://localhost:8080/api/v1/pautas/1/resultado
```

Listar pautas:

```bash
curl http://localhost:8080/api/v1/pautas
```

No Windows PowerShell, use `curl.exe` se o alias `curl` apontar para `Invoke-WebRequest`.

## Decisoes tecnicas

- A API usa versionamento por URL com prefixo `/api/v1`, por ser simples e explicito para clientes HTTP.
- As regras de negocio ficam na camada de service, mantendo controllers focados em HTTP.
- DTOs separam o contrato da API das entidades JPA.
- Bean Validation valida requests na entrada da API.
- Excecoes customizadas e `GlobalExceptionHandler` centralizam o tratamento de erro.
- A integracao de CPF foi isolada em um client/facade fake para simular um sistema externo.
- O contrato mobile do Anexo 1 foi exposto em endpoints de tela separados dos endpoints de negocio.
- O dominio das URLs de callback das telas mobile e configuravel por `APP_CALLBACK_BASE_URL`.
- Flyway versiona o schema e evita perda de controle sobre alteracoes no banco.
- PostgreSQL foi usado para garantir persistencia real entre restarts.
- O cliente mobile nao faz parte da entrega avaliada; a API expoe contratos JSON para serem consumidos por esse cliente.

## Versionamento da API

A versao atual da API e `/api/v1`.

Uma nova versao, como `/api/v2`, deve ser criada quando houver mudanca incompatavel no contrato, por exemplo:

- renomear campos de request ou response;
- alterar semantica de status HTTP;
- remover endpoints;
- alterar regras que mudem o comportamento esperado por clientes existentes.

Mudancas compativeis, como adicionar campos opcionais ou novos endpoints, podem continuar em `/api/v1`.

## Performance

O endpoint de resultado usa contagens por pauta e opcao de voto. Para apoiar esse acesso, a migration `V4__create_index_votos_pauta_voto.sql` cria o indice:

```sql
CREATE INDEX idx_votos_pauta_voto
    ON votos (pauta_id, voto);
```

Esse indice ajuda consultas que filtram por `pauta_id` e `voto`, como as contagens de votos `SIM` e `NAO` usadas na apuracao.

Para preparar uma pauta com 100.000 votos no PostgreSQL local:

```powershell
cd desafio-votacao-api
docker exec -i desafio-votacao-postgres psql -U postgres -d desafio_votacao < .\scripts\performance\preparar-massa-resultado.sql
```

O comando retorna o `pauta_id` criado. Use esse id na medicao do endpoint de resultado:

```powershell
cd desafio-votacao-api
.\scripts\performance\resultado-votacao.ps1 -BaseUrl http://localhost:8080 -PautaId 1 -Requests 1000
```

O script executa varias chamadas para `GET /api/v1/pautas/{pautaId}/resultado` e retorna total de requisicoes, sucessos, falhas, tempo medio e requisicoes por segundo.

## Bonus

Status atual:

- Bonus 1, integracao fake de CPF: implementado com client fake aleatorio.
- Bonus 2, performance: implementado com contagens no banco, indice `(pauta_id, voto)`, massa SQL de 100.000 votos e script local de medicao do endpoint de resultado.
- Bonus 3, versionamento: API usa `/api/v1` e a estrategia esta documentada acima.
