package cz.ladicek.annDocuGen.example2;

import com.google.common.base.Optional;
import cz.ladicek.annDocuGen.api.Property;
import cz.ladicek.annDocuGen.api.Unit;

import javax.inject.Inject;

public abstract class AbstractMyUnit implements Unit {
    @Property("myunit.base")
    private String base;

    @Property("myunit.optional")
    private Optional<String> optional;

    @Inject
    private InheritedService inheritedService;
}
