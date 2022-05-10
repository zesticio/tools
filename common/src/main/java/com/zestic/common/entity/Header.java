/*
 * Version:  1.0.0
 *
 * Authors:  Kumar <Deebendu Kumar>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    private int commandStatus = Constants.ROK.getCode();

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
