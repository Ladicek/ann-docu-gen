package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;

/** Examples of unsupported annotation positions */
public class Errors {
    // uncomment this to experience compile-time error checking

/*
    @Inject
    @Property("error.1")
    private String a;

    @Property("error 2")
    private String b;

    @Property("error.3")
    public void c() {
    }
*/

    @Inject
    public void d(Baz bar) {
    }

/*
    @Property("error.4")
    public Errors() {
    }
*/
}
