package org.foo

class Utils implements Serializable{
    def steps
    Utils(steps){
        this.steps = steps
    }
    // 下载代码
    def checkOutFromGit(branch, gitCredentialsId, repoUrl) {
        steps.git branch: "${branch}", credentialsId: "${gitCredentialsId}", url: "${repoUrl}"
    }
    // 编译&单元测试
    def packageAndJunit() {
        steps.sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package dependency:copy-dependencies -U -Pproduction -Dmaven.test.failure.ignore'
    }
    // 生成镜像
    def buildImage(registry, dockerCredential, imageName) {
        steps.docker.withRegistry("https://${registry}", "${dockerCredential}") {
            steps.docker.build("${imageName}", '.').push()
        }
    }
    // 依赖安全检查
    def dependencyCheck(scanPath) {
        steps.dependencyCheckAnalyzer datadir: '',
            hintsFile: '',
            includeCsvReports: false,
            includeHtmlReports: false,
            includeJsonReports: false,
            isAutoupdateDisabled: false,
            outdir: '',
            scanpath: "${scanPath}",
            skipOnScmChange: false,
            skipOnUpstreamChange: false,
            suppressionFile: '',
            zipExtensions: ''
        // 有高级别组件漏洞时，fail掉pipeline
        steps.dependencyCheckPublisher canComputeNew: false,
            defaultEncoding: '',
            failedTotalHigh: '0',
            healthy: '',
            pattern: '',
            unHealthy: '',
            unstableTotalHigh: '1',
            unstableTotalLow: '10',
            unstableTotalNormal: '5',
//            failedTotalHigh: '20',
            failedTotalLow: '100',
            failedTotalNormal: '50'
    }
}
