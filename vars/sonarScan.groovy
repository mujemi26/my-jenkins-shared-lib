#!/usr/bin/env groovy
// vars/sonarScan.groovy
def call(Map config) {
    withSonarQubeEnv('mySonarQube') {
    sh """
    ./gradlew sonarqube \
      -Dsonar.projectKey=${config.projectKey} \
      -Dsonar.projectName=${config.projectName} \
      -Dsonar.host.url=${config.sonarHostUrl}
    """
    }
}
