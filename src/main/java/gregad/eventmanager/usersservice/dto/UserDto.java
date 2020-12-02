package gregad.eventmanager.usersservice.dto;

import lombok.*;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;

import java.util.List;

/**
 * @author Greg Adler
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String id;
    private String telegramId;
    private List<String> allowedSocialNetworks;
}
