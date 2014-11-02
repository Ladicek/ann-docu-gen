package cz.ladicek.annDocuGen.example;

import javax.inject.Inject;

/**
 * A service in one module that is only referenced from another module. It needs the trivial constructor
 * to be documented, otherwise its documentation wouldn't be available when merging multiple modules' documentations.
 */
public class OutsideService {
    @Inject
    public OutsideService() {
    }
}
