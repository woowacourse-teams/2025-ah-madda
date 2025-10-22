#!/bin/bash

JAR_NAME="ahmadda-server-prod.jar"
SHUTDOWN_WAIT_SECONDS=30 # 30초 동안 정상 종료(Graceful Shutdown)를 기다림

echo "Gracefully stopping $JAR_NAME..."

PIDS=$(pgrep -f $JAR_NAME)

if [ -z "$PIDS" ]; then
  echo "No process found for $JAR_NAME. Already stopped."
  exit 0
fi

echo "Found running process(es): $PIDS. Sending SIGTERM (15) to request graceful shutdown..."
kill -15 $PIDS

for (( i=1; i<=$SHUTDOWN_WAIT_SECONDS; i++ )); do
  PIDS_AFTER_TERM=$(pgrep -f $JAR_NAME)

  if [ -z "$PIDS_AFTER_TERM" ]; then
    echo "Application stopped gracefully after $i seconds."
    exit 0
  fi

  echo "Waiting for process(es) to stop... ($i/$SHUTDOWN_WAIT_SECONDS)"
  sleep 1
done

PIDS_STILL_RUNNING=$(pgrep -f $JAR_NAME)
if [ -n "$PIDS_STILL_RUNNING" ]; then
  echo "Process(es) $PIDS_STILL_RUNNING are still running after $SHUTDOWN_WAIT_SECONDS seconds."
  echo "Graceful shutdown is in progress. Script will exit without forced termination."
else
  echo "Application stopped gracefully at the last second."
fi

exit 0

