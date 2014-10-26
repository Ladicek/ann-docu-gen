package cz.ladicek.annDocuGen.example2;

import com.google.common.base.Optional;
import cz.ladicek.annDocuGen.api.Property;

import javax.inject.Inject;

public abstract class AbstractMyService {
    @Property("myservice.base")
    private String base;

    @Property("myservice.optional")
    private Optional<String> optional;

    @Inject
    private InheritedService inheritedService;
}
