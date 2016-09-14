package com.a.eye.skywalking.network;

import com.a.eye.skywalking.network.common.AbstractDataSerializable;
import com.a.eye.skywalking.network.exception.ConvertFailedException;

public class BufferFileEOFProtocol extends AbstractDataSerializable {
    @Override
    public int getDataType() {
        return -1;
    }

    @Override
    public byte[] getData() {
        return new byte[0];
    }

    @Override
    public AbstractDataSerializable convertData(byte[] data) throws ConvertFailedException {
        return new BufferFileEOFProtocol();
    }

    public boolean isNull() {
        return false;
    }
}
