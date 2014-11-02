package cz.ladicek.annDocuGen.example2;

import com.google.common.base.Optional;
import cz.ladicek.annDocuGen.api.Output;
import cz.ladicek.annDocuGen.api.OutputProperty;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Unit;
import cz.ladicek.annDocuGen.example.OutsideService;

import javax.inject.Inject;

public abstract class AbstractMyUnit implements Unit {
    @Property("myunit.base")
    private String base;

    @Property("myunit.optional")
    private Optional<String> optional;

    @OutputProperty("myunit.output")
    private Output<String> output;

    @Inject
    private InheritedService inheritedService;

    @Inject
    private OutsideService outsideService;
}
