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
    private int id;
    private List<String> allowedSocialNetworks;
}
