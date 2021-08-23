# 로컬 개발 시 수동 Docker 커맨드 가이드

## Clean & Build
수동 배포 테스트 시 변경사항이 있으면 Clean & Build를 수행해서 반드시 jar 파일을 새로 생성해주어야 한다

## Docker Build
/Users/home/skeleton-apis
 
위의 Path에서 스크립트를 수행한다고 했을떄 아래와 같이 모듈프로젝트 디렉토리를 지정해서 Docker Build를 하면
**test/kotlin-sprigboot-skeleton-apis**명으로 Docker Image 가 생성된다 

```bash
docker build -t test/skeleton-apis --pull=true .
```

### Argument 전달

Arument 전달시 아래와 같이 `--build-arg` 옵션으로 값을 넘겨주면 된다

```bash
docker build -t test/skeleton-apis --pull=true --build-arg SPRING_PROFILE=local skeleton-apis
```

## 프로세스 확인

컨테이너 명으로 구동중인 Docker 프로세스 ID를 조회한다

```bash
docker ps --filter name=skeleton-apis --format "{{.ID}}"
```

## Docker Compose 수동 커맨드
상세 커맨드 옵션 참조: https://freedeveloper.tistory.com/183?category=808752

## 서비스 지정 구동

`-d` 옵션을 줘서 백그라운드로 구동 시킴

```bash
docker-compose -f ./docker-compose.local.yml up -d
```

## 중지
단순 중지

```bash
docker-compose -f ./docker-compose.local.yml down
```


해당 docker-compose 파일에 정의된 서비스명의 이미지까지 모두 제거

```bash
docker-compose -f ./docker-compose.yml down --rmi all
```

해당 docker-compose 파일에 정의된 서비스명의 이미지와 Volume까지 모두 제거

```bash
docker-compose -f ./docker-compose.yml down --rmi all -v
```

## 다중 서비스 중 특정 서비스 컨테이너 중지 및 삭제

```bash
# 특정 서비스 컨테이너 중지
docker-compose -f ./docker-compose.yml stop skeleton-apis
# 특정 서비스 컨테이너 삭제
docker-compose -f ./docker-compose.yml rm -fsv skeleton-apis
```

## 로그 보기

```bash
docker-compose -f ./docker-compose.yml logs -f -t --tail="all" skeleton-apis
```

## 컨테이너 내부 접속

```bash
docker exec -it skeleton-apis-blue bash
```

## 컨테이너 내부 커맨드 수행

```bash
docker exec -it skeleton-apis-blue date
```