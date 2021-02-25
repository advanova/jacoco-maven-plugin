package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

@Mojo(name = "report-aggregate", threadSafe = true, requiresDependencyResolution = ResolutionScope.TEST)
public class ReportAggregateMojo extends org.jacoco.maven.AbstractReportAggregateMojo {
}
