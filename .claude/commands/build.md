# /project:build — 백엔드 빌드 & 재시작

PowerShell로 다음을 실행해줘:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
Set-Location "D:\eclipse\workspace\vibe-shop\backend"
```

1. **빌드**
   ```
   .\gradlew.bat bootJar --no-daemon -q
   ```
   성공/실패 여부 확인

2. **기존 서버 종료** — 포트 8080 점유 프로세스 kill

3. **새 JAR 실행**
   ```
   build/libs/vibe-shop-backend-0.0.1-SNAPSHOT.jar
   ```
   백그라운드로 시작 후 10초 대기

4. **기동 확인** — `curl http://localhost:8080/api/faq` 로 200 응답 확인

각 단계 결과를 순서대로 알려줘.
