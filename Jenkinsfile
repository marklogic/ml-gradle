@Library('shared-libraries') _

def getJavaHomePath() {
	if (env.JAVA_VERSION == "JAVA25") {
		return "/home/builder/java/jdk-25.0.1"
	} else if (env.JAVA_VERSION == "JAVA21") {
		return "/home/builder/java/jdk-21.0.1"
	} else {
		return "/home/builder/java/jdk-17.0.2"
	}
}

def setupDockerMarkLogic(String image) {
	sh label:'mlsetup', script: '''#!/bin/bash
	echo "Removing any running MarkLogic server and clean up MarkLogic data directory"
	sudo /usr/local/sbin/mladmin remove
	sudo /usr/local/sbin/mladmin cleandata
	cd ml-gradle
	docker-compose down -v || true
	docker volume prune -f
	echo "Using image: "'''+image+'''
	docker pull '''+image+'''
	MARKLOGIC_IMAGE='''+image+''' MARKLOGIC_LOGS_PATH=/tmp/marklogic/logs docker compose up -d --build
	echo "Waiting for MarkLogic server to initialize."
	sleep 60s
  '''
}

def tearDownDocker() {
	sh label:'tearDownDocker', script: '''#!/bin/bash
		cd ml-gradle
    docker compose down -v || true
		docker volume prune -f
		sudo rm -rf /tmp/marklogic/logs
	'''
}

pipeline {
  agent {label 'devExpLinuxPool'}

  parameters {
    booleanParam(name: 'ONLY_PUBLISH', defaultValue: false, description: 'Skip tests and only run publish stage')
    string(name: 'JAVA_VERSION', defaultValue: 'JAVA17', description: 'Either JAVA17, JAVA21, or JAVA25')
  }

  options {
    checkoutToSubdirectory 'ml-gradle'
    buildDiscarder logRotator(artifactDaysToKeepStr: '7', artifactNumToKeepStr: '', daysToKeepStr: '7', numToKeepStr: '5')
  }

  environment {
    JAVA_HOME_DIR = getJavaHomePath()
    GRADLE_DIR    = ".gradle"
    DMC_USER      = credentials('MLBUILD_USER')
    DMC_PASSWORD  = credentials('MLBUILD_PASSWORD')
  }

  stages {

    stage('tests') {
      when {
        expression { params.ONLY_PUBLISH == false }
      }
      steps {
      	cleanupDocker()
      	setupDockerMarkLogic("ml-docker-db-dev-tierpoint.bed-artifactory.bedford.progress.com/marklogic/marklogic-server-ubi-rootless:latest-12")
        sh label:'test', script: '''#!/bin/bash
        	# 'set -e' causes the script to fail if any command fails.
        	set -e
          export JAVA_HOME=$JAVA_HOME_DIR
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH
          cd ml-gradle
          cd test-app
          ../gradlew -i mlDeploy
          cd ..
          # Running these separately to decrease likelihood of connection resets.
          ./gradlew ml-javaclient-util:test || true
					./gradlew ml-app-deployer:test || true
					./gradlew ml-gradle:test || true
        '''
        junit '**/build/**/*.xml'
      }
      post {
				always {
					tearDownDocker()
					cleanupDocker()
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
        	export JAVA_HOME=$JAVA_HOME_DIR;
          export GRADLE_USER_HOME=$WORKSPACE/$GRADLE_DIR;
          export PATH=$GRADLE_USER_HOME:$JAVA_HOME/bin:$PATH;
          rm -rf $GRADLE_USER_HOME || true;
          cd ml-gradle;
          ./gradlew clean;
          cp ~/.gradle/gradle.properties $GRADLE_USER_HOME/;
          ./gradlew publish
        '''
      }
    }
  }
}
