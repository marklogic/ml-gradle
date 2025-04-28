@Library('shared-libraries') _

def runTests(String mlVersion, String javaVersion){
  copyRPM 'Release',mlVersion
  setUpML '$WORKSPACE/xdmp/src/Mark*.rpm'
  sh label:'test', script: '''#!/bin/bash
    export JAVA_HOME=$'''+javaVersion+'''
    export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
    export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
    cd $WORKSPACE/ml-gradle/ml-javaclient-util-test-app
    echo "mlPassword=admin" > gradle-local.properties
    ../gradlew -i :mlDeploy
    cd $WORKSPACE/ml-gradle/
    ./gradlew ml-javaclient-util:test || true
    ./gradlew ml-app-deployer:test || true
    ./gradlew ml-gradle:test || true
  '''
  junit '**/build/**/*.xml'
}

pipeline{
  agent none
  options {
    checkoutToSubdirectory 'ml-gradle'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '30', numToKeepStr: '')
  }
  environment{
    JAVA11_HOME_DIR="/home/builder/java/jdk-11.0.20"
    JAVA8_HOME_DIR="/home/builder/java/openjdk-1.8.0-262"
    JAVA17_HOME_DIR="/home/builder/java/jdk-17.0.2"
    JAVA21_HOME_DIR="/home/builder/java/jdk-21.0.1"
    GRADLE_DIR   =".gradle"
    DMC_USER     = credentials('MLBUILD_USER')
    DMC_PASSWORD = credentials('MLBUILD_PASSWORD')
  }
  stages{
    stage('tests-java11'){
      agent {label 'devExpLinuxPool'}
      steps{
        runTests('11.3.1','JAVA11_HOME_DIR')
      }
    }
    stage('regressions'){
      when {
        anyOf {
          branch 'dev'
          branch 'master'
        }
        beforeAgent true
      }
      parallel{
        stage('tests-java8'){
          agent {label 'devExpLinuxPool'}
          steps{
            runTests('11.3.1','JAVA8_HOME_DIR')
          }
        }
        stage('tests-java17'){
          agent {label 'devExpLinuxPool'}
          steps{
            runTests('11.3.1','JAVA17_HOME_DIR')
          }
        }
        stage('tests-java21'){
          agent {label 'devExpLinuxPool'}
          steps{
            runTests('11.3.1','JAVA21_HOME_DIR')
          }
        }
      }
    }
    stage('publish'){
      agent {label 'devExpLinuxPool'}
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
