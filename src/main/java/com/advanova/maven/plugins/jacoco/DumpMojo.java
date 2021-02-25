package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "dump", defaultPhase = LifecyclePhase.POST_INTEGRATION_TEST, threadSafe = true)
public class DumpMojo extends org.jacoco.maven.DumpMojo {
}
