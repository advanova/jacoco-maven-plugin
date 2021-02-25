package org.jacoco.maven;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.jacoco.report.IReportGroupVisitor;

/**
 * Needs to be in the org.jacoco.maven namespace, to access package private classes
 */
public abstract class AbstractReportAggregateMojo extends ReportAggregateMojo {

    @Parameter(defaultValue = "false")
    boolean includeTransitive;

    @Override
    void loadExecutionData(final ReportSupport support) throws IOException {
        // https://issues.apache.org/jira/browse/MNG-5440
        if (dataFileIncludes == null) {
            dataFileIncludes = Arrays.asList("target/*.exec");
        }

        final FileFilter filter = new FileFilter(dataFileIncludes,
                dataFileExcludes);
        loadExecutionData(support, filter, getProject().getBasedir());
        for (final MavenProject dependency : findDependencies(
                Artifact.SCOPE_COMPILE, Artifact.SCOPE_RUNTIME,
                Artifact.SCOPE_PROVIDED, Artifact.SCOPE_TEST)) {
            loadExecutionData(support, filter, dependency.getBasedir());
        }
    }

    private void loadExecutionData(final ReportSupport support,
                                   final FileFilter filter, final File basedir) throws IOException {
        for (final File execFile : filter.getFiles(basedir)) {
            support.loadExecutionData(execFile);
        }
    }

    private List<MavenProject> findDependencies(final String... scopes) {
        final List<MavenProject> result = new ArrayList<MavenProject>();
        final List<String> scopeList = Arrays.asList(scopes);
        for (final Artifact artifact : includeTransitive ? getProject().getArtifacts() : getProject().getDependencyArtifacts()) {
            if (scopeList.contains(artifact.getScope())) {
                final MavenProject project = findProjectFromReactor(artifact);
                if (project != null && !result.contains(project)) {
                    result.add(project);
                }
            }
        }
        return result;
    }

    private MavenProject findProjectFromReactor(final Artifact d) {
        final VersionRange depVersionAsRange;
        try {
            depVersionAsRange = VersionRange
                    .createFromVersionSpec(d.getVersion());
        } catch (InvalidVersionSpecificationException e) {
            throw new AssertionError(e);
        }

        List<MavenProject> reactorProjects;
        try {
            Field reactorProjectsField = ReportAggregateMojo.class.getDeclaredField("reactorProjects");
            reactorProjectsField.setAccessible(true);
            reactorProjects = (List<MavenProject>) reactorProjectsField.get(this);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        for (final MavenProject p : reactorProjects) {
            final DefaultArtifactVersion pv = new DefaultArtifactVersion(
                    p.getVersion());
            if (p.getGroupId().equals(d.getGroupId())
                    && p.getArtifactId().equals(d.getArtifactId())
                    && depVersionAsRange.containsVersion(pv)) {
                return p;
            }
        }
        return null;
    }

    @Override
    void createReport(final IReportGroupVisitor visitor,
                      final ReportSupport support) throws IOException {
        final IReportGroupVisitor group = visitor.visitGroup(title);
        for (final MavenProject dependency : findDependencies(
                Artifact.SCOPE_COMPILE, Artifact.SCOPE_RUNTIME,
                Artifact.SCOPE_PROVIDED)) {
            support.processProject(group, dependency.getArtifactId(),
                    dependency, getIncludes(), getExcludes(), sourceEncoding);
        }
    }
}
