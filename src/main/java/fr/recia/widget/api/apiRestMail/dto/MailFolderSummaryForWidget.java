package fr.recia.widget.api.apiRestMail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor // used when serializing from cache
public class MailFolderSummaryForWidget {

    List<MessageSummaryForWidget> messageSummaryForWidgetList;
    String inbox;
    int messageNotReadCount;

}
