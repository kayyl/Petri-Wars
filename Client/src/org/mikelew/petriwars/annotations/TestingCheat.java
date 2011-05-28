package org.mikelew.petriwars.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Dubbing a property or method with the TestingCheat annotation makes the cheatConsole 
 * automatically check to see if the testingcheat property has been enabled first.
 * @author timpittman
 *
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TestingCheat {}
