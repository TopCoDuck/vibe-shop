@echo off
REM =============================================
REM  Vibe Shop 수동 시작 스크립트
REM =============================================

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot
set DEPLOY_DIR=D:\deploy\vibe-shop
set PID_FILE=%DEPLOY_DIR%\app.pid

echo [INFO] Vibe Shop 시작 중...

if not exist "%DEPLOY_DIR%" mkdir "%DEPLOY_DIR%"

REM 기존 프로세스 종료
if exist "%PID_FILE%" (
    set /p OLD_PID=<"%PID_FILE%"
    taskkill /PID %OLD_PID% /F 2>nul
    del "%PID_FILE%"
    echo [INFO] 기존 프로세스 종료 완료
)

REM 앱 시작
start /B "" "%JAVA_HOME%\bin\java.exe" ^
    --enable-native-access=ALL-UNNAMED ^
    -jar "%DEPLOY_DIR%\app.jar" ^
    > "%DEPLOY_DIR%\app.log" 2>&1

timeout /T 3 /NOBREAK > nul

REM PID 저장
for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr PID') do (
    echo %%a > "%PID_FILE%"
    echo [INFO] 시작 완료. PID: %%a
    goto :done
)

:done
echo [INFO] 로그: %DEPLOY_DIR%\app.log
