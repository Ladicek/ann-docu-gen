package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

/** Baz service */
public class Baz {
    /** Property baz */
    @Inject @Property("baz")
    private String baz;

    @Inject
    public Baz(Quux quux) {}
}
