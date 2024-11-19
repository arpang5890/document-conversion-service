# Build, Test and Deploy Service to EKS GitHub Action

## Overview

The workflow consists of two main jobs:
- **`test`**: Builds and runs tests for the service.
- **`deploy`**: Deploys the service to EKS after successful testing.

### Features
- Runs on `ubuntu-latest`.
- Uses RabbitMQ service for integration tests.
- Loads AWS configurations from a secret JSON stored in GitHub secrets.
- Builds the service using Maven.
- Pushes Docker images to Amazon ECR.
- Creates an EKS cluster and node group if they do not exist.
- Deploys the application and services to the EKS cluster.

## Workflow Details

### Trigger
- The workflow is triggered by a `push` event on the `master` branch.

### Jobs

#### 1. **Test Job**
- **Runs-on**: `ubuntu-latest`
- **Services**: RabbitMQ
- **Steps**:
    - Checkout the code.
    - Set up JDK 21 with Maven cache.
    - Load AWS configuration from secrets.
    - Run unit tests (`mvn test`).
    - Run integration tests (`mvn verify -P integration-test`).
    - Upload test reports as artifacts.

#### 2. **Deploy Job**
- **Runs-on**: `ubuntu-latest`
- **Needs**: `test` job (depends on the successful completion of the `test` job)
- **Steps**:
    - Checkout the code.
    - Load AWS configuration from secrets.
    - Set up JDK 21 with Maven cache.
    - Configure AWS credentials using `aws-actions/configure-aws-credentials`.
    - Build the application using Maven (`mvn clean package`).
    - Check if ECR repository exists; create it if not.
    - Check if EKS cluster and node group exist; create them if needed.
    - Update kubeconfig for cluster access.
    - Log in to Amazon ECR and push the Docker image.
    - Deploy RabbitMQ to the cluster.
    - Deploy the application by updating the image URL in Kubernetes manifests.

## Environment Variables
These are loaded from the GitHub secret `AWS_CONFIG`:
```json
{
  "aws": {
    "region": "****",
    "account_id": "****",
    "access_key_id": "****",
    "secret_access_key": "****"
  },
  "eks": {
    "cluster_name": "****",
    "cluster_version": "1.31",
    "node_group": {
      "name": "****",
      "instance_type": "t3a.medium",
      "min_size": 2,
      "max_size": 3,
      "desired_size": 2
    }
  },
  "ecr": {
    "repository_name": "****"
  }
}


## Prerequisites
- Store the AWS configurations in GitHub secrets as `AWS_CONFIG` in JSON format.
- Ensure IAM roles and permissions are configured in AWS for creating and managing EKS resources.
