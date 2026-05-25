pipeline {
    agent any

    environment {
        JAVA_HOME    = 'C:\\Program Files\\Eclipse Adoptium\\jdk-25.0.3.9-hotspot'
        GRADLE_OPTS  = '--enable-native-access=ALL-UNNAMED'
        APP_NAME     = 'vibe-shop'
        BACKEND_DIR  = "${WORKSPACE}\\backend"
        FRONTEND_DIR = "${WORKSPACE}\\frontend"
        DEPLOY_DIR   = 'D:\\deploy\\vibe-shop'
        PID_FILE     = 'D:\\deploy\\vibe-shop\\app.pid'
        NOTIFY_EMAIL = 'wpwp102@gmail.com'
        IS_MAIN      = "${env.BRANCH_NAME == 'main'}"
    }

    tools {
        nodejs 'NodeJS-22'
    }

    options {
        timestamps()
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 20, unit: 'MINUTES')
    }

    stages {

        // ──────────────────────────────────────────
        stage('Checkout') {
            steps {
                echo "📦 브랜치: ${env.BRANCH_NAME}"
                checkout scm
            }
        }

        // ──────────────────────────────────────────
        stage('Build') {
            parallel {
                stage('Backend Build') {
                    steps {
                        echo '🔨 백엔드 빌드 (Gradle)'
                        dir(BACKEND_DIR) {
                            bat "set JAVA_HOME=${env.JAVA_HOME} && gradlew.bat clean build -x test --no-daemon"
                        }
                    }
                }
                stage('Frontend Build') {
                    steps {
                        echo '🔨 프론트엔드 빌드 (npm)'
                        dir(FRONTEND_DIR) {
                            bat 'npm ci'
                            bat 'npm run build'
                        }
                    }
                }
            }
        }

        // ──────────────────────────────────────────
        stage('Test') {
            steps {
                echo '🧪 백엔드 테스트 실행'
                dir(BACKEND_DIR) {
                    bat "set JAVA_HOME=${env.JAVA_HOME} && gradlew.bat test --no-daemon"
                }
            }
            post {
                always {
                    junit allowEmptyResults: true,
                          testResults: 'backend/build/test-results/test/*.xml'
                }
            }
        }

        // ──────────────────────────────────────────
        // main 브랜치만 배포 실행
        stage('Deploy') {
            when {
                branch 'main'
            }
            steps {
                echo '🚀 배포 시작 (main 브랜치)'
                bat "if not exist \"${env.DEPLOY_DIR}\" mkdir \"${env.DEPLOY_DIR}\""
                bat "if not exist \"${env.DEPLOY_DIR}\\frontend\" mkdir \"${env.DEPLOY_DIR}\\frontend\""

                // 기존 프로세스 종료
                bat """
                    if exist "${env.PID_FILE}" (
                        set /p OLD_PID=<"${env.PID_FILE}"
                        taskkill /PID %OLD_PID% /F 2>nul || echo No process to kill
                        del "${env.PID_FILE}"
                    )
                """

                // JAR + 프론트엔드 dist 복사
                bat "copy /Y \"${env.BACKEND_DIR}\\build\\libs\\*.jar\" \"${env.DEPLOY_DIR}\\app.jar\""
                bat "xcopy /E /Y /I \"${env.FRONTEND_DIR}\\dist\\*\" \"${env.DEPLOY_DIR}\\frontend\\\""

                // Spring Boot 백그라운드 실행
                bat """
                    set JAVA_HOME=${env.JAVA_HOME}
                    start /B "" "${env.JAVA_HOME}\\bin\\java.exe" ^
                        --enable-native-access=ALL-UNNAMED ^
                        -jar "${env.DEPLOY_DIR}\\app.jar" ^
                        > "${env.DEPLOY_DIR}\\app.log" 2>&1

                    timeout /T 10 /NOBREAK

                    for /f "tokens=2" %%a in ('tasklist /FI "IMAGENAME eq java.exe" /FO LIST ^| findstr PID') do (
                        echo %%a > "${env.PID_FILE}"
                    )
                    echo 배포 완료.
                """
            }
        }

        // ──────────────────────────────────────────
        // main 브랜치만 헬스 체크
        stage('Health Check') {
            when {
                branch 'main'
            }
            steps {
                echo '❤️ 서버 헬스 체크'
                retry(5) {
                    sleep(time: 5, unit: 'SECONDS')
                    bat 'curl -f http://localhost:8080/api/products || exit 1'
                }
            }
        }
    }

    // ──────────────────────────────────────────
    post {
        success {
            script {
                if (env.BRANCH_NAME == 'main') {
                    // main: 배포 성공 알림
                    emailext to: "${env.NOTIFY_EMAIL}",
                             subject: "[${env.APP_NAME}] ✅ 배포 성공 - #${env.BUILD_NUMBER}",
                             body: """
배포 성공!

프로젝트  : ${env.APP_NAME}
빌드 번호 : #${env.BUILD_NUMBER}
브랜치    : ${env.BRANCH_NAME}
빌드 URL  : ${env.BUILD_URL}
소요 시간 : ${currentBuild.durationString}
서버 주소 : http://localhost:8080
"""
                } else {
                    // feature/*: 테스트 통과 알림
                    emailext to: "${env.NOTIFY_EMAIL}",
                             subject: "[${env.APP_NAME}] ✅ 빌드/테스트 통과 - ${env.BRANCH_NAME} #${env.BUILD_NUMBER}",
                             body: """
빌드 및 테스트 통과!

프로젝트  : ${env.APP_NAME}
빌드 번호 : #${env.BUILD_NUMBER}
브랜치    : ${env.BRANCH_NAME}
빌드 URL  : ${env.BUILD_URL}
소요 시간 : ${currentBuild.durationString}

※ 배포는 main 브랜치 머지 후 자동 실행됩니다.
"""
                }
            }
        }
        failure {
            emailext to: "${env.NOTIFY_EMAIL}",
                     subject: "[${env.APP_NAME}] ❌ 빌드 실패 - ${env.BRANCH_NAME} #${env.BUILD_NUMBER}",
                     body: """
빌드 실패!

프로젝트  : ${env.APP_NAME}
빌드 번호 : #${env.BUILD_NUMBER}
브랜치    : ${env.BRANCH_NAME}
빌드 URL  : ${env.BUILD_URL}
로그 확인 : ${env.BUILD_URL}console
"""
        }
        always {
            echo "🏁 파이프라인 종료 [${env.BRANCH_NAME}] - 상태: ${currentBuild.currentResult}"
            cleanWs()
        }
    }
}
