# Kotlin SpringBoot Skeleton Apis

- PORT: 9001(BLUE), 9002(GREEN)

## Spec
- **SpringCloud 2020.0.3**
- **SpringBoot 2.5.5**
- **Kotlin 1.5.31**
- **JAVA 11**
- **Gradle 7.2**
- **Build: Kotlin Gradle(kts)**
- **Swagger SpringDoc OpenAPI 1.5.11**

## 배포
1. Jenkins를 통해 Dockerfile 로 이미지를 생성해서 AWS ECR에 PUSH
2. Jenkins에서 SSH를 통해 배포할 서버에 deploy 폴더 내에 있는 파일들을 복사
3. Jenkins에서 SSH를 통해 `deploy.sh`를 수행해서 `docker-compose.yml` 에 환경변수를 전달하고 Docker 이미지를 Pull
4. `deploy.sh` 로 BLUE/GREEN 배포를 수행

## 기본 구성
- **RDBMS** 커넥션 설정
- **RDBMS** JPA, QueryDSL 설정
- **MongoDB** 커넥션 설정
- **MongoDB** QueryDSL 설정
- AWS CloudWatch 모니터링 설정
- 공통 파일 다운로드/업로드 기능 (**AWS S3**)
- 공통 엑셀 다운로드 기능
- 공통 엑셀 업로드 기능
- **Exception Handler** 설정
- **ERROR** 메세지 **Slack** 메신저로 전송 설정
- **Profile** 별 로그(Logback) 설정
- **Profile** 별 **Yaml** 파일 설정
- **Base** 테스트 클래스
- **Tutorial API** 코드 및 테스트 코드 (개발중)
- **Tutorial** 엑셀 업로드/다운로드 **API** 코드 및 테스트 코드
- **Tutorial** 파일 업로드/다운로드 **API** 코드 및 테스트 코드
- 공통 메인 코드 **API** 코드 및 테스트코드 (개발중)
- 공통 상세 코드 **API** 코드 및 테스트코드 (개발중)

## TODO 테스트코드는 데이터베이스를 H2로 붙여서 로컬에서 테스트하도록 개선해야함