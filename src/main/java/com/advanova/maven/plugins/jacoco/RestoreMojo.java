package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "restore-instrumented-classes", defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class RestoreMojo extends org.jacoco.maven.RestoreMojo {
}
