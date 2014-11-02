package cz.ladicek.annDocuGen.example2;

import cz.ladicek.annDocuGen.api.Output;
import cz.ladicek.annDocuGen.api.OutputProperty;
import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;

public class MyUnit2 extends AbstractMyUnit {
    @Property("myunit2.derived")
    private String derived;

    @OutputProperty("myunit2.derived.output")
    private Output<String> derivedOutput;

    @Inject
    private MyService2 service;

    @Inject
    private PrivateService privateService;

    @Override
    public void execute() {
    }
}
