#!/bin/bash

export ZONE=${ZONE:-zone1}

# 처리할 PROFILE
PROFILE=${PROFILE:-prod}
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] PROFILE=${PROFILE}" | tee deploy.log

AWS_ACCESS_KEY_ID=${ECR_AWS_ACCESS_KEY_ID}
AWS_SECRET_ACCESS_KEY=${ECR_AWS_SECRET_ACCESS_KEY}
AWS_ECR_PRIVATE_DOMAIN=${AWS_ECR_PRIVATE_DOMAIN}

if [ "${PROFILE}" == "prod" ]; then
  EUREKA_DOMAIN=${EUREKA_DOMAIN:-'https://eureka.skeleton.com'}
  EUREKA_SERVER1=${EUREKA_SERVER1:-172.3.1.1}
  EUREKA_SERVER2=${EUREKA_SERVER2:-172.3.1.2}
elif [ "${PROFILE}" == "stage" ]; then
  EUREKA_DOMAIN=${EUREKA_DOMAIN:-'https://eureka-test.skeleton.com'}
  EUREKA_SERVER1=${EUREKA_SERVER1:-172.2.1.1}
  EUREKA_SERVER2=${EUREKA_SERVER2:-172.2.1.2}
else
  EUREKA_DOMAIN=${EUREKA_DOMAIN:-'http://eureka-dev.skeleton.com'}
  EUREKA_SERVER1=${EUREKA_SERVER1:-172.1.1.1}
  EUREKA_SERVER2=${EUREKA_SERVER2:-172.1.1.1}
fi

export EUREKA_SERVER1
export EUREKA_SERVER2
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] EUREKA_SERVER1=${EUREKA_SERVER1}, EUREKA_SERVER2=${EUREKA_SERVER2}" | tee deploy.log
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] EUREKA_DOMAIN=${EUREKA_DOMAIN}" | tee deploy.log

AWSCLI_INSTALL=$(aws --version | grep aws-cli | wc -l)

# 만약 AWS CLI2가 설치되지 않았다면
if [ $AWSCLI_INSTALL -ne 1 ];then
  echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] AWS CLI2 설치" | tee -a deploy.log
  # AWS CLI2 설치
  sudo apt update && \
  sudo apt-get install unzip -y && \
  curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip" && \
  sudo unzip awscliv2.zip && \
  sudo ./aws/install

  # AWS Configure 셋팅 및 로그인
  aws configure set aws_access_key_id ${AWS_ACCESS_KEY_ID} && \
  aws configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY} && \
  aws configure set region ap-northeast-2 && \
  aws configure set output json
fi

# AWS CloudWatch를 사용하기 위한 Agent 다운로드
if [ "${PROFILE}" != "dev" ] && [ ! -e 'tmp/amazon-cloudwatch-agent.deb' ];then
  echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] AWS CloudWatch Agent 다운로드" | tee -a deploy.log
  wget https://s3.amazonaws.com/amazoncloudwatch-agent/ubuntu/amd64/latest/amazon-cloudwatch-agent.deb -P tmp
fi

echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] AWS ECR Private docker login" | tee -a deploy.log
aws ecr get-login-password --region ap-northeast-2 | docker login --username AWS --password-stdin ${AWS_ECR_PRIVATE_DOMAIN}

DELAY_TIME=${DELAY_TIME:-30}
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] DELAY_TIME=${DELAY_TIME}" | tee -a deploy.log

DOCKER_COMPOSE_FILENAME=docker-compose
#if [ "${PROFILE}" == "dev" ];then
#  DOCKER_COMPOSE_FILENAME=docker-compose.dev
#fi

export DOCKER_APP_NAME=${1:-skeleton-apis}
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] DOCKER_APP_NAME=${DOCKER_APP_NAME}" | tee -a deploy.log

IMAGE_NAME="${AWS_ECR_PRIVATE_DOMAIN}/test/${DOCKER_APP_NAME}"

if [ "${PROFILE}" != "prod" ]; then
  IMAGE_NAME="${IMAGE_NAME}-${PROFILE}"
fi
export IMAGE_NAME
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] IMAGE_NAME=${IMAGE_NAME}" | tee -a deploy.log

if [ "${PROFILE}" == "local" ];then
  echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] 맥북 로컬IP 조회" | tee -a deploy.log
  # 맥북 로컬IP 셋팅
  PUBLIC_SERVER_IP=$(ipconfig getifaddr en0)
  SERVER_IP=$(ipconfig getifaddr en0)
