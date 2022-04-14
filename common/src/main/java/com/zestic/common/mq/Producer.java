package com.zestic.common.mq;

import com.zestic.common.entity.Message;
import com.zestic.common.exception.ApplicationRuntimeException;

public interface Producer {

    void submit(String queueName, Message message) throws ApplicationRuntimeException;
}
