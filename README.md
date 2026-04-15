# 안심동행 Backend

안심동행 백엔드는 React SPA 프론트엔드와 분리된 Spring Boot 3.x JSON REST API 서버입니다.
현재 저장소는 bootstrap 정리 단계가 아니라 실제 개발/연동 단계 기준으로 정리되어 있으며, Oracle 운영 스키마와 H2 테스트 구성을 함께 유지합니다.

## 기술 스택

- Java 21
- Spring Boot 3.3.x
- Maven Wrapper
- Spring Web, Validation, Security, Data JPA, WebSocket, WebFlux, Actuator
- QueryDSL
- Oracle Database
- H2 in-memory (test profile)
- JWT
- springdoc OpenAPI

## 현재 구현된 도메인

- 인증: 회원가입, 로그인, JWT 재발급, 로그아웃, Kakao OAuth 로그인
- 사용자: 내 정보 조회/수정, 공개 프로필 조회
- 프로젝트: 생성, 내 목록 조회, 상세 조회, 수정, 취소
- 프리랜서: 공개 목록/상세 조회, 프리랜서 워크스페이스 접근 확인
- 제안: 사용자 제안 생성, 프리랜서 제안 목록/상세 조회, 제안 수락
- 운영 확인: 관리자 접근 확인, Swagger/OpenAPI, Actuator health, WebSocket endpoint

## 골격만 있는 도메인

- recommendation
- report
- chat
- notice
- notification
- review
- verification

이 도메인들은 패키지/엔드포인트 골격만 존재하거나 실제 비즈니스 로직이 아직 연결되지 않았습니다.

## 프로필과 데이터베이스

- `local`: Oracle 연결, 로컬 개발용 로그 강화
- `dev`: Oracle 연결, `ddl-auto=validate`
- `prod`: Oracle 연결, `ddl-auto=validate`
- `test`: H2 in-memory, Oracle 없이 테스트 실행

Oracle 프로필은 실제 dev DB가 11g 계열인 점을 반영해 `org.hibernate.community.dialect.OracleLegacyDialect`를 사용합니다.
테스트 프로필만 H2를 사용하며, 운영 스키마를 대체하는 용도로 H2를 사용하지 않습니다.

## 환경 변수

주요 환경 변수 예시는 [.env.example](/C:/dev/AIBE5_Project2_Team4_BE/.env.example)에 정리되어 있습니다.

중요 사항:

- `JWT_SECRET`가 비어 있거나 placeholder 수준이면 `local/dev/prod`에서 서버가 기동되지 않습니다.
- `test` 프로필만 테스트 전용 JWT 설정을 사용합니다.
- Oracle 접속 정보는 `.env` 또는 OS 환경 변수로 주입합니다.

## 실행 명령

컴파일:

```powershell
.\mvnw.cmd -q -DskipTests compile
```

테스트:

```powershell
.\mvnw.cmd test
```

로컬 실행:

```powershell
.\mvnw.cmd spring-boot:run
```

특정 프로필 실행 예시:

```powershell
$env:SPRING_PROFILES_ACTIVE='dev'
$env:JWT_SECRET='replace-with-a-real-secret'
.\mvnw.cmd spring-boot:run
```

## API 계약

- Base URL: `/api/v1`
- 성공 응답: `ApiResponse<T>`
- 실패 응답: `ApiResponse<Void>` 안의 `error: ErrorResponse`
- 날짜/시간 포맷: ISO-8601 문자열, `Asia/Seoul`
- 인증 헤더: `Authorization: Bearer <access-token>`

### 인증 계약

회원가입:

- `POST /api/v1/auth/signup`
- body: `email`, `password`, `name`, `phone`, `intro`

로그인:

- `POST /api/v1/auth/login`
- canonical body: `email`, `password`
- legacy alias: `username`도 임시 호환 지원

재발급:

- `POST /api/v1/auth/refresh`
- legacy alias endpoint: `POST /api/v1/auth/reissue`
- body: `refreshToken`

로그아웃:

- `POST /api/v1/auth/logout`
- access token 인증이 필요합니다.
- 현재 Oracle 스키마에는 refresh token 저장 테이블이 없어 서버 측 revoke 저장소 없이 stateless JWT 정책으로 동작합니다.

### 프로젝트/제안 상태값

프로젝트 상태:

- `REQUESTED`
- `ACCEPTED`
- `IN_PROGRESS`
- `COMPLETED`
- `CANCELLED`

제안 상태:

- `PENDING`
- `ACCEPTED`
- `REJECTED`
- `EXPIRED`
- `CANCELLED`

### 페이지네이션

현재 구현된 목록 API는 다음 쿼리 파라미터를 사용합니다.

- `page` default `0`
- `size` default `10`
- `status` optional enum filter

## CORS / WebSocket / Multipart

기본 CORS 허용 origin:

- `http://localhost:5173`
- `http://127.0.0.1:5173`
- `http://localhost:3000`
- `http://127.0.0.1:3000`

주의 사항:

- wildcard origin과 credential 허용 조합은 금지됩니다.
- WebSocket endpoint 기본값은 `/ws`입니다.
- multipart 설정은 최대 크기와 저장 경로만 정리되어 있으며, 실제 파일 저장 기능 전체는 아직 완성되지 않았습니다.

## 외부 연동 안정성

- Kakao OAuth WebClient timeout 적용
- AI WebClient timeout 적용
- Kakao provider 4xx/5xx 오류를 명시적으로 매핑

## 검증 결과

2026-04-15 기준으로 다음을 확인했습니다.

- `./mvnw.cmd -q -DskipTests compile` 통과
- `./mvnw.cmd test` 통과 (51 tests)
- 실제 Oracle `dev` 프로필 기동 성공
- 실제 Oracle DB에서 `signup -> login -> refresh -> users/me -> projects/me -> project create -> project detail -> logout` smoke 성공
