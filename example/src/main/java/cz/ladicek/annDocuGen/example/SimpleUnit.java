package cz.ladicek.annDocuGen.example;

import cz.ladicek.annDocuGen.api.Unit;

/**
 * A simple unit that doesn't depend on anything and doesn't need any properties. It's used to demonstrate
 * the behavior in case a unit class has no {@code @Inject} nor {@code @Property} annotations.
 */
public class SimpleUnit implements Unit {
    @Override
    public void execute() {
    }
}
