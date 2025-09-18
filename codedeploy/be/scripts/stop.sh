#!/bin/bash
# scripts/stop.sh

PORT=8080
PID=$(lsof -ti tcp:$PORT)

if [ -z "$PID" ]; then
  echo "애플리케이션이 실행 중이지 않습니다."
else
  echo "애플리케이션을 중지합니다. (PID: $PID)"
  kill -15 "$PID"
  sleep 5
fi
