pipeline {
    agent any
    tools {
        jdk "JAVA17"
    }
    stages {
        stage("Notify Discord") {
            discordSend webhookUrl: env.FDD_WH_ADMIN,
                    title: "MoreCreativeTabs Deploy #${BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: 'SUCCESS',
                    description: "Build: [${BUILD_NUMBER}](${env.BUILD_URL})"
        }
        stage("Prepare") {
            sh "wget -O changelog-forge.md https://raw.githubusercontent.com/hypherionmc/changelogs/main/mct/changelog-forge.md"
            sh "wget -O changelog-fabric.md https://raw.githubusercontent.com/hypherionmc/changelogs/main/mct/changelog-fabric.md"
            sh "chmod +x ./gradlew"
            sh "./gradlew clean"
        }
        stage("Publish") {
            sh "./gradlew modrinth curseforge -Prelease=true"
        }
    }
    post {
        always {
            sh "./gradlew --stop"
            deleteDir()

            discordSend webhookUrl: env.FDD_WH_ADMIN,
                    title: "MoreCreativeTabs Deploy #${BUILD_NUMBER}",
                    link: env.BUILD_URL,
                    result: currentBuild.currentResult,
                    description: "Build: [${BUILD_NUMBER}](${env.BUILD_URL})\nStatus: ${currentBuild.currentResult}"
        }
    }
}
