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
fi

nohup java -jar $APP_DIR/$JAR_NAME --server.port=$APP_PORT --spring.profiles.active=prod

echo "Application started on port $APP_PORT"
exit 0
