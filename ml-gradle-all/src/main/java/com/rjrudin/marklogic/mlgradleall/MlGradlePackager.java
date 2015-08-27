package com.rjrudin.marklogic.mlgradleall;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Namespace;
import org.springframework.util.FileCopyUtils;

import com.rjrudin.marklogic.junit.Fragment;

public class MlGradlePackager {

    private static File baseDir = new File("../../gh-pages-marklogic-java/releases/com/rjrudin");
    private static File destDir = new File("build/ml-gradle-all/com/rjrudin");

    public static void main(String[] args) throws Exception {
        if (destDir.exists()) {
            destDir.delete();
        }
        destDir.mkdirs();
        processDependency("ml-gradle", "2.0b5");
        processDependency("ml-junit", "2.2.2");
    }

    private static void processDependency(String name, String version) throws IOException {
        File inDir = new File(baseDir, name + File.separator + version);
        if (inDir.exists()) {
            System.out.println("Processing " + name + ":" + version);
            File outDir = new File(destDir, name + File.separator + version);
            outDir.mkdirs();
            for (File f : inDir.listFiles()) {
                if (f.isFile()) {
                    FileCopyUtils.copy(f, new File(outDir, f.getName()));
                }
            }

            // Now read the pom file
            List<String> list = findMlDependencies(new File(inDir, name + "-" + version + ".pom"));
            for (int i = 0; i < list.size(); i += 2) {
                processDependency(list.get(i), list.get(i + 1));
            }
            System.out.println(list);
        } else {
            System.out.println("Directory doesn't exist, so not processing: " + inDir.getAbsolutePath());
        }
    }

    private static List<String> findMlDependencies(File f) throws IOException {
        List<String> list = new ArrayList<String>();
        if (f.exists()) {
            Namespace n = Namespace.getNamespace("p", "http://maven.apache.org/POM/4.0.0");
            Fragment xml = new Fragment(FileCopyUtils.copyToString(new FileReader(f)), n);
            for (Fragment el : xml.getFragments("//p:dependency[p:groupId = 'com.rjrudin']")) {
                list.add(el.getElementValue("/node()/p:artifactId"));
                list.add(el.getElementValue("/node()/p:version"));
                // list.add(el.getChildText("artifactId", n));
                // list.add(el.getChildText("version", n));
            }
        }
        return list;
    }
}
