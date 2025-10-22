#!/bin/bash

HEALTH_CHECK_URL="http://localhost:80/actuator/health"
TIMEOUT=30

echo "Validating service health at $HEALTH_CHECK_URL..."

for (( i=1; i<=$TIMEOUT; i++ )); do
  STATUS_CODE=$(curl -s -o /dev/null -w "%{http_code}" $HEALTH_CHECK_URL)

  if [ "$STATUS_CODE" -eq 200 ]; then
    echo "Validation successful! (Status: $STATUS_CODE)"
    exit 0
  else
    echo "Validation attempt $i/$TIMEOUT failed (Status: $STATUS_CODE). Retrying in 1s..."
  fi

  sleep 1
done

echo "Service validation failed after $TIMEOUT seconds."
exit 1
