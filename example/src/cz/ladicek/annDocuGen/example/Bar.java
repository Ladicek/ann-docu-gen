package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

/** Bar service */
public class Bar {
    /** Property bar */
    @Inject @Property("bar")
    private String bar;

    public void doSomething(String param) {
        System.out.println(bar + " " + param);
    }
}