else
  echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] 인스턴스 공인IP 조회" | tee -a deploy.log
  ## 공인IP
  PUBLIC_SERVER_IP=$(curl ifconfig.me)
  NET_TOOLS=$(dpkg -l | grep net-tools | awk '{print $2}')
  if [ -z "${NET_TOOLS}" ];then
    sudo apt install net-tools -y
  fi

  echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] 인스턴스 Private IP 조회" | tee -a deploy.log
  if [ "${PROFILE}" == "dev" ];then
    SERVER_IP=$(ifconfig -a | grep "inet 172*" | grep "netmask 255.255.240.0" | awk '{print $2}')
  else
    SERVER_IP=$(ifconfig -a | grep "inet 172*" | grep "netmask 255.255.255.0" | awk '{print $2}')
  fi
fi

export PUBLIC_SERVER_IP
export SERVER_IP

BLUE_PORT=${2:-9001}
GREEN_PORT=${3:-9002}

if [ -z ${DOCKER_APP_NAME} ]; then
 echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] Please Input DOCKER_APP_NAME" | tee -a deploy.log
 echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] EXIT" | tee -a deploy.log
 exit
fi

echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] PUBLIC_SERVER_IP=$PUBLIC_SERVER_IP" | tee -a deploy.log
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] SERVER_IP=$SERVER_IP" | tee -a deploy.log
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] DOCKER_APP_NAME=$DOCKER_APP_NAME" | tee -a deploy.log

#cd ~/${DOCKER_APP_NAME}

EXIST_BLUE=$(docker ps --filter name=${DOCKER_APP_NAME}-blue --format "{{.ID}}")
EXIST_GREEN=$(docker ps --filter name=${DOCKER_APP_NAME}-green --format "{{.ID}}")

echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] docker ps --filter name=${DOCKER_APP_NAME}-blue --format '{{.ID}}'" | tee -a deploy.log
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] EXIST_BLUE=${EXIST_BLUE}" | tee -a deploy.log
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] docker ps --filter name=${DOCKER_APP_NAME}-green --format '{{.ID}}'" | tee -a deploy.log
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] EXIST_GREEN=${EXIST_GREEN}" | tee -a deploy.log

echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] 0. make initialize"
if [ ! -d "logs" ];then
  echo "mkdir logs"
  mkdir "logs"
fi

#EXIST_NETWORK=$(docker network inspect msa-network -f "{{json .Id}}")
EXIST_NETWORK=$(docker network ls | grep msa-network)

# 구동중인 컨테이너가 없으면 UP
if [ -z "$EXIST_NETWORK" ]; then
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-0] docker network create msa-network" | tee -a deploy.log
    docker network create msa-network | tee -a deploy.log
fi

