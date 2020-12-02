package gregad.eventmanager.usersservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SocialNetworkCredentialDto {
    private String id;
    private String network;
    private String userName;
    private String password;
}
