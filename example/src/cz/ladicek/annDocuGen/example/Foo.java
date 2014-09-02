package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;

/** Foo service */
public class Foo {
    /** Property foo */
    @Inject @Property("foo")
    private String foo;

    /** Depends on Bar */
    @Inject
    private Bar bar;

    public void doSomething() {
        bar.doSomething(foo);
    }
}
