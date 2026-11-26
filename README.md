# Biblioteca CloudTech Solutions — API REST

API REST em **Java 17 + Spring Boot 3** para o gerenciamento do acervo de livros de uma biblioteca: consulta, cadastro, alteração e exclusão de livros por meio de requisições HTTP.

O projeto aplica práticas de DevOps: versionamento com Git/GitHub, testes manuais com Thunder Client, testes automatizados com JUnit 5 + MockMvc, integração contínua com GitHub Actions e deploy na nuvem com Railway.

## Tecnologias

- Java 17
- Spring Boot 3 (Spring Web)
- JUnit 5 + MockMvc
- Maven
- GitHub Actions (CI)
- Docker + Railway (deploy)

## Estrutura

```
biblioteca-api/
├── src/
│   ├── main/java/br/com/cloudtech/biblioteca/
│   │   ├── BibliotecaApplication.java
│   │   ├── models/        -> Livro
│   │   ├── controller/    -> LivroController, HomeController
│   │   └── repository/    -> LivroRepository (dados em memória)
│   └── test/java/...      -> LivroApiTest (JUnit 5 + MockMvc)
├── .github/workflows/pipeline.yml
├── Dockerfile
├── pom.xml
├── README.md
└── .gitignore
```

## Modelo Livro

| Campo | Tipo | Descrição |
|---|---|---|
| id | Long | Código de identificação |
| titulo | String | Título do livro |
| autor | String | Nome do autor |
| editora | String | Editora |
| ano | Integer | Ano de publicação |
| categoria | String | Categoria |
| status | String | "Disponível" ou "Emprestado" |

## Endpoints

| Método | Endpoint | Descrição | Resposta |
|---|---|---|---|
| GET | `/` | Mensagem de boas-vindas | 200 |
| GET | `/livros` | Lista os livros | 200 |
| GET | `/livros/{id}` | Consulta um livro pelo ID | 200 / 404 |
| POST | `/livros/cadastrar` | Cadastra um livro | 201 / 400 |
| PUT | `/livros/alterar/{id}` | Altera um livro | 200 / 404 |
| DELETE | `/livros/excluir/{id}` | Exclui um livro (extra) | 204 / 404 |
| GET | `/documentacao` | Lista os endpoints da API | 200 |

Exemplo — `POST /livros/cadastrar`
```json
{
  "titulo": "Banco de Dados para Iniciantes",
  "autor": "Maria Souza",
  "editora": "EduTech",
  "ano": 2023,
  "categoria": "Informática",
  "status": "Disponível"
}
```

## Como executar

```bash
mvn spring-boot:run        # sobe em http://localhost:8080
mvn test                   # executa os testes
mvn clean package          # gera target/biblioteca-api.jar
java -jar target/biblioteca-api.jar
```

## Integração contínua

O workflow `.github/workflows/pipeline.yml` roda a cada push/pull request: baixa o código, configura o Java 17, instala as dependências, compila, executa os testes e gera o JAR (disponível como artefato da execução).

## Deploy

Publicado na Railway a partir deste repositório (build pelo `Dockerfile`). A porta é lida da variável `PORT` fornecida pela Railway.

URL pública: `https://somativabackend2026bibliotecas-production.up.railway.app`
