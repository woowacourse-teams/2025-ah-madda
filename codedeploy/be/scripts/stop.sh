#!/bin/bash
# scripts/stop.sh

PID=$(pgrep -f "ahmadda-server-prod.jar")

if [ -z "$PID" ]; then
  echo "애플리케이션이 실행 중이지 않습니다."
else
  echo "애플리케이션을 중지합니다. (PID: $PID)"
  kill -15 "$PID"
  sleep 5
fi
