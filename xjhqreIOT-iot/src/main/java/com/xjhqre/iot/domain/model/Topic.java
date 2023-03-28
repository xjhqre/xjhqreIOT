package com.xjhqre.iot.domain.model;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Data;

/**
 * <p>
 * Topic
 * </p>
 *
 * @author xjhqre
 * @since 3月 16, 2023
 */
@Data
public class Topic {

    private String topic;

    private int qos;

    private String node;

    @JsonAlias("clientid")
    private String clientId;
}
