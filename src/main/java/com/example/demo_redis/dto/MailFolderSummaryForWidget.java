package com.example.demo_redis.dto;

import lombok.Data;

import java.util.List;

@Data
public class MailFolderSummaryForWidget {

    final List<MessageSummaryForWidget> messageSummaryForWidgetList;
    final String inbox;
    final int messageNotReadCount;

}
