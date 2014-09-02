package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Unit;

/** Example unit */
public class ExampleUnit implements Unit {
    /** Depends on Foo */
    @Inject
    private Foo foo;

    /** Property my.property */
    @Inject @Property("my.property")
    private String myProperty;

    @Inject @Property("my.other.property")
    private long myOtherProperty;

    @Override
    public void execute() {
        foo.doSomething();
    }
}
