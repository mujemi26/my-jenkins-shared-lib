#!/usr/bin/env groovy
// vars/dockerBuild.groovy

def call(Map config){
    def imageTag = "${config.imageName}:${config.version}";
    
    sh """
    docker build -t ${imageTag} .
    """
}
