#!/bin/bash
# scripts/validate.sh

for i in {1..12}; do
  HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/actuator/health)

  if [ "$HTTP_CODE" -eq 200 ]; then
    echo "애플리케이션 시작 확인"
    exit 0
  fi

  echo "애플리케이션 시작 대기 중... ($i/10)"
  sleep 6
done

echo "애플리케이션 시작 실패"
exit 1