# 구동중인 container 의 ec2 host id.
EC2_ID=$(curl http://169.254.169.254/latest/meta-data/instance-id)

# 구동중인 컨테이너가 없으면 UP
if [ -z "$EXIST_BLUE" ]; then
    # BLUE 컨테이너를 구동하기 위한 변수 설정
    export HOST_PORT=${BLUE_PORT}
    export CONTAINER_TYPE=blue
    # [BLUE] container hostname 에 사용. ({ec2_instance_id}-{app_name}-{blue/green} docker-compose.yaml 파일)
    export HOST_NAME="${EC2_ID}-${DOCKER_APP_NAME}-${CONTAINER_TYPE}"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] HOST_NAME=${HOST_NAME}" | tee -a deploy.log

    # BLUE 컨테이너 UP
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] BLUE UP PROCESS.." | tee -a deploy.log

    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml pull ${DOCKER_APP_NAME}" | tee -a deploy.log
    docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml pull ${DOCKER_APP_NAME} > /dev/null 2>&1

    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml up -d --force-recreate --build ${DOCKER_APP_NAME}" | tee -a deploy.log
    docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml up -d --force-recreate --build ${DOCKER_APP_NAME} | tee -a deploy.log

    # BLUE 컨테이너가 구동되기까지 30초정도 대기
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] BLUE DETECT ${DELAY_TIME}S WAITING.."
    sleep ${DELAY_TIME}

    # Run AWS Cloudwatch Agent (only for stage and production)
    if [ "${PROFILE}" != "dev" ];then
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] AWS CloudWatch Agent UP PROCESS.." | tee -a deploy.log
        docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo dpkg -i -E tmp/amazon-cloudwatch-agent.deb | tee -a deploy.log
        # CollectD file setting for AWS Cloudwatch
        docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo mkdir -p /usr/share/collectd | tee -a deploy.log
        docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo touch /usr/share/collectd/types.db | tee -a deploy.log
        nohup docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo /opt/aws/amazon-cloudwatch-agent/bin/start-amazon-cloudwatch-agent 1>/dev/null 2>&1 &
    fi

    EXIST_BLUE=$(docker ps --filter name=${DOCKER_APP_NAME}-blue --format "{{.ID}}")
    EXIST_GREEN=$(docker ps --filter name=${DOCKER_APP_NAME}-green --format "{{.ID}}")

    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] EXIST_BLUE = $EXIST_BLUE, EXIST_GREEN = $EXIST_GREEN" | tee -a deploy.log
    # BLUE 컨테이너 구동이 확인되고 만약 블루/그린 두개의 컨테이너가 구동중이라면 30초 후에 GREEN DOWN
    if [ "$EXIST_BLUE" ] && [ "$EXIST_GREEN" ]; then
        # GREEN 컨테이너를 중지하기 위한 변수 설정
        export HOST_PORT=${GREEN_PORT}
        export CONTAINER_TYPE=green
        # [BLUE] container hostname 에 사용. ({ec2_instance_id}-{app_name}-{blue/green} docker-compose.yaml 파일)
        export HOST_NAME="${EC2_ID}-${DOCKER_APP_NAME}-${CONTAINER_TYPE}"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] HOST_NAME=${HOST_NAME}" | tee -a deploy.log

        # GREEN APPLICATION Graceful Shutdown
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] Green Graceful Shutdown Start..." | tee -a deploy.log
        echo "docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} ps -ef | grep app.jar"' | grep -v "grep" | awk "{print $2}"'
        PID=$(docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} ps -ef | grep app.jar | grep -v 'grep' | awk '{print $2}')

        if [ -z "$PID" ]; then
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] PID Empty retry search ..." | tee -a deploy.log
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} ps -ef | grep app.jar | "'grep -v "grep" | awk "{print $2}"' | tee -a deploy.log
          PID=$(docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} ps -ef | grep app.jar | grep -v 'grep' | awk '{print $2}')
        fi

        if [ -z "$PID" ]; then
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] PID Empty=${PID}" | tee -a deploy.log
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] EXIT" | tee -a deploy.log
          exit
        fi

        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] SpringBoot Application PID=${PID}" | tee -a deploy.log

        ## 지워지지 않은 EUREKA REGISTRY 서비스 강제 삭제 처리를 위한 REGISTRY URI 생성
        #DELETE_REGISTRY=$(docker ps -f name=${DOCKER_APP_NAME}-${CONTAINER_TYPE} -q)
        #DELETE_REGISTRY="${SERVER_IP}"
        DELETE_REGISTRY="${EC2_ID}-${DOCKER_APP_NAME}-${CONTAINER_TYPE}"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] DELETE_REGISTRY=${DELETE_REGISTRY}" | tee -a deploy.log
        DELETE_REGISTRY_URI="${EUREKA_DOMAIN}/eureka/apps/${DOCKER_APP_NAME^^}/${DELETE_REGISTRY}:${DOCKER_APP_NAME}:${HOST_PORT}"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] DELETE_REGISTRY_URI=${DELETE_REGISTRY_URI}" | tee -a deploy.log

        ## 지워지지 않은 EUREKA REGISTRY 서비스 강제 삭제 처리
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] curl -X DELETE ${DELETE_REGISTRY_URI}" | tee -a deploy.log
        curl -X DELETE "${DELETE_REGISTRY_URI}" | tee -a deploy.log

        # GREEN APPLICATION 정상종료 시그널 전송
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} kill -15 ${PID}" | tee -a deploy.log
        docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} kill -15 ${PID} | tee -a deploy.log

        # GREEN APPLICATION 로그 출력 컨테이너 종료 시점을 잡기 위해 출력
        # APPLICATION이 종료되면 로그가 닫히고 다음으로 넘어감
