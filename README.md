# sync

## 빌드 및 실행 방법

- Requirements
  - Docker
  - Java 25
  - pnpm

- 서버 빌드 및 실행
  - 빌드

    ```bash
    ./scripts/ci/server.sh
    ```

  - 실행

    ```bash
    cd apps/server
    ./gradlew bootRun
    ./gradlew bootRun --args='--spring.profiles.active=dev' # dev profile
    ```

- 웹 빌드 및 실행
  - 빌드

    ```bash
    ./scripts/ci/web.sh
    ```

  - 실행

    ```bash
    cd apps/web
    pnpm run dev
    ```

## 로컬 인프라 실행 (DB/S3)

- DB(Postgres) 실행

  ```bash
  docker compose -f apps/server/compose.yaml up -d
  ```

- LocalStack(S3) 실행

  ```bash
  docker compose -f infra/localstack/docker-compose.yml up -d
  ```
