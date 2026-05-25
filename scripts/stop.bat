@echo off
REM =============================================
REM  Vibe Shop 중지 스크립트
REM =============================================

set PID_FILE=D:\deploy\vibe-shop\app.pid

if not exist "%PID_FILE%" (
    echo [INFO] 실행 중인 프로세스가 없습니다.
    exit /b 0
)

set /p PID=<"%PID_FILE%"
echo [INFO] PID %PID% 프로세스 종료 중...
taskkill /PID %PID% /F
del "%PID_FILE%"
echo [INFO] 종료 완료.