#        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml logs -f -t --tail='all' ${DOCKER_APP_NAME}" | tee -a deploy.log
#        docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml logs -f -t --tail="all" ${DOCKER_APP_NAME} | tee -a deploy.log

        # GREEN APPLICATION 종료될때까지 30초 대기 후 종료절차 시작
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] ${DOCKER_APP_NAME}-${CONTAINER_TYPE} 종료 ${DELAY_TIME}초 대기" | tee -a deploy.log
        sleep ${DELAY_TIME}

        # GREEN 컨테이너 DOWN
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-3] GREEN DOWN PROCESS.." | tee -a deploy.log

        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-3] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml down" | tee -a deploy.log
        docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml down | tee -a deploy.log

        ## 지워지지 않은 EUREKA REGISTRY 서비스 강제 삭제 처리
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] curl -X DELETE ${DELETE_REGISTRY_URI}" | tee -a deploy.log
        curl -X DELETE "${DELETE_REGISTRY_URI}" | tee -a deploy.log
    fi
# 구동중인 컨테이너가 있으면 down
else
    # GREEN 컨테이너를 구동하기 위한 변수 설정
    export HOST_PORT=${GREEN_PORT}
    export CONTAINER_TYPE=green
    # [GREEN] container hostname 에 사용. ({ec2_instance_id}-{app_name}-{blue/green} docker-compose.yaml 파일)
    export HOST_NAME="${EC2_ID}-${DOCKER_APP_NAME}-${CONTAINER_TYPE}"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] HOST_NAME=${HOST_NAME}" | tee -a deploy.log

    # GREEN 컨테이너 UP
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] GREEN UP PROCESS.." | tee -a deploy.log

    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml pull ${DOCKER_APP_NAME}" | tee -a deploy.log
    docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml pull ${DOCKER_APP_NAME} > /dev/null 2>&1

    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml up -d --force-recreate --build ${DOCKER_APP_NAME}" | tee -a deploy.log
    docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml up -d --force-recreate --build ${DOCKER_APP_NAME} | tee -a deploy.log

    # GREEN 컨테이너가 구동되기까지 30초정도 대기
    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] GREEN DETECT ${DELAY_TIME}S WAITING.." | tee -a deploy.log
    sleep ${DELAY_TIME}

    # Run AWS Cloudwatch Agent (only for stage and production)
    if [ "${PROFILE}" != "dev" ];then
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] AWS CloudWatch Agent UP PROCESS.." | tee -a deploy.log
        docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo dpkg -i -E tmp/amazon-cloudwatch-agent.deb | tee -a deploy.log
        # CollectD file setting for AWS Cloudwatch
        docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo mkdir -p /usr/share/collectd | tee -a deploy.log
        docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo touch /usr/share/collectd/types.db | tee -a deploy.log
        nohup docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} sudo /opt/aws/amazon-cloudwatch-agent/bin/start-amazon-cloudwatch-agent 1>/dev/null 2>&1 &
    fi

    EXIST_BLUE=$(docker ps --filter name=${DOCKER_APP_NAME}-blue --format "{{.ID}}")
    EXIST_GREEN=$(docker ps --filter name=${DOCKER_APP_NAME}-green --format "{{.ID}}")

    echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-1] EXIST_BLUE = $EXIST_BLUE, EXIST_GREEN = $EXIST_GREEN" | tee -a deploy.log

    # GREEN 컨테이너 구동이 확인되고 만약 블루/그린 두개의 컨테이너가 구동중이라면 30초 후에 BLUE DOWN
    if [ "$EXIST_GREEN" ] && [ "$EXIST_BLUE" ]; then
        # BLUE 컨테이너를 구동하기 위한 변수 설정
        export HOST_PORT=${BLUE_PORT}
        export CONTAINER_TYPE=blue
        # [BLUE] container hostname 에 사용. ({ec2_instance_id}-{app_name}-{blue/green} docker-compose.yaml 파일)
        export HOST_NAME="${EC2_ID}-${DOCKER_APP_NAME}-${CONTAINER_TYPE}"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] HOST_NAME=${HOST_NAME}" | tee -a deploy.log

        # BLUE APPLICATION Graceful Shutdown
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] Blue Graceful Shutdown Start..." | tee -a deploy.log
        echo "docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} ps -ef | grep app.jar"' | grep -v "grep" | awk "{print $2}"'
        PID=$(docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} ps -ef | grep app.jar | grep -v 'grep' | awk '{print $2}')

        if [ -z "$PID" ]; then
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] PID Empty retry search ..." | tee -a deploy.log
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} ps -ef | grep app.jar | "'grep -v "grep" | awk "{print $2}"' | tee -a deploy.log
          PID=$(docker exec -u 0 ${DOCKER_APP_NAME}-${CONTAINER_TYPE} ps -ef | grep app.jar | grep -v 'grep' | awk '{print $2}')
        fi

        if [ -z "$PID" ]; then
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] PID Empty=${PID}" | tee -a deploy.log
          echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] EXIT" | tee -a deploy.log
          exit
        fi

        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] SpringBoot Application PID=${PID}" | tee -a deploy.log

        ## 지워지지 않은 EUREKA REGISTRY 서비스 강제 삭제 처리를 위한 REGISTRY URI 생성
        #DELETE_REGISTRY=$(docker ps -f name=${DOCKER_APP_NAME}-${CONTAINER_TYPE} -q)
        #DELETE_REGISTRY="${SERVER_IP}"
        DELETE_REGISTRY="${EC2_ID}-${DOCKER_APP_NAME}-${CONTAINER_TYPE}"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] DELETE_REGISTRY=${DELETE_REGISTRY}" | tee -a deploy.log
        DELETE_REGISTRY_URI="${EUREKA_DOMAIN}/eureka/apps/${DOCKER_APP_NAME^^}/${DELETE_REGISTRY}:${DOCKER_APP_NAME}:${HOST_PORT}"
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] DELETE_REGISTRY_URI=${DELETE_REGISTRY_URI}" | tee -a deploy.log

        ## 지워지지 않은 EUREKA REGISTRY 서비스 강제 삭제 처리
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] curl -X DELETE ${DELETE_REGISTRY_URI}" | tee -a deploy.log
        curl -X DELETE "${DELETE_REGISTRY_URI}" | tee -a deploy.log

        # BLUE APPLICATION 정상종료 시그널 전송
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} kill -15 ${PID}" | tee -a deploy.log
        docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml exec -T ${DOCKER_APP_NAME} kill -15 ${PID} | tee -a deploy.log

        # BLUE APPLICATION 로그 출력 컨테이너 종료 시점을 잡기 위해 출력
        # APPLICATION이 종료되면 로그가 닫히고 다음으로 넘어감
