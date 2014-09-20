package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Singleton;

/** Baz service */
@Singleton
public class Baz {
    /** Property baz */
    @Property("baz")
    private String baz;

    @Inject
    public Baz(Quux quux) {
    }
}
