#!groovy 
import org.foo.Command
def call(Map map){
    pipeline {
        agent any
        environment {
            tag = "${map.tag}.${BUILD_NUMBER}"
            registry = "registry.ecloud.work"
            scanPath = "./target/dependency/*.jar"
            imageName = "gwcloud/isearch-api:${tag}"
            gitCredentialsId = "0afa4ce7-6007-43bd-9b44-be880905b5c9"
            dockerCredential = "registry-hub-credentials"
            sonarQubeScannerHome = "sonar-scanner-3.2.0.1227"
            yamlName = "isearch-api.template.yaml"
            postmanCJ = "./src/test/resources/isearch.postman_collection.json"
            postmanEJ = "./src/test/resources/isearch.postman_env.json"
            reporterEXml = "./surefire-reports/api-reporter.xml"
            reporterEHtml = "./reporter.html"
            reporterTHtml = "./src/test/resources/isearch.postman_html.hbs"
            reportName = "ISearchAPI测试报告"
            reportTitles = "ISearchAPI"
        }
        tools {
            maven 'maven'
            jdk 'jdk'
        }
        def comm = new org.foo.Command(steps)
        stages {
            stage('CheckOut') {
                steps {
                    comm.checkOutFromGit("${map.branch}","${gitCredentialsId}","${map.repoUrl}")
                }
            }
            stage('编译&单元测试') {
                steps {
                    comm.packageAndJunit()
                }
            }
            stage('sync') {
                parallel {
                    stage('代码审查') {
                        strps {
                            script {
                                comm.codeReview("${sonarQubeScannerHome}")
                            }
                        }
                    }
                    stage('生成镜像') {
                        steps {
                            script {
                                comm.buildImage(symbols,"${registry}","${dockerCredential}","${imageName}")
                            }
                        }
                    }
                    stage('依赖安全检查') {
                        steps {
                            comm.dependencyCheck(symbols,"${scanPath}")
                        }
                    }
                }
            }
            stage('部署到集群') {
                steps {
                    comm.applyK8s("${yamlName}","${tag}")
                }
            }
            stage('API测试') {
                steps {
                    comm.apiTest(symbols,"${postmanCJ}",
                        "${postmanEJ}",
                        "${reporterEXml}",
                        "${reporterEHtml}",
                        "${reporterTHtml}",
                        "${reportName}",
                        "${reportTitles}")
                }
            }
        }
    }
}
