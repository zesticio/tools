package com.zestic.common.entity;

import com.zestic.common.exception.NotEnoughDataInByteBufferException;
import com.zestic.common.exception.TerminatingZeroNotFoundException;
import com.zestic.common.utils.Buffer;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;

public class Message extends Entity implements Serializable, Cloneable {

    private Fields fields = new Fields();

    /**
     * transaction id, unique for every transaction
     */
    private String messageId = "";
    /**
     * Used to associate the current message with a previous message.
     * This header is commonly used to associate a response message with a
     * request message.
     */
    private String correlationId = "";
    private Header header = null;

    public void setHeader(Buffer buffer) throws NotEnoughDataInByteBufferException, TerminatingZeroNotFoundException, UnsupportedEncodingException {
        checkHeader();
        header.setData(buffer);
    }

    protected void checkHeader() {
        if (header == null) {
            header = new Header();
        }
    }

    public void setCommandLength(int length) {
        checkHeader();
        header.setCommandLength(length);
    }

    public int getCommandLength() {
        checkHeader();
        return header.getCommandLength();
    }

    public void setCommandStatus(int status) {
        checkHeader();
        this.header.setCommandStatus(status);
    }

    public int getCommandStatus() {
        checkHeader();
        return header.getCommandStatus();
    }

    public int getCommandId() {
        checkHeader();
        return header.getCommandId();
    }

    public void setCommandType(int type) {
        checkHeader();
        this.header.setCommandType(type);
    }

    public int getCommandType() {
        checkHeader();
        return this.header.getCommandType();
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String value) {
        this.messageId = value;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String value) {
        this.correlationId = value;
    }


    @Override
    public String debugString() {
        StringBuilder buffer = new StringBuilder();
        String header = String.format("0x%08X 0x%08X",
                getCommandId(),
                getCommandStatus()
        );
        buffer.append(String.format("%-50s", header));
        buffer.append(String.format("%-11s%-39s", "message-id", getMessageId()));
        return buffer.toString();
    }
}
