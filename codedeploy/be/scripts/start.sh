#!/bin/bash
# scripts/start.sh

APP_DIR="/home/ubuntu/app/"
JAR_FILE="ahmadda-server-prod.jar"
PROFILE="prod"

echo "애플리케이션을 시작합니다..."
nohup sudo java -Dspring.profiles.active=$PROFILE -jar $APP_DIR$JAR_FILE &
