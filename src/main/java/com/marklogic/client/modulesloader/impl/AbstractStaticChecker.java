package com.marklogic.client.modulesloader.impl;

import com.marklogic.client.helper.LoggingObject;

import java.util.List;

/**
 * Has all the guts for doing static checking, but delegates the execution of an XQuery script to a subclass.
 */
public abstract class AbstractStaticChecker extends LoggingObject implements StaticChecker {

	private boolean checkLibraryModules = false;
	private boolean bulkCheck = false;

	@Override
	public void checkLoadedAssets(List<LoadedAsset> assets) {
		if (bulkCheck) {
			performBulkStaticCheck(assets);
		} else {
			for (LoadedAsset asset : assets) {
				if (asset.isCanBeStaticallyChecked()) {
					staticallyCheckModule(asset.getUri());
				}
			}
		}
	}

	protected abstract void executeQuery(String xquery);

	/**
	 * Statically checks the module at the given URI. Includes support for evaluating a library module by trying to
	 * extract its namespace and then using xdmp:eval to evaluate a module that imports the library module. If this
	 * fails to extract its namespace, an error will be reported just like if the module itself has an error in it.
	 *
	 * @param uri
	 */
	protected void staticallyCheckModule(String uri) {
		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Performing static check on module at URI: " + uri);
			}
			String xquery = "let $uri := '" + uri + "' return " + buildXqueryForStaticallyCheckingModule();
			executeQuery(xquery);
		} catch (Exception re) {
			String message = "Static check failed for module at URI: " + uri + "; cause: " + re.getMessage();
			throw new RuntimeException(message, re);
		}
	}

	protected void performBulkStaticCheck(List<LoadedAsset> assets) {
		String xquery = "let $uris := (";
		for (LoadedAsset asset : assets) {
			if (asset.isCanBeStaticallyChecked()) {
				String uri = asset.getUri();
				if (!xquery.endsWith("(")) {
					xquery += ",";
				}
				xquery += "'" + uri + "'";
			}
		}
		xquery += ") for $uri in $uris return " + buildXqueryForStaticallyCheckingModule();

		try {
			if (logger.isDebugEnabled()) {
				logger.debug("Static checking all loaded modules");
			}
			executeQuery(xquery);
			if (logger.isDebugEnabled()) {
				logger.debug("Finished static checking all loaded modules");
			}
		} catch (Exception re) {
			String message = "Bulk static check failure, cause: " + re.getMessage();
			throw new RuntimeException(message, re);
		}
	}


	/**
	 * Assumes that there's already a variable in XQuery named "uri" in scope. If the module is a library module, an
	 * attempt is made to extract its namespace and import it in a statement passed to xdmp:eval. If an error occurs
	 * in construct that statement, it cannot be distinguished from an error in the actual module. To turn this behavior
	 * off, set "staticCheckLibraryModules" to false.
	 *
	 * @return
	 */
	protected String buildXqueryForStaticallyCheckingModule() {
		String xquery =
			"try { xdmp:invoke($uri, (), <options xmlns='xdmp:eval'><static-check>true</static-check></options>) } " +
				"catch ($e) { " +
				"if ($e/*:code = 'XDMP-NOEXECUTE') then () " +
				"else if ($e/*:code = 'XDMP-EVALLIBMOD') then ";
		if (checkLibraryModules) {
			xquery +=
				"  let $doc := xdmp:eval('declare variable $URI external; fn:doc($URI)', (xs:QName('URI'), $uri), <options xmlns='xdmp:eval'><database>{xdmp:modules-database()}</database></options>) " +
					"  let $line := fn:tokenize($doc, '\n')[fn:contains(., 'module namespace')][1] " +
					"  let $ns := fn:tokenize($line, '=')[2] " +
					"  let $ns := fn:replace($ns, ';', '') " +
					"  let $ns := fn:replace($ns, \"'\", \"\") " +
					"  let $ns := fn:normalize-space(fn:replace($ns, '\"', '')) " +
					"  let $xquery := fn:concat('import module namespace ns = \"', $ns, '\" at \"', $uri, '\"; ()')" +
					"  return xdmp:eval($xquery, (), <options xmlns='xdmp:eval'><static-check>true</static-check></options>) ";
		} else {
			xquery += " () ";
		}
		return xquery + " else xdmp:rethrow() }";
	}

	public void setCheckLibraryModules(boolean checkLibraryModules) {
		this.checkLibraryModules = checkLibraryModules;
	}

	public void setBulkCheck(boolean bulkCheck) {
		this.bulkCheck = bulkCheck;
	}
}