#        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml logs -f -t --tail='all' ${DOCKER_APP_NAME}" | tee -a deploy.log
#        docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml logs -f -t --tail="all" ${DOCKER_APP_NAME} | tee -a deploy.log
        #sleep ${DELAY_TIME}

        # BLUE APPLICATION 종료될때까지 30초 대기 후 종료절차 시작
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] ${DOCKER_APP_NAME}-${CONTAINER_TYPE} 종료 ${DELAY_TIME}초 대기" | tee -a deploy.log
        sleep ${DELAY_TIME}

        # BLUE 컨테이너 DOWN
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-3] BLUE DOWN PROCESS.." | tee -a deploy.log

        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-3] docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml down" | tee -a deploy.log
        docker-compose -p ${DOCKER_APP_NAME}-${CONTAINER_TYPE} -f ./${DOCKER_COMPOSE_FILENAME}.yml down | tee -a deploy.log

        ## 지워지지 않은 EUREKA REGISTRY 서비스 강제 삭제 처리
        echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-2] curl -X DELETE ${DELETE_REGISTRY_URI}" | tee -a deploy.log
        curl -X DELETE "${DELETE_REGISTRY_URI}" | tee -a deploy.log
    fi
fi

## 이름이 없거나 태그가 없는 사용되지 않는 모든 이미지 삭제
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-4] docker image prune -a -f" | tee -a deploy.log
docker image prune -a -f | tee -a deploy.log # > /dev/null 2>&1
## 중지된 모든 컨테이너 삭제
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-4] docker container prune -f" | tee -a deploy.log
docker container prune -f | tee -a deploy.log # > /dev/null 2>&1
## 중지된 모든 네트워크 삭제
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-4] docker network prune -f" | tee -a deploy.log
docker network prune -f | tee -a deploy.log # > /dev/null 2>&1
## 중지된 모든 볼륨 삭제
echo "[$(date '+%Y-%m-%d %H:%M:%S')][STEP-4] docker volume prune -f" | tee -a deploy.log
docker volume prune -f | tee -a deploy.log # > /dev/null 2>&1