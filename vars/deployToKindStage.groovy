#!/usr/bin/env groovy

def call(Map config) {
    // Extract parameters from config
    def imageName = config.DOCKER_IMAGE_NAME
    def imageVersion = config.DOCKER_IMAGE_VERSION
    def deploymentName = config.DEPLOYMENT_NAME
    def containerName = config.CONTAINER_NAME
    
    // Deploy using kubectl
   
    withKubeConfig([credentialsId: 'kind-kubeconfig']) {
        // Create or update the deployment
        sh """
            kubectl create deployment ${deploymentName} \
                --image=${imageName}:${imageVersion} \
                --dry-run=client -o yaml | kubectl apply -f -

            # Update the container name if it exists
            kubectl set env deployment/${deploymentName} \
                CONTAINER_NAME=${containerName}

            # Expose the deployment as a service
            kubectl expose deployment ${deploymentName} \
                --port=8080 \
                --target-port=8080 \
                --type=NodePort \
                --dry-run=client -o yaml | kubectl apply -f -

            # Wait for deployment to be ready
            kubectl rollout status deployment/${deploymentName}
        """
        
        // Get service information
        sh """
            echo "Service details:"
            kubectl get service ${deploymentName} -o wide
        """
    }
}
