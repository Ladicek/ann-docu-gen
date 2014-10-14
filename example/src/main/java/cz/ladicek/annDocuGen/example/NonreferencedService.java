package cz.ladicek.annDocuGen.example;

import javax.inject.Inject;

/**
 * A simple service that is never referenced yet it is documented (because of the trivial {@code @Inject} constructor).
 */
public class NonreferencedService {
    @Inject
    NonreferencedService() {
    }
}
