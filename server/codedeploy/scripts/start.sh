#!/bin/bash

APP_DIR="/home/ubuntu/app"
JAR_NAME="ahmadda-server-prod.jar"
LOG_FILE="$APP_DIR/nohup.out"
APP_PORT=80

echo "Starting $JAR_NAME at $APP_DIR..."

PIDS=$(pgrep -f $JAR_NAME)
if [ -n "$PIDS" ]; then
  echo "Stopping old process(es): $PIDS"
  kill -9 $PIDS
  sleep 2
fi

nohup java -Duser.timezone=Asia/Seoul -jar $APP_DIR/$JAR_NAME \
  --server.port=$APP_PORT \
  --spring.profiles.active=prod \
  > $LOG_FILE 2>&1 < /dev/null &

echo "Application started on port $APP_PORT with PID $(pgrep -f $JAR_NAME)"
exit 0

