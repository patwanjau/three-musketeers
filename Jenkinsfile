pipeline {
    agent { docker { image 'gradle:8.14.0-jdk21' } }
    stages {
        stage('Build Tool Version Check') {
            steps {
                sh 'gradle --version'
            }
        }
    }
}
