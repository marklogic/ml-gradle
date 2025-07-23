/*
 * Copyright (c) 2015-2025 Progress Software Corporation and/or its subsidiaries or affiliates. All Rights Reserved.
 */
package com.marklogic.contentpump.bean;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides a bean-like interface for using mlcp programmatically. Migrated to this project in the 6.0.0 release from
 * the https://github.com/marklogic-community/mlcp-util repository.
 */
public class MlcpBean {

	// import/export connection options
	private String host;
	private Integer port;
	private String username;
	private String password;
	private String database;

	// copy connection options
	private String input_host;
	private Integer input_port;
	private String input_username;
	private String input_password;
	private String input_database;
	private String output_host;
	private Integer output_port;
	private String output_username;
	private String output_password;
	private String output_database;

	private String command = "IMPORT";

	private String aggregate_record_element;
	private String aggregate_record_namespace;
	private Boolean archive_metadata_optional;
	private Integer batch_size;
	private String collection_filter;
	private Boolean compress;
	private String conf;
	private String content_encoding;

	// TODO these should all be booleans
	private String copy_collections;
	private String copy_metadata;
	private String copy_permissions;
	private String copy_properties;
	private String copy_quality;

	private String data_type;
	private String delimiter;
	private String delimited_root_name;
	private String directory_filter;
	private String document_selector;
	private String document_type;
	private Boolean fastload;
	private String filename_as_collection;
	private Boolean generate_uri;
	private String hadoop_conf_dir;
	private Boolean indented;
	private Boolean input_compressed;
	private String input_compression_codec;
	private String input_file_path;
	private String input_file_pattern;
	private String input_file_type;
	private Boolean input_ssl;
	private Integer max_split_size;
	private Integer min_split_size;
	private String mode;
	private String modules;
	private String modules_root;
	private String namespace;
	private String options_file;
	private Boolean output_cleandir;
	private String output_collections;
	private String output_directory;
	private String output_file_path;
	private String output_graph;
	private String output_language;
	private String output_override_graph;
	private String output_partition;
	private String output_permissions;
	private String output_quality;
	private Boolean output_ssl;
	private String output_type;
	private String output_uri_prefix;
	private String output_uri_replace;
	private String output_uri_suffix;
	private String path_namespace;
	private String query_filter;
	private String redaction;
	private Boolean restrict_hosts;
	private Boolean restrict_input_hosts;
	private Boolean restrict_output_hosts;
	private String sequencefile_key_class;
	private String sequencefile_value_class;
	private String sequencefile_value_type;
	private Boolean snapshot;
	private Boolean split_input;
	private Boolean ssl;
	private Boolean streaming;
	private String temporal_collection;
	private Integer thread_count;
	private Integer thread_count_per_split;
	private Boolean tolerate_errors;
	private String transform_function;
	private String transform_module;
	private String transform_namespace;
	private String transform_param;
	private Integer transaction_size;
	private String type_filter;
	private String uri_id;
	private String xml_repair_level;

	// Just in case, e.g. for when mlcp is upgraded and this library doesn't yet match it
	private Map<String, String> additionalOptions = new HashMap<>();

	/**
	 * Uses Spring's BeanWrapper to build up a list of arguments that can be fed into mlcp's
	 * OptionsFileUtil.expandArguments method.
	 *
	 * @return array of arguments
	 */
	public String[] buildArgs() {
		List<String> l = new ArrayList<String>();
		l.add(command);
		BeanWrapper bw = new BeanWrapperImpl(this);
		for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
			String name = pd.getName();
			if (pd.getReadMethod() != null && pd.getWriteMethod() != null && !"command".equals(name)
				&& !"additionalOptions".equals(name)) {
				Object value = bw.getPropertyValue(name);
				if (value != null && value.toString().trim().length() > 0) {
					l.add("-" + name);
					l.add(value.toString());
				}
			}
		}

		if (additionalOptions != null) {
			for (String key : additionalOptions.keySet()) {
				String value = additionalOptions.get(key);
				if (value != null) {
					l.add("-" + key);
					l.add(value);
				}
			}
		}

