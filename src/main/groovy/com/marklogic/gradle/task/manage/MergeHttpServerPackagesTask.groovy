package com.marklogic.gradle.task.manage

import org.gradle.api.tasks.TaskAction
import org.springframework.util.FileCopyUtils

import com.marklogic.manage.pkg.servers.HttpServerPackageMerger

class MergeHttpServerPackagesTask extends ManageTask {

    List<String> mergePackageFilePaths
    String outputPath = "build/ml-gradle/merged-http-server-package.xml"

    @TaskAction
    void mergeHttpServerPackages() {
        if (!mergePackageFilePaths) {
            println "No mergePackageFilePaths specified, will not produce a new HTTP server package file"
            return
        }

        File outputFile = new File(outputPath)
        if (outputFile.exists()) {
            println "Deleting existing output file: " + outputFile.getAbsolutePath()
            outputFile.delete()
        }

        println "Merging HTTP server package files: " + mergePackageFilePaths
        String xml = new HttpServerPackageMerger().mergeHttpServerPackages(mergePackageFilePaths)
        println "Writing merged HTTP server package to " + outputFile.getAbsolutePath() + "\n"
        File dir = outputFile.getParentFile()
        if (dir != null) {
            dir.mkdirs()
        }
        FileCopyUtils.copy(xml,  new FileWriter(outputFile))
        getManageConfig().setHttpServerFilePath(outputPath)
    }
}
