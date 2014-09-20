package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Inject;
import cz.ladicek.annDocuGen.api.Optional;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Unit;

/** Example unit */
public class ExampleUnit implements Unit {
    /** Depends on Foo */
    @Inject
    private Foo foo;

    /** A simple property */
    @Property("my.property")
    private String myProperty;

    /** Property of a primitive type */
    @Property("my.other.property")
    private long myOtherProperty;

    /** Another property of a primitive type */
    @Property("my.next.property")
    private boolean myNextProperty;

    /** Property with default value */
    @Property("my.property.with.default.value")
    private String myPropertyWithDefaultValue = "default value";

    /** Optional property */
    @Property("my.optional.property")
    private Optional<String> myOptionalProperty;

    /** Optional property that defaults to absent explicitly */
    @Property("my.optional.absenting.property")
    private Optional<String> myOptionalAbsentingProperty = Optional.absent();

    /** Another way of defining a property with default value */
    @Property("my.optional.property.with.default.value")
    private Optional<String> myOptionalPropertyWithDefaultValue = Optional.of("default value");

    @Inject
    public ExampleUnit(Baz baz) {
    }

    @Override
    public void execute() {
    }
}
