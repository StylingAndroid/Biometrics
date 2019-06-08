node {
  stage('Checkout') {
        checkout scm
  }

  stage('Build') {
        sh "./gradlew clean assemble"
  }

  stage('Check') {
        sh "./gradlew detekt check ktlintCheck"
  }

  stage('Report') {
        scanForIssues tool: androidLintParser(pattern: '**/lint-results.xml', reportEncoding: 'UTF-8')
        scanForIssues tool: detekt(pattern: '**/reports/**/detekt.xml', reportEncoding: 'UTF-8')
        scanForIssues tool: ktLint(pattern: '**/reports/**/ktlint*.xml', reportEncoding: 'UTF-8')
  }
}
