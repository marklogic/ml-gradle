package com.marklogic.client.ext.datamovement.job;

import java.util.List;
import java.util.Properties;

/**
 * Interface for a job to implement when it can be configured via a Properties object. A primary benefit for clients is
 * that they can call the getJobProperties method and e.g. print out this list so a user knows what properties are
 * available for a job.
 */
public interface ConfigurableJob {

	/**
	 * Configure this job with the given set of Properties.
	 *
	 * @return a list of strings, with each presumably being a validation error message
	 */
	List<String> configureJob(Properties props);

	/**
	 * @return the list of JobProperty objects for this job. One use case for this is for a client to print out the
	 * name and description of each property.
	 */
	List<JobProperty> getJobProperties();
}
