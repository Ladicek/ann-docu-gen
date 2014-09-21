package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;
import javax.inject.Singleton;

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
