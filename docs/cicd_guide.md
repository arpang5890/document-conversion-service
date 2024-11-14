# CI/CD Pipeline for Document Service

This document outlines the CI/CD pipeline setup for the **Document Service** using GitHub Actions. The pipeline automates the build, test, and deployment processes for code pushed to the `master` branch.

## Overview

The CI/CD pipeline ensures that code changes are automatically tested and deployed to the production environment hosted on Amazon Elastic Kubernetes Service (EKS).

### Trigger

- The workflow is triggered whenever code is pushed to the `master` branch.

## Required GitHub Secrets

To run the CI/CD pipeline, the following secrets need to be configured in your GitHub repository under **Settings** > **Secrets and variables** > **Actions**:

| Secret Name             | Description                                                      |
|-------------------------|------------------------------------------------------------------|
| `AWS_ACCOUNT_ID`        | Your AWS Account ID where the ECR and EKS clusters are hosted.  |
| `AWS_ACCESS_KEY_ID`     | AWS access key with appropriate permissions.                     |
| `AWS_SECRET_ACCESS_KEY` | Secret access key for AWS authentication.                        |
| `AWS_REGION`            | The AWS region (e.g., `us-west-2`) for ECR and EKS.              |

## Pipeline Steps

The CI/CD workflow consists of the following steps:

1. **Checkout Code**: Clones the repository to the workflow runner.
2. **Set Up JDK 21**: Configures the build environment with Java 21.
3. **Build with Maven**: Compiles and packages the application.
4. **Configure AWS Credentials**: Authenticates with AWS using GitHub secrets.
5. **Create ECR Repository**: Checks if the ECR repository exists; if not, creates it.
6. **Log in to Amazon ECR**: Authenticates Docker to Amazon ECR.
7. **Build Docker Image**: Builds the Docker image and tags it with the commit SHA and `latest`.
8. **Push Docker Image to ECR**: Pushes the Docker image to the Amazon ECR.
9. **Update Kubernetes Deployment YAML**: Updates the image in the Kubernetes deployment file.
10. **Install `kubectl`**: Installs `kubectl` for interacting with the EKS cluster.
11. **Update kubeconfig for EKS**: Configures access to the EKS cluster.
12. **Deploy to EKS**: Applies the updated configuration using `kubectl`.
13. **Verify Deployment**: Checks the deployment status for successful rollout.