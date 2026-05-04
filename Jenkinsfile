pipeline {
    agent any

    environment {
        APP_NAME = 'timely-api'
        IMAGE_NAME = 'timely-api:latest'
        CONTAINER_NAME = 'timely-api'
        HOST_PORT = '8081'
        CONTAINER_PORT = '8080'
        DB_URL = 'jdbc:mysql://mysql:3306/devdb?serverTimezone=Asia/Seoul&characterEncoding=UTF-8&useSSL=false&allowPublicKeyRetrieval=true'
        DB_USER = 'dev'
    }

    stages {
        stage('Build') {
            steps {
                sh './gradlew clean compileKotlin --no-daemon'
            }
        }

        stage('Deploy') {
            steps {
                withCredentials([string(credentialsId: 'timely-db-password', variable: 'DB_PASSWORD')]) {
                    sh 'chmod +x scripts/deploy.sh'
                    sh './scripts/deploy.sh'
                }
            }
        }
    }
}
