package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.jacoco.maven.AbstractReportAggregateAllMojo;

@Mojo(name = "report-aggregate-all", requiresDependencyResolution = ResolutionScope.TEST, threadSafe = true, aggregator = true)
public class ReportAggregateAllMojo extends AbstractReportAggregateAllMojo {
    @Parameter(defaultValue = "true")
    private boolean runInExecutionRootOnly;

    @Override
    public boolean canGenerateReport() {
        if (runInExecutionRootOnly && !getProject().isExecutionRoot()) {
            getLog().info(
                    "Skipping JaCoCo execution because this is not the execution root.");
            return false;
        }
        return super.canGenerateReport();
    }
}
