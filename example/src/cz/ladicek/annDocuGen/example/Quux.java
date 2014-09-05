package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

/** Quux service */
public class Quux {
    /** Property quux */
    @Inject @Property("quux")
    private String quux;
}
