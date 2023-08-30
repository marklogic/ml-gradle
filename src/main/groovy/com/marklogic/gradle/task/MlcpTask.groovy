/*
 * Copyright (c) 2023 MarkLogic Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.marklogic.gradle.task

import com.marklogic.appdeployer.AppConfig
import com.marklogic.client.DatabaseClient
import com.marklogic.client.io.FileHandle
import com.marklogic.contentpump.bean.MlcpBean
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.*

/**
 * As of version 4.3.1, this no longer uses "@Delegate" and an instance of MlcpBean, which no longer works in Gradle 7.
 * See issue #595 for more details.
 *
 * Instead, this task now has each property that MlcpBean has. And when the task is run, each property is copied onto
 * an instance of MlcpBean, and the task otherwise functions the same as it used to. This of course makes little reuse
 * of MlcpBean, but it's not clear how to do that anymore in Gradle 7.
 *
 * Note that this defaults to using appConfig.restAdminUsername and appConfig.restAdminPassword. That user may not
 * have permission to perform the mlcp operation you wish to perform. In that case, just set the username/password
 * parameters of this task for the appropriate user.
 */
class MlcpTask extends JavaExec {

	@Input String command = "IMPORT"
	@Input @Optional String host
	@Input @Optional Integer port
	@Input @Optional String username
	@Input @Optional String password
	@Input @Optional String database
	@Input @Optional String input_host
	@Input @Optional Integer input_port
	@Input @Optional String input_username
	@Input @Optional String input_password
	@Input @Optional String input_database
	@Input @Optional String output_host
	@Input @Optional Integer output_port
	@Input @Optional String output_username
	@Input @Optional String output_password
	@Input @Optional String output_database
	@Input @Optional String aggregate_record_element
	@Input @Optional String aggregate_record_namespace
	@Input @Optional Boolean archive_metadata_optional
	@Input @Optional Integer batch_size
	@Input @Optional String collection_filter
	@Input @Optional Boolean compress
	@Input @Optional String conf
	@Input @Optional String content_encoding
	@Input @Optional String copy_collections
	@Input @Optional String copy_metadata
	@Input @Optional String copy_permissions
	@Input @Optional String copy_properties
	@Input @Optional String copy_quality
	@Input @Optional String data_type
	@Input @Optional String delimiter
	@Input @Optional String delimited_root_name
	@Input @Optional String directory_filter
	@Input @Optional String document_selector
	@Input @Optional String document_type
	@Input @Optional Boolean fastload
	@Input @Optional String filename_as_collection
	@Input @Optional Boolean generate_uri
	@Input @Optional String hadoop_conf_dir
	@Input @Optional Boolean indented
	@Input @Optional Boolean input_compressed
	@Input @Optional String input_compression_codec
	@Input @Optional String input_file_path
	@Input @Optional String input_file_pattern
	@Input @Optional String input_file_type
	@Input @Optional Boolean input_ssl
	@Input @Optional Integer max_split_size
	@Input @Optional Integer min_split_size
	@Input @Optional String mode
	@Input @Optional String modules
	@Input @Optional String modules_root
	@Input @Optional String namespace
	@Input @Optional String options_file
	@Input @Optional Boolean output_cleandir
	@Input @Optional String output_collections
	@Input @Optional String output_directory
	@Input @Optional String output_file_path
	@Input @Optional String output_graph
	@Input @Optional String output_language
	@Input @Optional String output_override_graph
	@Input @Optional String output_partition
	@Input @Optional String output_permissions
	@Input @Optional String output_quality
	@Input @Optional Boolean output_ssl
	@Input @Optional String output_type
	@Input @Optional String output_uri_prefix
	@Input @Optional String output_uri_replace
	@Input @Optional String output_uri_suffix
	@Input @Optional String path_namespace
	@Input @Optional String query_filter
	@Input @Optional String redaction
	@Input @Optional Boolean restrict_hosts
	@Input @Optional Boolean restrict_input_hosts
	@Input @Optional Boolean restrict_output_hosts
	@Input @Optional String sequencefile_key_class
	@Input @Optional String sequencefile_value_class
	@Input @Optional String sequencefile_value_type
	@Input @Optional Boolean snapshot
	@Input @Optional Boolean split_input
	@Input @Optional Boolean ssl
	@Input @Optional Boolean streaming
	@Input @Optional String temporal_collection
	@Input @Optional Integer thread_count
	@Input @Optional Integer thread_count_per_split
	@Input @Optional Boolean tolerate_errors
	@Input @Optional String transform_function
	@Input @Optional String transform_module
	@Input @Optional String transform_namespace
	@Input @Optional String transform_param
	@Input @Optional Integer transaction_size
	@Input @Optional String type_filter
	@Input @Optional String uri_id
	@Input @Optional String xml_repair_level

