# Backend Server 구조

```plaintext
# 프로젝트 디렉토리 구조

```plaintext
📦 backend
 ┣ 📂 src
 ┃ ┣ 📂 main
 ┃ ┃ ┣ 📂 java/com/example/project  // Java 소스 코드
 ┃ ┃ ┃ ┣ 📂 config                  // 설정 관련 클래스 (Security, JWT, DB 등)
 ┃ ┃ ┃ ┣ 📂 controller              // API 컨트롤러 (사용자, 책, 감상문 관련)
 ┃ ┃ ┃ ┣ 📂 service                 // 비즈니스 로직 (서비스 계층)
 ┃ ┃ ┃ ┣ 📂 repository              // JPA 리포지토리 (DB 연동)
 ┃ ┃ ┃ ┣ 📂 dto                     // 요청(Request) & 응답(Response) 객체
 ┃ ┃ ┃ ┣ 📂 entity                  // JPA 엔티티 클래스
 ┃ ┃ ┃ ┣ 📂 exception               // 예외 처리 관련 클래스
 ┃ ┃ ┃ ┗ 📂 util                    // 공통 유틸리티 (JWT, 날짜 포맷 등)
 ┃ ┃ ┣ 📂 resources
 ┃ ┃ ┃ ┣ 📜 application.yml        // Spring Boot 설정 파일 (DB, JWT 설정)
 ┃ ┃ ┃ ┣ 📜 schema.sql             // 초기 DB 스키마 (선택 사항)
 ┃ ┃ ┃ ┗ 📜 data.sql               // 초기 데이터 삽입 (선택 사항)
 ┃ ┣ 📂 test
 ┃ ┃ ┣ 📂 java/com/example/project // 테스트 코드 디렉토리
 ┃ ┃ ┃ ┣ 📂 controller              // 컨트롤러 테스트
 ┃ ┃ ┃ ┣ 📂 service                 // 서비스 테스트
 ┃ ┃ ┃ ┗ 📂 repository              // JPA 테스트
 ┣ 📂 scripts                      // DB 마이그레이션, 초기 데이터, 배포 스크립트
 ┃ ┣ 📜 init-db.sql                // 데이터베이스 초기화 스크립트
 ┃ ┣ 📜 start-server.sh            // 서버 실행 스크립트
 ┃ ┗ 📜 deploy.sh                  // 배포 자동화 스크립트
 ┣ 📂 docs                         // API 문서, ERD, 개발 문서
 ┃ ┣ 📜 API_SPEC.md                // Swagger API 명세
 ┃ ┣ 📜 ERD.png                    // 데이터베이스 ERD
 ┃ ┗ 📜 README.md                  // 프로젝트 설명
 ┣ 📜 .gitignore                    // Git에서 제외할 파일 설정
 ┣ 📜 build.gradle                 // Gradle 빌드 설정 (Java)
 ┣ 📜 gradlew                        // Gradle 실행 파일 (리눅스)
 ┣ 📜 gradlew.bat                    // Gradle 실행 파일 (윈도우)
 ┣ 📜 README.md                      // 프로젝트 개요 및 사용법
 ┗ 📜 Dockerfile                     // Docker 배포 설정 파일
