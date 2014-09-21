package cz.ladicek.annDocuGen.example;

import com.google.common.base.Optional;
import cz.ladicek.annDocuGen.api.EnvironmentVariables;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.TheAnswer;
import cz.ladicek.annDocuGen.api.Unit;

import javax.inject.Inject;
import java.util.Map;

/** Example unit */
public class ExampleUnit implements Unit {
    /** Depends on Foo */
    @Inject
    @TheAnswer(42)
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
    public ExampleUnit(Baz baz, @EnvironmentVariables Map<String, String> environment) {
    }

    @Override
    public void execute() {
    }
}
