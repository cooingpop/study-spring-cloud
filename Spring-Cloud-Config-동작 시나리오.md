# Spring Cloud Config 동작 시나리오

## 개요
이 문서는 Spring Cloud Config Server와 Client의 동작 시나리오를 설명합니다. Spring Cloud Config는 분산 시스템에서 설정 정보를 외부화하고 중앙에서 관리할 수 있게 해주는 솔루션입니다.

## 시스템 구성
- **Config Server**: 설정 정보를 제공하는 서버 (포트: 8888)
- **Config Client**: 설정 정보를 사용하는 클라이언트 애플리케이션 (포트: 8080)
  - 데이터베이스 기능 포함 (JPA, H2 인메모리 DB)
- **Config Repository**: 설정 파일이 저장된 Git 저장소 (로컬 파일 시스템의 config-repo 폴더)

## 동작 시나리오

### 1. Config Server 시작
- Config Server 애플리케이션을 실행합니다.
- 서버는 `application.yml`에 설정된 대로 `file:///${user.dir}/config-repo` 경로의 설정 파일들을 읽어들여 준비합니다.
- 서버는 8888 포트에서 HTTP 요청을 수신할 준비를 합니다.

### 2. Config Client 시작
- Config Client 애플리케이션을 실행합니다.
- 클라이언트는 자신의 `application.yml`에 설정된 `spring.config.import: configserver:http://localhost:8888`를 읽고 Config Server에 연결을 시도합니다.

### 3. 설정 정보 요청
- 클라이언트는 Config Server에 HTTP 요청을 보냅니다.
- 요청 URL은 다음 형식을 따릅니다: `http://localhost:8888/{application}/{profile}[/{label}]`
  - `{application}`: 클라이언트의 `spring.application.name` (여기서는 `config-client`)
  - `{profile}`: 클라이언트의 활성 프로필 (예: `dev`, `prod` 등. 명시하지 않으면 `default`)
  - `{label}`: Git 브랜치 이름 (`default-label`에 지정된 `master`)
- 따라서 config-client는 Config Server에게 `http://localhost:8888/config-client/default/master`와 같은 URL로 요청을 보냅니다.

### 4. Config Server 응답
- Config Server는 요청을 받으면 config-repo 폴더에서 다음 파일들을 찾습니다:
  - `config-client.yml` (또는 `.properties`): 애플리케이션별 설정
  - `application.yml` (또는 `.properties`): 공통 설정
- 이 파일들의 내용을 합쳐서 JSON 또는 YAML 형식으로 클라이언트에게 응답합니다.
- 응답에는 `message: "Hello from Config Server!"` 속성이 포함됩니다 (config-client.yml에 정의됨).

### 5. Config Client 설정 적용
- Config Client는 Config Server로부터 받은 설정 값을 자신의 환경 속성으로 로드합니다.
- `@Value` 어노테이션이 붙은 필드들은 이 설정 값으로 초기화됩니다.
- 따라서 `ConfigController`의 `message` 필드에는 "Hello from Config Server!"가 주입됩니다.
- 클라이언트의 `/config` 엔드포인트에 접속하면 이 설정 값을 확인할 수 있습니다.

### 6. 런타임 설정 변경 (동적 갱신)
- config-repo/config-client.yml 파일의 message 값을 변경하고 Git 저장소에 커밋합니다.
- Config Client 애플리케이션을 재시작할 필요 없이, POST 요청으로 `http://localhost:8080/actuator/refresh` 엔드포인트를 호출합니다.
- Config Client는 Config Server로부터 최신 설정을 다시 가져옵니다.
- `@RefreshScope`가 붙은 `ConfigController` 빈의 `message` 필드가 변경된 값으로 업데이트됩니다.
- 다시 `/config` 엔드포인트에 접속하면 변경된 설정 값을 확인할 수 있습니다.

## 프로필별 설정
- 클라이언트가 특정 프로필(예: `dev`)로 실행되면, Config Server는 해당 프로필에 맞는 설정 파일(예: `config-client-dev.yml`)을 찾아 제공합니다.
- 이 경우 요청 URL은 `http://localhost:8888/config-client/dev/master`가 됩니다.
- 응답에는 `message: "Hello from Config Server - Development Environment!"`가 포함됩니다.

## 데이터베이스 기능
Config Client 애플리케이션은 이제 데이터베이스 기능을 포함하고 있습니다:

### 1. 엔티티 및 저장소
- `User` 엔티티: 사용자 정보(ID, 사용자명, 이메일)를 저장하는 JPA 엔티티
- `UserRepository`: Spring Data JPA를 사용하여 User 엔티티에 대한 CRUD 작업을 제공하는 인터페이스

### 2. 데이터 초기화
- `DataInitializationService`: 애플리케이션 시작 시 샘플 사용자 데이터를 초기화하는 서비스
- 데이터베이스가 비어있을 경우에만 샘플 데이터를 추가합니다

### 3. 데이터베이스 설정
- 기본 환경: MySQL 데이터베이스 사용 (application.yml에 정의)
- 개발 환경: H2 인메모리 데이터베이스 사용 (application-dev.yml에 정의)
  - H2 콘솔 활성화 (/h2-console)
  - JPA 자동 스키마 생성 (ddl-auto: update)
  - SQL 쿼리 로깅 활성화

### 4. 프로필에 따른 데이터베이스 설정 전환
- Config Client는 `spring.profiles.active` 속성에 따라 다른 데이터베이스 설정을 사용합니다
- Config Server는 프로필에 맞는 설정 파일을 제공합니다
- 개발 환경(dev)에서는 H2 인메모리 데이터베이스를 사용하고, 기본 환경에서는 MySQL을 사용합니다

## 결론
Spring Cloud Config를 사용하면 애플리케이션의 설정을 외부화하고 중앙에서 관리할 수 있으며, 런타임에 설정을 변경할 수 있습니다. 이는 마이크로서비스 아키텍처에서 여러 서비스의 설정을 효율적으로 관리하는 데 큰 도움이 됩니다. 또한 데이터베이스 설정과 같은 환경별 구성을 중앙에서 관리함으로써 애플리케이션 배포와 환경 전환을 더욱 쉽게 만들어 줍니다.
