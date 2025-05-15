pipeline {
    agent { docker { image 'gradle:8.14.0-jdk21-alpine' } }
    stages {
        stage('build') {
            steps {
                sh 'gradle --version'
            }
        }
    }
}
