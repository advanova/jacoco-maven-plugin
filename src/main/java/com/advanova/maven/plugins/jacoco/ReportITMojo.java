package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "report-integration", defaultPhase = LifecyclePhase.VERIFY, threadSafe = true)
public class ReportITMojo extends org.jacoco.maven.ReportITMojo {
}
