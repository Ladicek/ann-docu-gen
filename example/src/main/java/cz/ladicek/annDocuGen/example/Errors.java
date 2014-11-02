package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Output;
import cz.ladicek.annDocuGen.api.OutputProperty;
import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;

/** Examples of unsupported annotation positions and other kinds of errors. */
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

/*
    @Inject
    @OutputProperty("error.4")
    private Output<String> d;

    @OutputProperty("error 5")
    private Output<String> e;

    @OutputProperty("error.6")
    public void f() {
    }

    @OutputProperty("error.7")
    private String g;

    @Property("error.8") @OutputProperty("error.8")
    private Output<String> h;
*/

    @Inject
    public void i(Baz bar) {
    }

/*
    @Property("error.9")
    public Errors() {
    }

    @OutputProperty("error.10")
    public Errors(int ignored) {
    }
*/
}
