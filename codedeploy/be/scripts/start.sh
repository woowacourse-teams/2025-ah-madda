#!/bin/bash
# scripts/start.sh

APP_DIR="/home/ubuntu/app/"
JAR_FILE="ahmadda-server-prod.jar"
PROFILE="prod"

echo "테스트 애플리케이션을 시작합니다..."

python3 -m http.server 8080 &