	// Set this to define a URI in your content database for mlcp output to be written to as a text document
	@Input @Optional String logOutputUri

	// Allow the user to provide a custom DatabaseClient for logging mlcp output
	@Input @Optional DatabaseClient logClient

	@Override @Internal
	Logger getLogger() {
		return Logging.getLogger(MlcpTask.class)
	}

	MlcpTask() {
		super.getMainClass().set("com.marklogic.contentpump.ContentPump")
	}

	@TaskAction
	@Override
	void exec() {
		AppConfig config = getProject().property("mlAppConfig")

		List<String> newArgs = new ArrayList<>()
		newArgs.add(command)

		MlcpBean mlcpBean = new MlcpBean()
		applyTaskPropertiesToMlcpBean(mlcpBean)
		var optionsFileFields = []
		if (options_file != null) {
			optionsFileFields = loadOptionsFileFields(options_file)
		}

		mlcpBean.properties.each { prop, val ->
			if ((["host", "username", "password"].contains(prop)) && optionsFileFields.contains(prop)) {
				return
			}
			def propVal
			if (val) {
				switch (prop) {
					case ["class", "logger", "command", "password"]:
						// skip, as these aren't MLCP key/value args, and we don't want password to be shown below
						return
					default:
						propVal = val
						break
				}

				newArgs.add("-" + prop);
				newArgs.add(String.valueOf(propVal));
			}
		}

		// Ensure connection arguments are present, but not if a COPY
		boolean isCopy = "COPY".equals(command)
		if (!isCopy) {
			addArgsFromAppConfig(newArgs, optionsFileFields, config)
		}

		// Include any args that a user has configured via the args parameter of the Gradle task
		newArgs.addAll(getArgs())

		// Build args to print, excluding the two known COPY arguments that will reveal passwords
		List<String> safeArgsToPrint = new ArrayList<>()
		for (int i = 0; i < newArgs.size(); i++) {
			String arg = newArgs.get(i)
			if ("-password".equals(arg) || "-input_password".equals(arg) || "-output_password".equals(arg)) {
				// Skip the next argument too
				i++
			} else {
				safeArgsToPrint.add(arg)
			}
		}
		println "mlcp arguments, excluding known password arguments: " + safeArgsToPrint

		setArgs(newArgs)

		File logOutputFile = null
		if (logOutputUri) {
			println "Will write mlcp log output to URI: " + logOutputUri
			logOutputFile = new File(getProject().getBuildDir(), "mlcp-log-output-" + System.currentTimeMillis() + ".txt")
			setStandardOutput(logOutputFile.newOutputStream())
		}

		super.exec()

		if (logOutputFile != null) {
			DatabaseClient databaseClient
			if (logClient != null) {
				databaseClient = logClient
			} else {
				databaseClient = project.property("mlAppConfig").newDatabaseClient()
			}
			databaseClient.newDocumentManager().write(logOutputUri, new FileHandle(logOutputFile))
			println "Wrote mlcp log output to URI: " + logOutputUri
		}
	}

	void addArgsFromAppConfig(List<String> newArgs, List<String> optionsFileFields, AppConfig config ) {
		if ((!optionsFileFields.contains("host")) && (!newArgs.contains("-host"))) {
			newArgs.add("-host")
			newArgs.add(config.getHost())
		}
		if (!newArgs.contains("-port")) {
			newArgs.add("-port")
			newArgs.add("8000")
		}
		if ((!optionsFileFields.contains("username")) && (!newArgs.contains("-username"))) {
			newArgs.add("-username")
			newArgs.add(config.getRestAdminUsername())
		}
		if (!optionsFileFields.contains("password")) {
			newArgs.add("-password")
			newArgs.add(password ? password : config.getRestAdminPassword())
		}
	}

