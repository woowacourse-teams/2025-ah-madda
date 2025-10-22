#!/bin/bash
# scripts/start.sh

echo "테스트 애플리케이션을 시작합니다..."

# 백그라운드 프로세스의 stdout과 stderr를 /dev/null로 리디렉션
nohup python3 - <<'EOF' > /dev/null 2>&1 &
from http.server import HTTPServer, SimpleHTTPRequestHandler

class Handler(SimpleHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/actuator/health":
            self.send_response(200)
            self.end_headers()
            self.wfile.write(b"OK")
        else:
            super().do_GET()

HTTPServer(("", 80), Handler).serve_forever()
EOF

