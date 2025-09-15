#!/bin/bash
# scripts/start.sh

APP_DIR="/home/ubuntu/app"
JAR_FILE="ahmadda-server-prod.jar"
LOG_FILE="/home/ubuntu/app/app.log"
PROFILE="PROD"

echo "애플리케이션을 시작합니다..."
nohup java -jar -Dspring.profiles.active=$PROFILE "$APP_DIR/$JAR_FILE" > "$LOG_FILE" 2>&1 &
