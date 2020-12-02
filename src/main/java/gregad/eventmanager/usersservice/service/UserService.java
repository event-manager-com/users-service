package gregad.eventmanager.usersservice.service;

import gregad.eventmanager.usersservice.dto.SocialNetworkCredentialDto;
import gregad.eventmanager.usersservice.dto.UserDto;

import java.util.List;

/**
 * @author Greg Adler
 */
public interface UserService {
    UserDto addUser(String telegramId);
    UserDto getUser(String id);
    UserDto deleteUser(String id);
    UserDto updateUser(UserDto userDto);
    UserDto saveOrUpdateNetwork(SocialNetworkCredentialDto networkCredential);
    UserDto deleteNetwork(String id,String network);
    List<String> getNetworks(String id);

}