		return l.toArray(new String[]{});
	}

	/**
	 * Returns a string of all the args, excluding the password - useful for passing into a log method.
	 *
	 * @param args the arguments to be fed to MLCP
	 * @return string representation of the args, minus the password
	 */
	public String viewArgs(String[] args) {
		List<String> argList = new ArrayList<String>();
		for (int i = 0; i < args.length; i++) {
			if ("-password".equals(args[i]) && (i + 1 <= args.length)) {
				i++;
				continue;
			}
			argList.add(args[i]);
		}
		return "Content Pump args (not showing password): " + argList;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public Integer getPort() {
		return port;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabase() {
		return database;
	}

	public void setDatabase(String database) {
		this.database = database;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public String getInput_file_path() {
		return input_file_path;
	}

	public void setInput_file_path(String input_file_path) {
		this.input_file_path = input_file_path;
	}

	public String getInput_file_type() {
		return input_file_type;
	}

	public void setInput_file_type(String input_file_type) {
		this.input_file_type = input_file_type;
	}

	public String getInput_file_pattern() {
		return input_file_pattern;
	}

	public void setInput_file_pattern(String input_file_pattern) {
		this.input_file_pattern = input_file_pattern;
	}

	public Boolean getInput_compressed() {
		return input_compressed;
	}

	public void setInput_compressed(Boolean input_compressed) {
		this.input_compressed = input_compressed;
	}

	public String getDocument_type() {
		return document_type;
	}

	public void setDocument_type(String document_type) {
		this.document_type = document_type;
	}

	public String getOutput_collections() {
		return output_collections;
	}

	public void setOutput_collections(String output_collections) {
		this.output_collections = output_collections;
	}

	public String getDelimited_root_name() {
		return delimited_root_name;
	}

	public void setDelimited_root_name(String delimited_root_name) {
		this.delimited_root_name = delimited_root_name;
	}

	public String getNamespace() {
		return namespace;
	}

	public void setNamespace(String namespace) {
		this.namespace = namespace;
	}

	public String getOptions_file() {
		return options_file;
	}

	public void setOptions_file(String options_file) {
		this.options_file = options_file;
	}

	public String getOutput_uri_prefix() {
		return output_uri_prefix;
	}

	public void setOutput_uri_prefix(String output_uri_prefix) {
		this.output_uri_prefix = output_uri_prefix;
	}

	public String getOutput_uri_replace() {
		return output_uri_replace;
	}

	public void setOutput_uri_replace(String output_uri_replace) {
		this.output_uri_replace = output_uri_replace;
	}

	public String getOutput_permissions() {
		return output_permissions;
	}

	public void setOutput_permissions(String output_permissions) {
		this.output_permissions = output_permissions;
	}

	public String getTransform_module() {
		return transform_module;
	}

	public void setTransform_module(String transform_module) {
		this.transform_module = transform_module;
	}

	public String getTransform_namespace() {
		return transform_namespace;
	}

	public void setTransform_namespace(String transform_namespace) {
		this.transform_namespace = transform_namespace;
	}

	public String getTransform_param() {
		return transform_param;
	}

	public void setTransform_param(String transform_param) {
		this.transform_param = transform_param;
	}

	public Integer getThread_count() {
		return thread_count;
	}

	public void setThread_count(Integer thread_count) {
		this.thread_count = thread_count;
	}

	public Boolean getGenerate_uri() {
		return generate_uri;
	}

	public void setGenerate_uri(Boolean generate_uri) {
		this.generate_uri = generate_uri;
	}

	public String getOutput_uri_suffix() {
		return output_uri_suffix;
	}

	public void setOutput_uri_suffix(String output_uri_suffix) {
		this.output_uri_suffix = output_uri_suffix;
	}

	public Boolean getSplit_input() {
		return split_input;
	}

	public void setSplit_input(Boolean split_input) {
		this.split_input = split_input;
	}

	public Integer getBatch_size() {
		return batch_size;
	}

	public void setBatch_size(Integer batch_size) {
		this.batch_size = batch_size;
	}

	public String getXml_repair_level() {
		return xml_repair_level;
	}

	public void setXml_repair_level(String xml_repair_level) {
		this.xml_repair_level = xml_repair_level;
	}

	public String getAggregate_record_element() {
		return aggregate_record_element;
	}

	public void setAggregate_record_element(String aggregate_record_element) {
		this.aggregate_record_element = aggregate_record_element;
	}

	public String getAggregate_record_namespace() {
		return aggregate_record_namespace;
	}

	public void setAggregate_record_namespace(String aggregate_record_namespace) {
		this.aggregate_record_namespace = aggregate_record_namespace;
	}

	public String getCollection_filter() {
		return collection_filter;
	}

	public void setCollection_filter(String collection_filter) {
		this.collection_filter = collection_filter;
	}

	public String getConf() {
		return conf;
	}

	public void setConf(String conf) {
		this.conf = conf;
	}

	public String getContent_encoding() {
		return content_encoding;
	}

	public void setContent_encoding(String content_encoding) {
		this.content_encoding = content_encoding;
	}

	public String getCopy_collections() {
		return copy_collections;
	}

	public void setCopy_collections(String copy_collections) {
		this.copy_collections = copy_collections;
	}

	public String getCopy_permissions() {
		return copy_permissions;
	}

	public void setCopy_permissions(String copy_permissions) {
		this.copy_permissions = copy_permissions;
	}

	public String getCopy_properties() {
		return copy_properties;
	}

	public void setCopy_properties(String copy_properties) {
		this.copy_properties = copy_properties;
	}

	public String getCopy_quality() {
		return copy_quality;
	}

	public void setCopy_quality(String copy_quality) {
		this.copy_quality = copy_quality;
	}

	public String getData_type() {
		return data_type;
	}

	public void setData_type(String data_type) {
		this.data_type = data_type;
	}

	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDirectory_filter() {
		return directory_filter;
	}

	public void setDirectory_filter(String directory_filter) {
		this.directory_filter = directory_filter;
	}

	public Boolean getFastload() {
		return fastload;
	}

	public void setFastload(Boolean fastload) {
		this.fastload = fastload;
	}

	public String getFilename_as_collection() {
		return filename_as_collection;
	}

	public void setFilename_as_collection(String filename_as_collection) {
		this.filename_as_collection = filename_as_collection;
	}

	public String getHadoop_conf_dir() {
		return hadoop_conf_dir;
	}

	public void setHadoop_conf_dir(String hadoop_conf_dir) {
		this.hadoop_conf_dir = hadoop_conf_dir;
	}

	public Boolean getArchive_metadata_optional() {
		return archive_metadata_optional;
	}

	public void setArchive_metadata_optional(Boolean archive_metadata_optional) {
		this.archive_metadata_optional = archive_metadata_optional;
	}

	public String getInput_compression_codec() {
		return input_compression_codec;
	}

	public void setInput_compression_codec(String input_compression_codec) {
		this.input_compression_codec = input_compression_codec;
	}

	public String getSequencefile_key_class() {
		return sequencefile_key_class;
	}

	public void setSequencefile_key_class(String sequencefile_key_class) {
		this.sequencefile_key_class = sequencefile_key_class;
	}

	public String getSequencefile_value_class() {
		return sequencefile_value_class;
	}

	public void setSequencefile_value_class(String sequencefile_value_class) {
		this.sequencefile_value_class = sequencefile_value_class;
	}

	public String getSequencefile_value_type() {
		return sequencefile_value_type;
	}

	public void setSequencefile_value_type(String sequencefile_value_type) {
		this.sequencefile_value_type = sequencefile_value_type;
	}

	public Integer getMax_split_size() {
		return max_split_size;
	}

	public void setMax_split_size(Integer max_split_size) {
		this.max_split_size = max_split_size;
	}

	public Integer getMin_split_size() {
		return min_split_size;
	}

	public void setMin_split_size(Integer min_split_size) {
		this.min_split_size = min_split_size;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public Boolean getOutput_cleandir() {
		return output_cleandir;
	}

	public void setOutput_cleandir(Boolean output_cleandir) {
		this.output_cleandir = output_cleandir;
	}

	public String getOutput_directory() {
		return output_directory;
	}

	public void setOutput_directory(String output_directory) {
		this.output_directory = output_directory;
	}

	public String getOutput_graph() {
		return output_graph;
	}

	public void setOutput_graph(String output_graph) {
		this.output_graph = output_graph;
	}

	public String getOutput_language() {
		return output_language;
	}

	public void setOutput_language(String output_language) {
		this.output_language = output_language;
	}

	public String getOutput_partition() {
		return output_partition;
	}

	public void setOutput_partition(String output_partition) {
		this.output_partition = output_partition;
	}

	public String getOutput_override_graph() {
		return output_override_graph;
	}

	public void setOutput_override_graph(String output_override_graph) {
		this.output_override_graph = output_override_graph;
	}

	public String getOutput_quality() {
		return output_quality;
	}

	public void setOutput_quality(String output_quality) {
		this.output_quality = output_quality;
	}

	public Boolean getStreaming() {
		return streaming;
	}

	public void setStreaming(Boolean streaming) {
		this.streaming = streaming;
	}

	public String getTemporal_collection() {
		return temporal_collection;
	}

	public void setTemporal_collection(String temporal_collection) {
		this.temporal_collection = temporal_collection;
	}

	public Integer getThread_count_per_split() {
		return thread_count_per_split;
	}

	public void setThread_count_per_split(Integer thread_count_per_split) {
		this.thread_count_per_split = thread_count_per_split;
	}

	public Boolean getTolerate_errors() {
		return tolerate_errors;
	}

	public void setTolerate_errors(Boolean tolerate_errors) {
		this.tolerate_errors = tolerate_errors;
	}

	public String getTransform_function() {
		return transform_function;
	}

	public void setTransform_function(String transform_function) {
		this.transform_function = transform_function;
	}

	public Integer getTransaction_size() {
		return transaction_size;
	}

	public void setTransaction_size(Integer transaction_size) {
		this.transaction_size = transaction_size;
	}

	public String getType_filter() {
		return type_filter;
	}

	public void setType_filter(String type_filter) {
		this.type_filter = type_filter;
	}

	public String getUri_id() {
		return uri_id;
	}

	public void setUri_id(String uri_id) {
		this.uri_id = uri_id;
	}

	public String getInput_host() {
		return input_host;
	}

	public void setInput_host(String input_host) {
		this.input_host = input_host;
	}

	public Integer getInput_port() {
		return input_port;
	}

	public void setInput_port(Integer input_port) {
		this.input_port = input_port;
	}

	public String getInput_username() {
		return input_username;
	}

	public void setInput_username(String input_username) {
		this.input_username = input_username;
	}

	public String getInput_password() {
		return input_password;
	}

	public void setInput_password(String input_password) {
		this.input_password = input_password;
	}

	public String getInput_database() {
		return input_database;
	}

	public void setInput_database(String input_database) {
		this.input_database = input_database;
	}

	public String getOutput_host() {
		return output_host;
	}

	public void setOutput_host(String output_host) {
		this.output_host = output_host;
	}

	public Integer getOutput_port() {
		return output_port;
	}

	public void setOutput_port(Integer output_port) {
		this.output_port = output_port;
	}

	public String getOutput_username() {
		return output_username;
	}

	public void setOutput_username(String output_username) {
		this.output_username = output_username;
	}

	public String getOutput_password() {
		return output_password;
	}

	public void setOutput_password(String output_password) {
		this.output_password = output_password;
	}

	public String getOutput_database() {
		return output_database;
	}

	public void setOutput_database(String output_database) {
		this.output_database = output_database;
	}

	public Boolean getCompress() {
		return compress;
	}

	public void setCompress(Boolean compress) {
		this.compress = compress;
	}

	public String getDocument_selector() {
		return document_selector;
	}

	public void setDocument_selector(String document_selector) {
		this.document_selector = document_selector;
	}

	public Boolean getIndented() {
		return indented;
	}

	public void setIndented(Boolean indented) {
		this.indented = indented;
	}

	public String getOutput_file_path() {
		return output_file_path;
	}

	public void setOutput_file_path(String output_file_path) {
		this.output_file_path = output_file_path;
	}

	public String getOutput_type() {
		return output_type;
	}

	public void setOutput_type(String output_type) {
		this.output_type = output_type;
	}

	public String getPath_namespace() {
		return path_namespace;
	}

	public void setPath_namespace(String path_namespace) {
		this.path_namespace = path_namespace;
	}

	public String getQuery_filter() {
		return query_filter;
	}

	public void setQuery_filter(String query_filter) {
		this.query_filter = query_filter;
	}

	public Boolean getSnapshot() {
		return snapshot;
	}

	public void setSnapshot(Boolean snapshot) {
		this.snapshot = snapshot;
	}

	public Map<String, String> getAdditionalOptions() {
		return additionalOptions;
	}

	public void setAdditionalOptions(Map<String, String> additionalOptions) {
		this.additionalOptions = additionalOptions;
	}


	public String getCopy_metadata() {
		return copy_metadata;
	}

	public void setCopy_metadata(String copy_metadata) {
		this.copy_metadata = copy_metadata;
	}

	public Boolean getInput_ssl() {
		return input_ssl;
	}

	public void setInput_ssl(Boolean input_ssl) {
		this.input_ssl = input_ssl;
	}

	public Boolean getOutput_ssl() {
		return output_ssl;
	}

	public void setOutput_ssl(Boolean output_ssl) {
		this.output_ssl = output_ssl;
	}

	public String getRedaction() {
		return redaction;
	}

	public void setRedaction(String redaction) {
		this.redaction = redaction;
	}

	public Boolean getRestrict_hosts() {
		return restrict_hosts;
	}

	public void setRestrict_hosts(Boolean restrict_hosts) {
		this.restrict_hosts = restrict_hosts;
	}

	public Boolean getRestrict_input_hosts() {
		return restrict_input_hosts;
	}

	public void setRestrict_input_hosts(Boolean restrict_input_hosts) {
		this.restrict_input_hosts = restrict_input_hosts;
	}

	public Boolean getRestrict_output_hosts() {
		return restrict_output_hosts;
	}

	public void setRestrict_output_hosts(Boolean restrict_output_hosts) {
		this.restrict_output_hosts = restrict_output_hosts;
	}

	public Boolean getSsl() {
		return ssl;
	}

	public void setSsl(Boolean ssl) {
		this.ssl = ssl;
	}

	public String getModules() {
		return modules;
	}

	public void setModules(String modules) {
		this.modules = modules;
	}

	public String getModules_root() {
		return modules_root;
	}

	public void setModules_root(String modules_root) {
		this.modules_root = modules_root;
	}
}
