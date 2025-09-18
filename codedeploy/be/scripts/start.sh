#!/bin/bash
# scripts/start.sh

APP_DIR="/home/ubuntu/app/"
JAR_FILE="ahmadda-server-prod.jar"
PROFILE="prod"

echo "테스트 애플리케이션을 시작합니다..."

python3 - <<'EOF' &
from http.server import HTTPServer, SimpleHTTPRequestHandler

class Handler(SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/actuator/health":
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b"OK")
        else:
            super().do_GET()

HTTPServer(("", 8080), Handler).serve_forever()
EOF
