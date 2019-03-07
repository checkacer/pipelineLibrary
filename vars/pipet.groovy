#!groovy
def call(Map map){
    def u = new org.foo.Utilities()
    pipeline {
        agent any
        environment {
            registry = "registry.cn-hangzhou.aliyuncs.com"
//            gitCredentialsId = "a6600fad-d566-4408-b024-2d5e8ea29311"
            gitCredentialsId = "${map.gitCredentialsId}"
//            dockerCredential = "aliyun"
            dockerCredential = "${map.dockerCredential}"
//            branch = "master"
            branch = "${map.master}"
//            repoUrl = "https://github.com/checkacer/runindockerdemo"
            repoUrl = "${map.repoUrl}"
//            imageName = "dcits/api-t"
            imageName = "${map.imageName}"
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
                        echo "${branch} & ${gitCredentialsId} & ${repoUrl}"
                        u.checkOutFromGit("${branch}","${gitCredentialsId}","${repoUrl}")

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
                        u.buildImage(this,"${registry}","${dockerCredential}","${imageName}")
                    }
                }
            }
        }
    }
}