#!/bin/bash

if [ "$SPRING_PROFILE" == "stage" ] || [ "$SPRING_PROFILE" == "prod" ]; then
  # Run AWS Cloudwatch Agent (only for stage and production)
  sudo /opt/aws/amazon-cloudwatch-agent/bin/start-amazon-cloudwatch-agent &
fi

# Run Java Application
java ${JAVA_OPTS}