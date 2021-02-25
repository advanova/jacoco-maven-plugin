package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "merge", defaultPhase = LifecyclePhase.GENERATE_RESOURCES, threadSafe = true)
public class MergeMojo extends org.jacoco.maven.MergeMojo {
}
