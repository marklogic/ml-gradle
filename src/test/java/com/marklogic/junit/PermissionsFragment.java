package com.marklogic.junit;

/**
 * Extends Fragment to provide MarkLogic permission-specific methods for assertions.
 */
public class PermissionsFragment extends Fragment {

	public PermissionsFragment(Fragment other) {
		super(other);
	}

	public void assertPermissionExists(String roleName, String capability) {
		assertElementExists(format("/node()/sec:permission[sec:role-name = '%s' and sec:capability = '%s']", roleName,
			capability));
	}

	public void assertPermissionCount(int count) {
		String xpath = "/node()/sec:permission[%d]";
		assertElementExists(format(xpath, count));
		assertElementMissing(format("Only expected %d permissions", count), format(xpath, count + 1));
	}
}
