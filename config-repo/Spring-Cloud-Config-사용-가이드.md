# Spring Cloud Config 사용 가이드

이 문서는 Spring Cloud Config를 사용하여 애플리케이션 구성을 관리하는 방법을 설명합니다. 특히 config-repo 디렉토리에 구성 파일을 작성하는 방법에 중점을 둡니다.

## 목차

1. [Spring Cloud Config 개요](#spring-cloud-config-개요)
2. [구성 파일 명명 규칙](#구성-파일-명명-규칙)
3. [구성 파일 우선순위](#구성-파일-우선순위)
4. [구성 파일 작성 예제](#구성-파일-작성-예제)
5. [프로필별 구성](#프로필별-구성)
6. [모범 사례](#모범-사례)

## Spring Cloud Config 개요

Spring Cloud Config는 분산 시스템의 외부화된 구성을 관리하기 위한 서버와 클라이언트를 제공합니다. Config Server는 Git 저장소(로컬 또는 원격)에서 구성 파일을 읽어 클라이언트 애플리케이션에 제공합니다.

### 주요 구성 요소

- **Config Server**: 구성 파일을 저장하고 제공하는 서버
- **Config Client**: Config Server에서 구성을 가져오는 클라이언트 애플리케이션
- **Config Repository**: 구성 파일이 저장된 Git 저장소 (이 프로젝트에서는 `config-repo` 디렉토리)

## 구성 파일 명명 규칙

Spring Cloud Config는 다음과 같은 명명 규칙을 사용합니다:

```
/{application}/{profile}[/{label}]
/{application}-{profile}.yml
/{label}/{application}-{profile}.yml
/{application}-{profile}.properties
/{label}/{application}-{profile}.properties
```

여기서:
- `{application}`: 애플리케이션 이름 (spring.application.name)
- `{profile}`: 활성화된 프로필 (spring.profiles.active)
- `{label}`: Git 브랜치 이름 (기본값: master)

### 파일 이름 예시

- `application.yml`: 모든 애플리케이션, 모든 프로필에 적용되는 기본 구성
- `application-dev.yml`: 모든 애플리케이션의 dev 프로필에 적용되는 구성
- `config-client.yml`: config-client 애플리케이션의 기본 구성
- `config-client-dev.yml`: config-client 애플리케이션의 dev 프로필 구성

## 구성 파일 우선순위

Spring Cloud Config는 다음 우선순위로 구성을 적용합니다 (높은 우선순위가 낮은 우선순위를 덮어씁니다):

1. `{application}-{profile}.yml`
2. `{application}.yml`
3. `application-{profile}.yml`
4. `application.yml`

예를 들어, config-client 애플리케이션이 dev 프로필로 실행될 때:
- `config-client-dev.yml`의 구성이 가장 높은 우선순위
- 그 다음 `config-client.yml`
- 그 다음 `application-dev.yml`
- 마지막으로 `application.yml`

## 구성 파일 작성 예제

### 기본 구성 파일 (application.yml)

```yaml
# 모든 애플리케이션에 공통으로 적용되는 구성
app:
  common:
    property: common-value

# 데이터베이스 구성
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: dbuser
    password: dbpass

# 로깅 구성
logging:
  level:
    root: INFO
    org.springframework: INFO
    com.example: DEBUG
```

### 프로필별 구성 파일 (application-dev.yml)

```yaml
# 개발 환경 특정 구성
app:
  common:
    property: dev-value

# 개발 데이터베이스 구성
spring:
  datasource:
    url: jdbc:h2:mem:devdb
    username: sa
    password: 
    driver-class-name: org.h2.Driver
  
  # JPA 구성
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: update
    show-sql: true
```

### 애플리케이션별 구성 파일 (config-client.yml)

```yaml
message: "Hello from Config Server!"

# 애플리케이션 특정 구성
app:
  feature:
    enabled: true
```

### 애플리케이션 프로필별 구성 파일 (config-client-dev.yml)

```yaml
message: "Hello from Config Server - Development Environment!"

# 개발 환경에서만 활성화되는 기능
app:
  feature:
    debug-mode: true
```

## 프로필별 구성

Spring Cloud Config는 프로필을 사용하여 다양한 환경(개발, 테스트, 프로덕션 등)에 대한 구성을 관리합니다.

### 프로필 활성화 방법

클라이언트 애플리케이션에서 다음과 같이 프로필을 활성화할 수 있습니다:

```yaml
spring:
  profiles:
    active: dev
```

또는 애플리케이션 실행 시 명령줄 인수로 지정:

```
java -jar app.jar --spring.profiles.active=dev
```

### 다중 프로필

여러 프로필을 동시에 활성화할 수 있습니다:

```yaml
spring:
  profiles:
    active: dev,local,secure
```

이 경우 구성 파일의 우선순위는 다음과 같습니다:
1. `{application}-dev,local,secure.yml` (존재하는 경우)
2. `{application}-secure.yml`
3. `{application}-local.yml`
4. `{application}-dev.yml`
5. `{application}.yml`
6. `application-dev,local,secure.yml` (존재하는 경우)
7. `application-secure.yml`
8. `application-local.yml`
9. `application-dev.yml`
10. `application.yml`

## 권장 사항

### 1. 민감한 정보 관리

민감한 정보(비밀번호, API 키 등)는 암호화하거나 환경 변수를 통해 제공하는 것이 좋습니다:

```yaml
spring:
  datasource:
    password: '{cipher}AQA...'  # 암호화된 비밀번호
```

### 2. 공통 구성과 특정 구성 분리

- `application.yml`: 모든 애플리케이션에 공통적인 구성
- `{application}.yml`: 특정 애플리케이션에만 필요한 구성

### 3. 명확한 주석 추가

구성 파일에 주석을 추가하여 각 속성의 목적과 사용법을 설명합니다:

```yaml
# 이 타임아웃은 외부 API 호출에 사용됩니다.
# 값이 너무 작으면 타임아웃 오류가 발생할 수 있습니다.
my:
  service:
    timeout: 30000  # 밀리초 단위
```

### 4. 구성 파일 구조화

관련 속성을 논리적 그룹으로 구성하고 명확한 계층 구조를 사용합니다:

```yaml
# 데이터베이스 구성
database:
  primary:
    url: jdbc:mysql://localhost:3306/mydb
    username: dbuser
    
  # 읽기 전용 복제본
  replica:
    url: jdbc:mysql://replica.example.com:3306/mydb
    username: readuser
```

### 5. 버전 관리

config-repo는 Git 저장소이므로 변경 사항을 커밋하고 버전을 관리합니다. 중요한 변경 사항에는 태그를 지정하는 것이 좋습니다.

### 6. 구성 변경 테스트

새 구성을 프로덕션에 적용하기 전에 개발 또는 테스트 환경에서 테스트합니다.