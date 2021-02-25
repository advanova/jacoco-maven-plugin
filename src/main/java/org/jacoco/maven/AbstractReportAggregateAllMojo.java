/*******************************************************************************
 * Copyright (c) 2009, 2019 Mountainminds GmbH & Co. KG and Contributors
 * This program and the accompanying materials are made available under
 * the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    John Oliver, Marc R. Hoffmann, Jan Wloka - initial API and implementation
 *    https://github.com/bmaehr - https://github.com/jacoco/jacoco/files/4295329/ReportAggregateMojo.java.txt
 *
 *******************************************************************************/
package org.jacoco.maven;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jacoco.report.IReportGroupVisitor;

/**
 * Needs to be in the org.jacoco.maven namespace, to access package private classes
 */
public abstract class AbstractReportAggregateAllMojo extends AbstractReportMojo {

	@Parameter(defaultValue = "${session}", required = true, readonly = true)
	private MavenSession session;

	/**
	 * A list of execution data files to include in the report from each
	 * project. May use wildcard characters (* and ?). When not specified all
	 * *.exec files from the target folder will be included.
	 */
	@Parameter
	List<String> dataFileIncludes;

	/**
	 * A list of execution data files to exclude from the report. May use
	 * wildcard characters (* and ?). When not specified nothing will be
	 * excluded.
	 */
	@Parameter
	List<String> dataFileExcludes;

	/**
	 * Output directory for the reports. Note that this parameter is only
	 * relevant if the goal is run from the command line or from the default
	 * build lifecycle. If the goal is run indirectly as part of a site
	 * generation, the output directory configured in the Maven Site Plugin is
	 * used instead.
	 */
	@Parameter(defaultValue = "${project.reporting.outputDirectory}/jacoco-aggregate-all")
	private File outputDirectory;

	@Override
	boolean canGenerateReportRegardingDataFiles() {
		return true;
	}

	@Override
	boolean canGenerateReportRegardingClassesDirectory() {
		return true;
	}

	@Override
	void loadExecutionData(final ReportSupport support) throws IOException {
		// https://issues.apache.org/jira/browse/MNG-5440
		if (dataFileIncludes == null) {
			dataFileIncludes = Arrays.asList("target/*.exec");
		}

		final FileFilter filter = new FileFilter(dataFileIncludes,
				dataFileExcludes);
		loadExecutionData(support, filter, getProject().getBasedir());
		for (final MavenProject dependency : getProjects()) {
			loadExecutionData(support, filter, dependency.getBasedir());
		}
	}

	private void loadExecutionData(final ReportSupport support,
			final FileFilter filter, final File basedir) throws IOException {
		for (final File execFile : filter.getFiles(basedir)) {
			support.loadExecutionData(execFile);
		}
	}

	@Override
	void addFormatters(final ReportSupport support, final Locale locale)
			throws IOException {
		support.addAllFormatters(outputDirectory, outputEncoding, footer,
				locale);
	}

	@Override
	void createReport(final IReportGroupVisitor visitor,
			final ReportSupport support) throws IOException {
		final IReportGroupVisitor group = visitor.visitGroup(title);
		for (final MavenProject dependency : getProjects()) {
			support.processProject(group, dependency.getArtifactId(),
					dependency, getIncludes(), getExcludes(), sourceEncoding);
		}
	}

	@Override
	protected String getOutputDirectory() {
		return outputDirectory.getAbsolutePath();
	}

	@Override
	public void setReportOutputDirectory(final File reportOutputDirectory) {
		if (reportOutputDirectory != null && !reportOutputDirectory
				.getAbsolutePath().endsWith("jacoco-aggregate-all")) {
			outputDirectory = new File(reportOutputDirectory,
					"jacoco-aggregate-all");
		} else {
			outputDirectory = reportOutputDirectory;
		}
	}

	public String getOutputName() {
		return "jacoco-aggregate-all/index";
	}

	public String getName(final Locale locale) {
		return "JaCoCo Aggregate All";
	}

	private List<MavenProject> getProjects() {
		return session.getProjectDependencyGraph().getSortedProjects();
	}

}