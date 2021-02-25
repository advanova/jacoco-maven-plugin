package com.advanova.maven.plugins.jacoco;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;

@Mojo(name = "instrument", defaultPhase = LifecyclePhase.PROCESS_CLASSES, threadSafe = true)
public class InstrumentMojo extends org.jacoco.maven.InstrumentMojo {
}
