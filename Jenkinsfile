@Library('shared-libraries') _
pipeline{
  agent {label 'devExpLinuxPool'}
  options {
    checkoutToSubdirectory 'ml-gradle'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '')
  }
  environment{
    JAVA_HOME_DIR="/home/builder/java/jdk-11.0.2"
    GRADLE_DIR   =".gradle"
    DMC_USER     = credentials('MLBUILD_USER')
    DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
  }
  stages{
    stage('tests'){
      steps{
        copyRPM 'Release','10.0-9.4'
        setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
        sh label:'test', script: '''#!/bin/bash
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd ml-gradle
          ./gradlew test  || true
        '''
        junit '**/build/**/*.xml'
      }
    }
    stage('publish'){
			when {
      	branch 'dev'
      }
      steps{
      	sh label:'publish', script: '''#!/bin/bash
        	export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cp ~/.gradle/gradle.properties $GRADLE_USER_HOME;
          cd ml-gradle
           ./gradlew publish
        '''
      }
    }
  }
}
