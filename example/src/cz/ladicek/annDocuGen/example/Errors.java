package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

/** Examples of unsupported annotation positions */
public class Errors {
    @Inject @Property("error.1")
    public void a() {}

    @Inject
    public void b(Baz bar) {}

    @Inject @Property("error.2")
    public Errors() {}
}
