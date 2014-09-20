package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Singleton;

/** Foo service */
@Singleton
public class Foo {
    /** Property foo */
    @Property("foo")
    private String foo;

    /** Depends on Bar */
    @Inject
    private Bar bar;
}
