package gregad.eventmanager.usersservice.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user")
public class UserEntity {
    @Id
    private String id;
    private String telegramId;
    private List<String>allowedSocialNetworks;
}
