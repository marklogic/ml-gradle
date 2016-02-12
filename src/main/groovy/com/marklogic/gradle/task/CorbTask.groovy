package com.marklogic.gradle.task

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig

class CorbTask extends JavaExec {

  // prefix for corb project properties, to ensure no conflicts with other project properties
  private static final String CORB_PROPERTY_PREFIX = "corb"

    /*
    * TODO: replace hard-coded CSV of options with a reference to the CoRB Options class,
    *       once the next version of CoRB2 is released:
    *
      Map corbOptions = com.marklogic.developer.corb.Options.class.declaredFields.findAll { !it.synthetic }.collectEntries {
        //Create a 'corb' prefixed camelCased value (i.e. URIS-FILE => corbUrisFile )
        String gradleProperty = 'corb' + it.toLowerCase().split('_|-').collect { it.capitalize() }.join('')
        // create Map entry
        [(gradleProperty): it]
       }
    */
    Map corbOptions = "BATCH-SIZE,BATCH-URI-DELIM,COLLECTION-NAME,DECRYPTER,\
ERROR-FILE-NAME,EXIT-CODE-NO-URIS,EXPORT_FILE_AS_ZIP,\
EXPORT-FILE-BOTTOM-CONTENT,EXPORT-FILE-DIR,EXPORT-FILE-HEADER-LINE-COUNT,\
EXPORT-FILE-NAME,EXPORT-FILE-PART-EXT,\
EXPORT-FILE-SORT,EXPORT-FILE-SORT-COMPARATOR,\
EXPORT-FILE-TOP-CONTENT,EXPORT-FILE-URI-TO-PATH,\
FAIL-ON-ERROR,INSTALL,INIT-MODULE,INIT-TASK,JASYPT-PROPERTIES-FILE,\
MAX_OPTS_FROM_MODULE,MODULES-DATABASE,MODULE-ROOT,OPTIONS-FILE,\
POST-BATCH-MODULE,POST-BATCH-TASK,POST-BATCH-XQUERY-MODULE,\
PRE-BATCH-MODULE,PRE-BATCH-TASK,PRE-BATCH-XQUERY-MODULE,\
PRIVATE-KEY-ALGORITHM,PRIVATE-KEY-FILE,\
PROCESS-MODULE,PROCESS-TASK,\
QUERY-RETRY-LIMIT,QUERY-RETRY-INTERVAL,\
SSL-CIPHER-SUITES,SSL-CONFIG-CLASS,SSL-ENABLED-PROTOCOLS,SSL-KEY-PASSWORD,\
SSL-KEYSTORE,SSL-KEYSTORE-PASSWORD,SSL-KEYSTORE-TYPE,SSL-PROPERTIES-FILE,\
THREAD-COUNT,URIS_BATCH_REF,URIS-FILE,URIS-LOADER,URIS-MODULE,URIS-REPLACE-PATTERN,\
XCC_CONNECTION_RETRY_LIMIT,XCC-CONNECTION-RETRY-INTERVAL,XCC-CONNECTION-URI,\
XCC-DBNAME,XCC-HOSTNAME,XCC-PASSWORD,XCC-PORT,XCC-USERNAME,\
XQUERY_MODULE"
.tokenize(',').collectEntries {
        //Create a 'corb' prefixed camelCased value (i.e. URIS-FILE => corbUrisFile )
        String gradleProperty = CORB_PROPERTY_PREFIX + it.toLowerCase().split('_|-').collect { it.capitalize() }.join('')
        // create Map entry gradle property and original values, for easy lookup/translation
        [(gradleProperty): it]
    }

    String xccUrl //same as CoRB option: xccConnectionUri
    // It's common practice for the uris/transform modules to have the same prefix, so just set this if that's the
    // case - e.g. convert-uris.xqy and convert-transform.xqy
    String modulePrefix
    // Otherwise, set processModule (or transformModule) and either (urisModule or urisFile)
    String transformModule //same as CoRB option: processModule


