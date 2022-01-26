/*
 *
 * Version:  1.0.0
 *
 * Authors:  Kumar <kumar@elitasolutions.in>
 *
 *********************
 *
 * Copyright (c) 2009,2010,2011 Elita IT Solutions
 * All Rights Reserved.
 *
 *********************
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Elita IT Solutions and its suppliers, if any.
 * The intellectual and technical concepts contained
 * herein are proprietary to Elita IT Solutions
 * and its suppliers and may be covered by India and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Elita IT Solutions.
 *
 * The above copyright notice and this permission notice must be included
 * in all copies of this file.
 *
 * Description:
 */
package com.zestic.common.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Document extends Entity {

    @JsonProperty("version")
    private Long version;

    @JsonProperty("created_at")
    private Date createdAt;

    @JsonProperty("modified_at")
    private Date modifiedAt;
}