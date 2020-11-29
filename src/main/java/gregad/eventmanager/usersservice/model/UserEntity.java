package gregad.eventmanager.usersservice.model;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
@Document(collection = "users")
public class UserEntity {
    private long id;
    private String telegramId;
    private List<String>allowedSocialNetworks;
}
