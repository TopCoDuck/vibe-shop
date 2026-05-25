@echo off
REM =============================================
REM  Vibe Shop 로컬 빌드 + 배포 스크립트
REM  Jenkins 없이 수동으로 실행할 때 사용
REM =============================================

set JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-25.0.3.9-hotspot
set GRADLE_OPTS=--enable-native-access=ALL-UNNAMED
set ROOT_DIR=%~dp0..
set BACKEND_DIR=%ROOT_DIR%\backend
set FRONTEND_DIR=%ROOT_DIR%\frontend
set DEPLOY_DIR=D:\deploy\vibe-shop

echo ============================================
echo  [1/4] 백엔드 빌드
echo ============================================
cd /d "%BACKEND_DIR%"
call gradlew.bat clean build -x test --no-daemon
if %ERRORLEVEL% neq 0 ( echo [ERROR] 백엔드 빌드 실패 & exit /b 1 )

echo ============================================
echo  [2/4] 프론트엔드 빌드
echo ============================================
cd /d "%FRONTEND_DIR%"
call npm ci
call npm run build
if %ERRORLEVEL% neq 0 ( echo [ERROR] 프론트엔드 빌드 실패 & exit /b 1 )

echo ============================================
echo  [3/4] 배포 파일 복사
echo ============================================
if not exist "%DEPLOY_DIR%\frontend" mkdir "%DEPLOY_DIR%\frontend"
copy /Y "%BACKEND_DIR%\build\libs\*.jar" "%DEPLOY_DIR%\app.jar"
xcopy /E /Y /I "%FRONTEND_DIR%\dist\*" "%DEPLOY_DIR%\frontend\"

echo ============================================
echo  [4/4] 서버 재시작
echo ============================================
cd /d "%ROOT_DIR%\scripts"
call stop.bat
call start.bat

echo ============================================
echo  배포 완료!  http://localhost:8080
echo ============================================
