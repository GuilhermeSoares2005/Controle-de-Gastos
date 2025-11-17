# Controle de Gastos

Sistema web Full Stack feito com Spring Boot 3 + Thymeleaf para registrar receitas e despesas, calcular totais e preparar o deploy em contêiner (Docker/Render). A UI é server-side, simples e focada em produtividade.

## Stack principal
- Java 21 / Spring Boot 3.5 (Web, Data JPA, Validation, Thymeleaf)
- H2 em memória para desenvolvimento e PostgreSQL para produção
- Maven, Docker, Render

## Recursos disponíveis
- Cadastro de lançamentos (descrição, valor, data e tipo `RECEITA` ou `DESPESA`)
- Regras de validação com mensagens amigáveis no formulário
- Lista consolidada com totais agrupados por tipo e saldo final
- Exclusão rápida de registros diretamente na tabela
- Perfis separados para `default` (H2) e `prod` (PostgreSQL)

## Executando localmente
1. **Pré-requisitos:** Java 21+ e Maven 3.9+ no PATH.
2. Dentro do diretório do projeto, execute:
   ```bash
   ./mvnw spring-boot:run
   ```
3. Acesse `http://localhost:8080`.
4. (Opcional) Console H2 disponível em `http://localhost:8080/h2-console` com JDBC `jdbc:h2:mem:controlegastos`.

### Perfil de produção local (PostgreSQL)
```bash
SPRING_PROFILES_ACTIVE=prod \
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/controlegastos \
SPRING_DATASOURCE_USERNAME=postgres \
SPRING_DATASOURCE_PASSWORD=postgres \
./mvnw spring-boot:run
```

## Testes automatizados
```bash
./mvnw test
```

## Docker
```bash
docker build -t controle-de-gastos .
docker run -p 8080:8080 controle-de-gastos
```

## Deploy no Render
1. Suba o código para um repositório público/privado no GitHub (ex.: `https://github.com/<usuario>/controle-de-gastos`).
2. No Render, crie um **Web Service** apontando para esse repositório.
3. Configure:
   - Runtime: Docker
   - Branch: `main`
   - Build command: `docker build -t controle-de-gastos .`
   - Start command: `docker run -p $PORT:8080 controle-de-gastos`
   - Variáveis: `SPRING_PROFILES_ACTIVE=prod`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`.
4. Após o deploy, atualize o link público no README (ex.: `https://controledegastos-seunome.onrender.com`).

