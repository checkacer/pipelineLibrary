#!groovy
def call(){
    def u = new org.foo.Utilities()
    pipeline {
        agent any
        environment {
            registry = "registry.cn-hangzhou.aliyuncs.com"
            gitCredentialsId = "a6600fad-d566-4408-b024-2d5e8ea29311"
            dockerCredential = "aliyun"
            branch = "master"
            repoUrl = "https://github.com/checkacer/runindockerdemo"
        }
        tools {
            maven 'maven'
            jdk 'jdk'
        }
        stages {
            stage('test') {
                steps {
                    script {
                        u.setSteps(steps)
                        echo "Hello World"
                    }
                }
            }
        }
    }
}