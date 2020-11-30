package gregad.eventmanager.usersservice.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
public class SocialNetworkCredentialDto {
    private long id;
    private String network;
    private String userName;
    private String password;
}
