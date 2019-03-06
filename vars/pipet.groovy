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
            imageName = "api-t"
        }
        tools {
            maven 'maven'
            jdk 'jdk'
        }
        stages {
            stage('checkOut') {
                steps {
                    script {
                        u.setSteps(steps)
                        u.checkOutFromGit("${branch}","${gitCredentialsId}","${repoUrl}")
                        echo "Hello World"
                    }
                }
            }
            stage('package'){
                steps {
                    script {
                        u.packageAndJunit()
                    }
                }
            }
            stage('build') {
                steps {
                    script {
                        u.buildImage("registry","dockerCredential","${imageName}")
                    }
                }
            }
        }
    }
}