package cz.ladicek.annDocuGen.example2;

import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;

public class MyUnit1 extends AbstractMyUnit {
    @Property("myunit1.derived")
    private String derived;

    @Inject
    private MyService1 service;

    @Inject
    private PrivateService privateService;

    @Override
    public void execute() {
    }
}
