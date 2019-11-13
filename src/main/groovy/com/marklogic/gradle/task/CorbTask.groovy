package com.marklogic.gradle.task

import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.TaskAction

import com.marklogic.appdeployer.AppConfig

class CorbTask extends JavaExec {

  def corbOptions = [:]

  //default CORB option list
  private static String[] DEFAULT_CORB_OPTIONS = "BATCH-SIZE,BATCH-URI-DELIM,\
COLLECTION-NAME,COMMAND,COMMAND-FILE,COMMAND-FILE-POLL-INTERVAL,CONNECTION_POLICY,CONTENT_SOURCE_POOL,\
DECRYPTER,DISK-QUEUE,DISK-QUEUE-MAX-IN-MEMORY-SIZE,DISK-QUEUE-TEMP-DIR,\
ERROR-FILE-NAME,EXIT-CODE-NO-URIS,EXPORT_FILE_AS_ZIP,\
EXPORT-FILE-BOTTOM-CONTENT,EXPORT-FILE-DIR,EXPORT-FILE-HEADER-LINE-COUNT,\
EXPORT-FILE-NAME,EXPORT-FILE-PART-EXT,\
EXPORT-FILE-SORT,EXPORT-FILE-SORT-COMPARATOR,\
EXPORT-FILE-TOP-CONTENT,EXPORT-FILE-URI-TO-PATH,\
FAIL-ON-ERROR,INSTALL,INIT-MODULE,INIT-TASK,JASYPT-PROPERTIES-FILE,JOB_NAME,JOB_SERVER_PORT,\
LOADER_BASE64_ENCODE,LOADER_PATH,LOADER_SET_URIS_BATCH_REF,LOADER_USE_ENVELOPE,LOADER_VARIABLE,\
MAX_OPTS_FROM_MODULE,METADATA,METADATA_TO_PROCESS_MODULE,\
METRICS_COLLECTIONS,METRICS_DATABASE,METRICS_LOG_LEVEL,METRICS_MODULE,\
METRICS_NUM_FAILED_TRANSACTIONS,METRICS_NUM_SLOW_TRANSACTIONS,METRICS_ROOT,METRICS_SYNC_FREQUENCY,\
ML_LOG_LEVELS,MODULES-DATABASE,MODULE-ROOT,\
NUM-TPS-FOR-ETC,OPTIONS-FILE,\
POST_BATCH_MINIMUM_COUNT,POST-BATCH-MODULE,POST-BATCH-TASK,POST-BATCH-XQUERY-MODULE,\
PRE_BATCH_MINIMUM_COUNT,PRE-BATCH-MODULE,PRE-BATCH-TASK,PRE-BATCH-XQUERY-MODULE,PRE_POST_BATCH_ALWAYS_EXECUTE,\
PRIVATE-KEY-ALGORITHM,PRIVATE-KEY-FILE,\
PROCESS-MODULE,PROCESS-TASK,\
QUERY-RETRY-LIMIT,QUERY-RETRY-INTERVAL,QUERY-RETRY-ERROR-CODES,QUERY-RETRY-ERROR-MESSAGE\
SSL-CIPHER-SUITES,SSL-CONFIG-CLASS,SSL-ENABLED-PROTOCOLS,SSL-KEY-PASSWORD,\
SSL-KEYSTORE,SSL-KEYSTORE-PASSWORD,SSL-KEYSTORE-TYPE,SSL-PROPERTIES-FILE,\
TEMP_DIR,THREAD-COUNT,\
URIS_BATCH_REF,URIS-FILE,URIS-LOADER,URIS-MODULE,URIS_REDACTED,URIS-REPLACE-PATTERN,URIS_TOTAL_COUNT,\
XCC_CONNECTION_RETRY_LIMIT,XCC-CONNECTION-RETRY-INTERVAL,XCC_CONNECTION_HOST_RETRY_LIMIT,XCC-CONNECTION-URI,\
XCC-DBNAME,XCC-HOSTNAME,XCC_HTTPCOMPLIANT,XCC-PASSWORD,XCC-PORT,XCC_PROTOCOL,XCC_TIME_ZONE,XCC-USERNAME,\
XML-FILE,XML_METADATA,XML-NODE,XML_SCHEMA,XML_TEMP_DIR,ZIP_FILE\
XQUERY-MODULE".tokenize(',')

  CorbTask() {
    String[] optionNames = DEFAULT_CORB_OPTIONS
    // obtain the options from CoRB Options class, to ensure that the latest available are applied to this task
    try {
        optionNames = Class.forName("com.marklogic.developer.corb.Options", true, Thread.currentThread().contextClassLoader)
        .declaredFields
        .findAll { !it.synthetic }.collect { it.get(null) }
    } catch (ClassNotFoundException ex) {}

    //Augment with member variables and a mapping of CoRB2 Options
    corbOptions = optionNames.collectEntries { option ->

        String camelOption = option.toLowerCase().split('_|-').collect { it.capitalize() }.join('')
        // create Map entry gradle property and original values, for easy lookup/translation
        String lowerCamelOption = new StringBuffer(camelOption.length())
                                    .append(Character.toLowerCase(camelOption.charAt(0)))
                                    .append(camelOption.substring(1))
                                    .toString();

        //add the lowerCamelCased CoRB2 option as a member variable
        this.metaClass[lowerCamelOption] = null

        // Create a 'corb' prefixed CamelCased entry (i.e. URIS-FILE => corbUrisFile )
        // mapped to the original CoRB2 option for lookup/conversion
        [(CORB_PROPERTY_PREFIX + camelOption): option]
    }

    //ensure that CORB task is always executed
    outputs.upToDateWhen { false }

  }

  // prefix for corb project properties, to ensure no conflicts with other project properties
  private static final String CORB_PROPERTY_PREFIX = "corb"

  String xccUrl //same as xccConnectionUri
  // It's common practice for the uris/transform modules to have the same prefix, so just set this if that's the
  // case - e.g. convert-uris.xqy and convert-transform.xqy
  String modulePrefix
  // Otherwise, set processModule (formerly transformModule) and urisModule
  String transformModule //same as processModule

  def install = false // INSTALL

  String moduleRoot = "/"  // MODULE-ROOT

  // corb defaults to 1, but 8 seems like a more common default
  def threadCount = 8 // THREAD-COUNT

  @TaskAction
  @Override
  public void exec() {
    //By convention, if there is a corb configuration, use it to set the classpath
    if (getProject().configurations.findByName('corb')) {
      setClasspath(getProject().configurations.corb)
    }
    setMain("com.marklogic.developer.corb.Manager")

    Map options = buildCorbOptions()
    //CoRB2 will evaluate System properties for options
    systemProperties(options)

    super.exec()
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
    this.metaClass.getProperties().collectEntries {
      String corbOptionKey = CORB_PROPERTY_PREFIX + it.name.capitalize()
      //evaluate whether a value is set and the name matches the corbOptions key pattern
	  if (corbOptions[corbOptionKey] && this[it.name]) {
	    [(corbOptions[corbOptionKey]): this[it.name] ]
	  } else {
	    [:]
	  }
    }
  }

  /**
  * Collect all System.properties. This allows for any CoRB option to be set, including those not statically known such
  * as CoRB custom inputs (e.g. URIS-MODULE.foo, PROCESS-MODULE.bar, etc) as well as settings for other libraries, such
  * as xcc.httpCompliant to enable XCCS compatability for XCC.
  * @return all System.properties
  */
  public Map collectSystemProperties() {
    System.properties
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
