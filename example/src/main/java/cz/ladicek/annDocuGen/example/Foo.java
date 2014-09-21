package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;
import javax.inject.Singleton;

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
