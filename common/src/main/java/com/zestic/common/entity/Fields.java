package com.zestic.common.entity;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * lets use mapdb for synchronized collection
 */
public class Fields implements Serializable {

    Map<String, String> map = new HashMap<>();

    public Fields() {
        super();
    }
}