    //These fields correspond directly to the CoRB2 options in the comments
    String batchSize // BATCH-SIZE
    String batchUriDelim // BATCH-URI-DELIM
    String collectionName // COLLECTION-NAME
    String decrypter // DECRYPTER
    String errorFileName //ERROR-FILE-NAME
    String exitCodeNoUris // EXIT-CODE-NO-URIS
    def exportFileAsZip // EXPORT_FILE_AS_ZIP
    String exportFileBottomContent // EXPORT-FILE-BOTTOM-CONTENT
    String exportFileDir // EXPORT-FILE-DIR
    String exportFileHeaderLineCount // EXPORT-FILE-HEADER-LINE-COUNT
    String exportFileName // EXPORT-FILE-NAME
    String exportFilePartExt // EXPORT-FILE-PART-EXT
    String exportFileSort // EXPORT-FILE-SORT
    String exportFileSortComparator // EXPORT-FILE-SORT-COMARATOR
    String exportFileTopContent // EXPORT-FILE-TOP-CONTENT
    String exportFileUriToPath // EXPORT-FILE-URI-TO-PATH
    def failOnError // FAIL-ON-ERROR
    def install = false // INSTALL
    String initModule // INIT-MODULE
    String initTask // INIT-TASK
    String jasyptPropertiesFile // JASYPT-PROPERTIES-FILE
    def maxOptsFromModule // MAX_OPTS_FROM_MODULE
    String modulesDatabase // MODULES-DATABASE
    String moduleRoot = "/"  // MODULE-ROOT
    String optionsFile // OPTIONS-FILE
    String postBatchModule // POST-BATCH-MODULE
    String postBatchTask // POST-BATCH-TASK
    String preBatchModule // PRE-BATCH-MODULE
    String preBatchTask // PRE-BATCH-TASK
    String privateKeyAlgorithm // PRIVATE-KEY-ALGORITHM
    String privateKeyFile // PRIVATE-KEY-FILE
    String processModule // PROCESS-MODULE
    String processTask // PROCESS-TASK
    def queryRetryLimit // QUERY-RETRY-LIMIT
    def queryRetryInterval // QUERY-RETRY-INTERVAL
    String sslCipherSuites // SSL-CIPHER-SUITES
    String sslConfigClass // SSL-CONFIG-CLASS
    String sslEnabledProtocos // SSL-ENABLED-PROTOCOLS
    String sslKeystore // SSL-KEYSTORE
    String sslKeyPassword // SSL-KEY-PASSWORD
    String sslKeystorePassword // SSL-KEYSTORE-PASSWORD
    String sslKeystoreType // SSL-KEYSTORE-TYPE
    String sslPropertiesFile // SSL-PROPERTIES-FILE
    // corb defaults to 1, but 8 seems like a more common default
    def threadCount = 8 // THREAD-COUNT
    String urisBatchRef // URIS_BATCH_REF
    String urisFile // URIS-FILE
    String urisLoader // URIS-LOADER
    String urisModule // URIS-MODULE
    String urisReplacePattern // URIS-REPLACE-PATTERN
    def xccConnectionRetryLimit // XCC-CONNECTION-RETRY-LIMIT
    def xccConnectionRetryInterval // XCC-CONNECTION-RETRY-INTERVAL
    String xccConnectionUri // XCC-CONNECTION-URI
    String xccDbname // XCC-DBNAME
    String xccHostname // XCC-HOSTNAME
    String xccPassword // XCC-PASSWORD
    def xccPort // XCC-PORT
    String xccUsername // XCC-USERNAME

    @TaskAction
    @Override
    public void exec() {
      setClasspath(getProject().configurations.corb)
      setMain("com.marklogic.developer.corb.Manager")

      Map options = buildCorbOptions()
      //CoRB2 will evaluate System properties for options
      systemProperties(options)

      println "super.exec() ${getSystemProperties()}"
      //super.exec()
    }

  /**
  * Construct CoRB2 options from the following sources:
  * task variables - lowerCamelCase names that correspond to their CoRB2
  *                  option (i.e. optionsFile => OPTIONS-FILE)
  * project properties - Project properties with the naming convention
  *                      of a 'corb' prefix and CamelCased CoRB2 option name
  *                      (i.e. corbOptionsFile => OPTIONS-FILE)
  * System properties - Any System property with a CoRB2 option name
  *
  * If properties are defined in more than one place, System properties will take
  * precedence over Project properties, which take precedence over task member variables.
  *
  * @return Map of CoRB2 options
  */
  public Map buildCorbOptions() {
    //first, convert legacy task properties and generate options from conventions
    Map options = collectNormalizedOptions()
    //collect all of the corb task options (i.e. threadCount=12)
    options << collectMemberVariables()
    //apply any corb project properties (i.e. -PcorbThreadCount=12)
    options << collectCorbProjectProperties()
    //apply any CoRB2 System properties (i.e. -DTHREAD-COUNT=12)
    options << collectSystemProperties()
    options //return the merged options
  }

  /**
  * Normalize corb task properties
  * @return Map of CoRB2 options
  */
  public Map collectNormalizedOptions() {
    Map options = [:]

    if (xccUrl) {
      options['XCC-CONNECTION-URI'] = xccUrl
    }

    if (transformModule) {
      options['PROCESS-MODULE'] = transformModule
    }

    //if modulePrefix is specified, then generate the selector and transform filenames
    if (modulePrefix) {
      options['URIS-MODULE'] = modulePrefix + "-uris.xqy"
      options['PROCESS-MODULE'] = modulePrefix + "-transform.xqy"
    }

    String modulesDatabaseName = getProject().property("mlAppConfig").getModulesDatabaseName()
    if (modulesDatabaseName) {
      options['MODULES-DATABASE'] = modulesDatabaseName
    }
    options //return any options constructed from CorbTask conventions
  }

  /**
  * Inspect the Task member variables and convert into CoRB2 options
  * @return Map of CoRB2 options
  */
  public Map collectMemberVariables() {
    //look for member variables where name match corbOption naming conventions
    this.class.declaredFields.collectEntries {
      String name = it.name.replace("_","") //normalize removing leading/trailing "_"
      String corbOptionKey = CORB_PROPERTY_PREFIX + name.capitalize()
      //evaluate whether a value is set and the name matches the corbOptions key pattern
      if (this[it.name] && corbOptions[corbOptionKey]) {
        [(corbOptions[corbOptionKey]): this[name] ]
      } else {
        [:]
      }
    }
  }

  /**
  * Find all CoRB2 System.properties
  * @return Map of CoRB2 options
  */
  public Map collectSystemProperties() {
    System.properties.findAll { corbOptions.containsValue(it.key) }
  }

  /**
  * For each of project properties specified with the convention corbXxxYyy,
  * construct a CoRB2 option
  * @return Map of CoRB2 options
  */
  public Map collectCorbProjectProperties() {
    project.properties.keySet().intersect(corbOptions.keySet()).collectEntries {
      [(corbOptions[it]): project[it]]
    }
  }

}
