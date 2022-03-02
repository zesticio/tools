package com.zestic.common.find_instances.subpackage;

import com.zestic.common.find_instances.TestInterface;

/**
 * @author hoersch
 */
public enum TestEnumWithSyntheticInnerClasses implements TestInterface {

    /**
     * Abc.
     */
    ABC,

    /**
     * Def.
     */
    DEF {
        @Override
        public void otherMethod() {
            System.out.println("Other method in " + name() + " called...");
        }
    };

    public void otherMethod() {
        System.out.println("Other method called....");
    }
}
