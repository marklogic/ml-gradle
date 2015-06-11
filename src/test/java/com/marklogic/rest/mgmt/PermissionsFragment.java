package com.marklogic.rest.mgmt;

import com.marklogic.junit.Fragment;

public class PermissionsFragment extends Fragment {

    public PermissionsFragment(Fragment other) {
        super(other);
    }

    public void assertPermissionExists(String roleName, String capability) {
        assertElementExists(format("/node()/sec:permission[sec:role-name = '%s' and sec:capability = '%s']", roleName,
                capability));
    }
}
