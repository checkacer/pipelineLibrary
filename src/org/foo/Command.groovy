package org.foo
class Command implements Serializable {
    def steps
    Command(steps){
        this.steps = steps
    }
// 下载代码
    def checkOutFromGit(branch,credentialsId,repo) {
        steps.git branch: "${branch}", credentialsId: "${credentialsId}", url: "${repo}"
    }
// 编译&单元测试
    def packageAndJunit(){
        steps.sh 'mvn clean org.jacoco:jacoco-maven-plugin:prepare-agent package dependency:copy-dependencies -U -Pproduction -Dmaven.test.failure.ignore'
    }
// 代码审查
    def codeReview(sonarQubeScannerHome){
        steps.withSonarQubeEnv('SonarQube') {
            steps.sh "${sonarQubeScannerHome}/bin/sonar-scanner"
        }
    }
// 生成镜像
    def buildImage(repo,credential,imageName){
        steps.docker.withRegistry("https://${repo}","${credential}") {
            steps.docker.build("${imageName}",'.').push()
        }
    }
// 依赖安全检查
    def dependencyCheck(scanPath){
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
// 部署到k8s集群
    def applyK8s(yaml,tag){
        steps.sh "cat ${yaml} | sed 's/{tag}/${tag}/' | kubectl --kubeconfig /jenkins/.kube/config apply -f -"
    }
    def junit(url){
        steps.junit "${url}"
    }
    def apiTest(path,postmanCJ,postmanEJ,reporterEXml,reporterEHtml,reporterTHtml,reportName,reportTitles){
        try{
            steps.sh "export PATH=$path:/jenkins/tools/node-v8.11.2-linux-x64/bin/&&" +
                "newman run ${postmanCJ} " +
                "-e ${postmanEJ} " +
                "--reporters html,cli,junit " +
                "--reporter-junit-export ${reporterEXml} "+
                "--reporter-html-export ${reporterEHtml} " +
                "--reporter-html-template ${reporterTHtml}"
        } catch (e) {
            steps.echo 'API测试出现错误' + e.toString()
        }
        steps.publishHTML([allowMissing: false,
                     alwaysLinkToLastBuild: true,
                     keepAll: false,
                     reportDir: './',
                     reportFiles: 'reporter.html',
                     reportName: "${reportName}",
                     reportTitles: "${reportTitles}"])
    }
    def echo(){
        steps.echo 'class echo ...'
    }
}