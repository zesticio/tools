package com.zestic.common.entity;

import com.zestic.common.Constants;
import com.zestic.common.exception.NotEnoughDataInByteBufferException;
import com.zestic.common.exception.TerminatingZeroNotFoundException;
import com.zestic.common.utils.Buffer;
import lombok.Data;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

@Data
public class Header implements Serializable {

    private int commandLength = 0;
    private int commandId = 0;
    private int commandType = 0;
    private int commandStatus = Constants.ROK;

    public Header() {
    }

    public Buffer getData() {
        Buffer buffer = new Buffer();
        buffer.appendInt(getCommandLength());
        buffer.appendInt(getCommandId());
        buffer.appendInt(getCommandType());
        buffer.appendInt(getCommandStatus());
        return buffer;
    }

    public void setData(Buffer buffer) throws NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException, UnsupportedEncodingException {
        setCommandLength(buffer.removeInt());
        setCommandId(buffer.removeInt());
        setCommandType(buffer.removeInt());
        setCommandStatus(buffer.removeInt());
    }

}
