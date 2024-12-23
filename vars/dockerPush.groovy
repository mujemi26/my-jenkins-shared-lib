#!/usr/bin/env groovy
// vars/dockerPush.groovy

def call(Map config) {

     def imageTag = "${config.imageName}:${config.version}";
    
    withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKERHUB_USERNAME', passwordVariable: 'DOCKERHUB_PASSWORD')]) {
        sh """
         echo "${DOCKERHUB_PASSWORD}" | docker login -u "${DOCKERHUB_USERNAME}" --password-stdin
         docker push ${imageTag}
        """
    }
}
