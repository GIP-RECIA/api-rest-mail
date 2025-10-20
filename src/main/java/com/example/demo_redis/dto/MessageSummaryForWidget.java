package com.example.demo_redis.dto;

import lombok.Data;

import javax.mail.Address;

@Data
public class MessageSummaryForWidget {

    final String subject;
    final long id;
    final boolean isRead;

}
