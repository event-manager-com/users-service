package gregad.eventmanager.usersservice.dto;

import lombok.*;

import java.util.List;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private long id;
    private String telegramId;
    private List<String> allowedSocialNetworks;
}
