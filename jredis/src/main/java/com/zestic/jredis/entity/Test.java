package com.zestic.jredis.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Test extends RedisEntity {

    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;
}
