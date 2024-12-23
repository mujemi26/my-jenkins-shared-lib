#!/usr/bin/env groovy

def call(Map config = [:]) {
    // Ensure required parameters are provided
    if (!config.deploymentName || !config.containerName || !config.imageName || !config.imageVersion) {
        error "Missing required parameters. Please provide deploymentName, containerName, imageName, and imageVersion."
    }

    withCredentials([string(credentialsId: 'kind-kubeconfig', variable: 'KUBECONFIG')]) {
        // Define the Kubernetes deployment manifest
        def deploymentManifest = """
apiVersion: apps/v1
kind: Deployment
metadata:
  name: ${config.deploymentName}
spec:
  replicas: 1
  selector:
    matchLabels:
      app: ${config.containerName}
  template:
    metadata:
      labels:
        app: ${config.containerName}
    spec:
      containers:
      - name: ${config.containerName}
        image: ${config.imageName}:${config.imageVersion}
        ports:
        - containerPort: 8080
"""

        script {
            // Write the deployment manifest to a file
            writeFile file: 'deployment.yaml', text: deploymentManifest

            // Apply the deployment using kubectl with the provided KUBECONFIG
            sh 'kubectl apply -f deployment.yaml'

            // Clean up the manifest file
            sh 'rm deployment.yaml'
        }
    }
}