	void applyTaskPropertiesToMlcpBean(MlcpBean mlcpBean) {
		mlcpBean.setHost(host)
		mlcpBean.setPort(port)
		mlcpBean.setUsername(username)
		mlcpBean.setPassword(password)
		mlcpBean.setDatabase(database)
		mlcpBean.setInput_host(input_host)
		mlcpBean.setInput_port(input_port)
		mlcpBean.setInput_username(input_username)
		mlcpBean.setInput_password(input_password)
		mlcpBean.setInput_database(input_database)
		mlcpBean.setOutput_host(output_host)
		mlcpBean.setOutput_port(output_port)
		mlcpBean.setOutput_username(output_username)
		mlcpBean.setOutput_password(output_password)
		mlcpBean.setOutput_database(output_database)
		mlcpBean.setAggregate_record_element(aggregate_record_element)
		mlcpBean.setAggregate_record_namespace(aggregate_record_namespace)
		mlcpBean.setArchive_metadata_optional(archive_metadata_optional)
		mlcpBean.setBatch_size(batch_size)
		mlcpBean.setCollection_filter(collection_filter)
		mlcpBean.setCompress(compress)
		mlcpBean.setConf(conf)
		mlcpBean.setContent_encoding(content_encoding)
		mlcpBean.setCopy_collections(copy_collections)
		mlcpBean.setCopy_metadata(copy_metadata)
		mlcpBean.setCopy_permissions(copy_permissions)
		mlcpBean.setCopy_properties(copy_properties)
		mlcpBean.setCopy_quality(copy_quality)
		mlcpBean.setData_type(data_type)
		mlcpBean.setDelimiter(delimiter)
		mlcpBean.setDelimited_root_name(delimited_root_name)
		mlcpBean.setDirectory_filter(directory_filter)
		mlcpBean.setDocument_selector(document_selector)
		mlcpBean.setDocument_type(document_type)
		mlcpBean.setFastload(fastload)
		mlcpBean.setFilename_as_collection(filename_as_collection)
		mlcpBean.setGenerate_uri(generate_uri)
		mlcpBean.setHadoop_conf_dir(hadoop_conf_dir)
		mlcpBean.setIndented(indented)
		mlcpBean.setInput_compressed(input_compressed)
		mlcpBean.setInput_compression_codec(input_compression_codec)
		mlcpBean.setInput_file_path(input_file_path)
		mlcpBean.setInput_file_pattern(input_file_pattern)
		mlcpBean.setInput_file_type(input_file_type)
		mlcpBean.setInput_ssl(input_ssl)
		mlcpBean.setMax_split_size(max_split_size)
		mlcpBean.setMin_split_size(min_split_size)
		mlcpBean.setMode(mode)
		mlcpBean.setModules(modules)
		mlcpBean.setModules_root(modules_root)
		mlcpBean.setNamespace(namespace)
		mlcpBean.setOptions_file(options_file)
		mlcpBean.setOutput_cleandir(output_cleandir)
		mlcpBean.setOutput_collections(output_collections)
		mlcpBean.setOutput_directory(output_directory)
		mlcpBean.setOutput_file_path(output_file_path)
		mlcpBean.setOutput_graph(output_graph)
		mlcpBean.setOutput_language(output_language)
		mlcpBean.setOutput_override_graph(output_override_graph)
		mlcpBean.setOutput_partition(output_partition)
		mlcpBean.setOutput_permissions(output_permissions)
		mlcpBean.setOutput_quality(output_quality)
		mlcpBean.setOutput_ssl(output_ssl)
		mlcpBean.setOutput_type(output_type)
		mlcpBean.setOutput_uri_prefix(output_uri_prefix)
		mlcpBean.setOutput_uri_replace(output_uri_replace)
		mlcpBean.setOutput_uri_suffix(output_uri_suffix)
		mlcpBean.setPath_namespace(path_namespace)
		mlcpBean.setQuery_filter(query_filter)
		mlcpBean.setRedaction(redaction)
		mlcpBean.setRestrict_hosts(restrict_hosts)
		mlcpBean.setRestrict_input_hosts(restrict_input_hosts)
		mlcpBean.setRestrict_output_hosts(restrict_output_hosts)
		mlcpBean.setSequencefile_key_class(sequencefile_key_class)
		mlcpBean.setSequencefile_value_class(sequencefile_value_class)
		mlcpBean.setSequencefile_value_type(sequencefile_value_type)
		mlcpBean.setSnapshot(snapshot)
		mlcpBean.setSplit_input(split_input)
		mlcpBean.setSsl(ssl)
		mlcpBean.setStreaming(streaming)
		mlcpBean.setTemporal_collection(temporal_collection)
		mlcpBean.setThread_count(thread_count)
		mlcpBean.setThread_count_per_split(thread_count_per_split)
		mlcpBean.setTolerate_errors(tolerate_errors)
		mlcpBean.setTransform_function(transform_function)
		mlcpBean.setTransform_module(transform_module)
		mlcpBean.setTransform_namespace(transform_namespace)
		mlcpBean.setTransform_param(transform_param)
		mlcpBean.setTransaction_size(transaction_size)
		mlcpBean.setType_filter(type_filter)
		mlcpBean.setUri_id(uri_id)
		mlcpBean.setXml_repair_level(xml_repair_level)
	}

	List<String> loadOptionsFileFields(options_file) {
		def optionsFileFields = []
		try {
			new File(options_file).eachLine { line ->
				if (line.startsWith("-")) {
					optionsFileFields.add(line.substring(1))
				}
			}
		} catch(Exception e) {
			getLogger().warn("Unable to read the specified options file; cause: " + e.getMessage() + "\nNot passing '-host', '-username', or '-password' to MLCP since they may exist in the options file.")
			optionsFileFields.add("host")
			optionsFileFields.add("username")
			optionsFileFields.add("password")
		}
		return optionsFileFields;
	}
}
