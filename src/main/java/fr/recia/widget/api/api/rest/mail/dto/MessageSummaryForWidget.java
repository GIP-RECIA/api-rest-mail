package fr.recia.widget.api.api.rest.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.mail.Address;

@Data
@AllArgsConstructor
@NoArgsConstructor // used when serializing from cache
public class MessageSummaryForWidget {

   String subject;
   long id;
   boolean isRead;

}
