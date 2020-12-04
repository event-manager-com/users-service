package gregad.eventmanager.usersservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

/**
 * @author Greg Adler
 */
@Getter
@AllArgsConstructor
public class NamePassword {
    private String name;
    private String password;
}
