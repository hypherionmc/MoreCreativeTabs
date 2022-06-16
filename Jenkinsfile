pipeline {
    agent "master"
    tools {
        jdk "JAVA17"
    }
    stages {
        stage("Notify Discord") {
            steps {
                discordSend webhookURL: env.FDD_WH_ADMIN,
                        title: "Deploy Started: MoreCreativeTabs Deploy #${BUILD_NUMBER}",
                        link: env.BUILD_URL,
                        result: 'SUCCESS',
                        description: "Build: [${BUILD_NUMBER}](${env.BUILD_URL})"
            }
        }
        stage("Prepare") {
            steps {
                sh "wget -O changelog-forge.md https://raw.githubusercontent.com/hypherionmc/changelogs/main/mct/changelog-forge.md"
                sh "wget -O changelog-fabric.md https://raw.githubusercontent.com/hypherionmc/changelogs/main/mct/changelog-fabric.md"
                sh "chmod +x ./gradlew"
                sh "./gradlew clean"
            }
        }
        stage("Publish") {
            steps {
                sh "./gradlew modrinth curseforge -Prelease=true"
            }
        }
    }
    post {
        always {
            sh "./gradlew --stop"
            deleteDir()

            discordSend webhookURL: env.FDD_WH_ADMIN,
                    title: "MoreCreativeTabs Deploy #${BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    description: "Build: [${BUILD_NUMBER}](${env.BUILD_URL})\nStatus: ${currentBuild.currentResult}"
        }
    }
}
