package com.marklogic.appdeployer.cpf;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import com.marklogic.appdeployer.AbstractAppDeployerTest;
import com.marklogic.rest.mgmt.cpf.CpfManager;

public class ManageCpfTest extends AbstractAppDeployerTest {

    @Test
    @Ignore("Need to be able to create a triggers database and a domain first")
    public void test() throws Exception {
        CpfManager mgr = new CpfManager(manageClient);

        String json = new String(FileCopyUtils.copyToByteArray(new ClassPathResource(
                "sample-app/src/main/ml-config/cpf/cpf-configs/my-cpf-config.json").getInputStream()));

        mgr.createCpfConfig("Triggers", json);
    }

}
