#!/usr/bin/env groovy
// vars/deployToKind.groovy

def call(Map config) {
     def imageTag = "${config.imageName}:${config.version}";

   sh """
       kubectl apply -f deployment.yaml
        kubectl set image deployment/${config.deploymentName} ${config.containerName}=${imageTag}
    """
}
