package com.a.eye.skywalking.network;

import com.a.eye.skywalking.network.common.NullableClass;

/**
 * Created by wusheng on 16/7/4.
 */
public class NullClass implements NullableClass {
    @Override
    public boolean isNull() {
        return true;
    }
}
