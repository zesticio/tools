package com.zestic.common.find_instances.subpackage;

import com.zestic.common.find_instances.TestInterface;

public class TestClass implements TestInterface {

    @Override
    public String name() {
        return getClass().getSimpleName();
    }

}
