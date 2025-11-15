pipeline {
    agent any

    tools {
        // Имена должны совпадать с тем, что настроено в Jenkins → Global Tool Configuration
        jdk 'jdk17'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Исходники уже выкачаны Jenkins из SCM'
                // Если job настроена как "Pipeline script from SCM",
                // Jenkins сам клонирует репозиторий перед выполнением пайплайна.
            }
        }

        stage('Run tests') {
            steps {
                // Для Windows-агента
                bat 'mvn clean test'
                // Для Linux-агента было бы: sh 'mvn clean test'
            }
        }

        stage('Allure report') {
            steps {
                // Публикация Allure-отчёта (нужен установленный Allure Jenkins Plugin
                // и настроенный Allure Commandline в "Manage Jenkins → Tools")
                allure includeProperties: false,
                       jdk: '',
                       results: [[path: 'target/allure-results']]
            }
        }
    }

    post {
        always {
            // Публикация результатов JUnit на вкладке "Test Result"
            junit 'target/surefire-reports/*.xml'
        }
    }
}
