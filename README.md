# spring-boot-maven-example

Spring Boot 4 REST service on PostgreSQL, built with Maven. Base path of the API is
`/api/v1/greeting`.

## Quality

<div align="center">

[![SonarQube Cloud](https://sonarcloud.io/images/project_badges/sonarcloud-light.svg)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=alert_status&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=bugs&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=code_smells&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=coverage&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=duplicated_lines_density&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=ncloc&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=reliability_rating&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Technical Debt](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=sqale_index&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=sqale_rating&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=io.github.pgatzka%3Aexample&metric=vulnerabilities&token=aa9ac4144cd3af34191cd1546aba8f1b46df32bb)](https://sonarcloud.io/summary/new_code?id=io.github.pgatzka%3Aexample)
</div>

## Prerequisites

- **JDK 25 or newer** — enforced by `maven-enforcer-plugin`, the build fails on anything older.
- **Docker** — required for the local database *and* for the tests (Testcontainers).
- Maven is not needed; use the bundled wrapper `./mvnw` (`mvnw.cmd` on Windows).

## Run it

```bash
./mvnw spring-boot:run
```

Nothing else to set up. The `development` profile is preconfigured for this goal, Spring Boot starts
the PostgreSQL container from `compose.yaml` and wires the datasource, and Flyway migrates the schema
on startup. The app listens on <http://localhost:8080>.

In IntelliJ IDEA use the committed **run@development** configuration instead — same result.

To run against a throwaway Testcontainers database rather than the compose stack, start
`TestApplication` from the test sources.

## Commands

| Command                      | Purpose                                                            |
|------------------------------|--------------------------------------------------------------------|
| `./mvnw spring-boot:run`     | Run locally with the `development` profile.                        |
| `./mvnw verify`              | Tests plus the JaCoCo coverage gate — this is what CI runs.         |
| `./mvnw spotless:apply`      | Format the code. **Run before committing, CI rejects otherwise.**   |
| `./mvnw package -DskipTests` | Build the jar into `target/`.                                       |

## Configuration

| Profile       | Datasource                                                                       |
|---------------|----------------------------------------------------------------------------------|
| `development` | started automatically from `compose.yaml` (`example_dev` / `example_dev` on 5432) |
| `staging`     | `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD` — all required |
| `production`  | same three environment variables                                                  |

`aot` is a build-time profile used inside the Docker build; it is not meant to be run by hand.

Hibernate runs with `ddl-auto: validate`, so the schema comes from the Flyway migrations in
`src/main/resources/db/migration` only — add a new migration, never edit an applied one.

## Docker

The jar has to exist first, the `Dockerfile` copies `target/*.jar`:

```bash
./mvnw package -DskipTests
docker build -t spring-boot-maven-example .
docker run --rm -p 8080:8080 \
  -e SPRING_PROFILES_ACTIVE=production \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/example_dev \
  -e SPRING_DATASOURCE_USERNAME=example_dev \
  -e SPRING_DATASOURCE_PASSWORD=example_dev \
  spring-boot-maven-example
```

## Contributing

Branches are named `<issue-number>-<short-description>`, commits start with the issue reference
(`#15 fix sonar badges`), and work lands on `main` through a pull request.

Before pushing: `./mvnw spotless:apply && ./mvnw verify`.

## License

[MIT](LICENSE) © Philipp Gatzka
