package gregad.eventmanager.usersservice.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@Builder
public class UserDto {
    private long id;
    private String telegramId;
    private List<String> allowedSocialNetworks;
}
