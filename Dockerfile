# DockerHub PUSH용 Dockerfile
FROM azul/zulu-openjdk:11

ARG JAR_FILE=build/libs/*.jar
ARG SPRING_PROFILE
#ARG SERVER_IP
#ARG SERVER_PORT

# 언어 설정
RUN apt-get update && apt-get -y install sudo
#apt-get install -y language-pack-ko && \
#RUN locale-gen ko_KR.UTF-8
#RUN update-locale LC_ALL=ko_KR.UTF-8 LANG=ko_KR.UTF-8
ENV LANG ko_KR.UTF-8
ENV LANGUAGE ko_KR.UTF-8
#ENV LC_ALL ko_KR.UTF-8
ENV SPRING_PROFILE=${SPRING_PROFILE:-local}
#ENV SERVER_IP=${SERVER_IP:-127.0.0.1}
#ENV SERVER_PORT=${SERVER_PORT:-9001}

# TimeZone 설정
ENV TZ=Asia/Seoul
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

RUN echo "GID = 1000" \
    echo "UID = 1000"

## 유저 및 그룹추가 sudo 추가
RUN groupadd --gid 1000 ubuntu \
    && useradd --uid 1000 --gid 1000 --create-home ubuntu \
    && adduser ubuntu sudo \
    && echo '%sudo ALL=(ALL) NOPASSWD:ALL' >> /etc/sudoers

## 유저 설정
USER 1000:1000
## 작업 디렉토리 설정
WORKDIR /home/ubuntu

RUN mkdir tmp files

RUN ls -alt

COPY ${JAR_FILE} app.jar
## app 소유자와 그룹 USER로 변경처리
RUN sudo chown 1000:1000 app.jar

#ENV JAVA_OPTS="-jar app.jar -Duser.timezone=Asia/Seoul --spring.profiles.active=${SPRING_PROFILE} --SERVER_IP=${SERVER_IP}"
# 원활한 설정을 위해 Docker Build 시 Eureka Client IP 셋팅을 패스하고 docker-compose deploy 시 적용되도록 수정
#  -Dserver.port=${SERVER_PORT} 포트 변경
ENV JAVA_OPTS="-jar app.jar -Duser.timezone=Asia/Seoul --spring.profiles.active=${SPRING_PROFILE}"
#ENV JAVA_OPTS="-jar app.jar -Duser.timezone=Asia/Seoul"
RUN echo ${JAVA_OPTS}

# AWS CloudWatch (for ubuntu)
COPY deploy/aws-cloudwatch-config/amazon-cloudwatch-agent-${SPRING_PROFILE}.json /opt/aws/amazon-cloudwatch-agent/etc/amazon-cloudwatch-agent.json

## app 구동
CMD java ${JAVA_OPTS}