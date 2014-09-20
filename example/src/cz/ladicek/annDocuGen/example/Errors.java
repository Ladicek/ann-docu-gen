package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

/** Examples of unsupported annotation positions */
public class Errors {
    @Inject
    @Property("error.1")
    private String a;

    @Property("error.2")
    public void b() {
    }

    @Inject
    public void c(Baz bar) {
    }

    @Property("error.3")
    public Errors() {
    }
}
