package gregad.eventmanager.usersservice.services.user_service;

import gregad.eventmanager.usersservice.dto.SocialNetworkCredentialDto;
import gregad.eventmanager.usersservice.dto.UserDto;

import java.util.List;

/**
 * @author Greg Adler
 */
public interface UserService {
    UserDto addUser(int telegramId);
    UserDto getUser(int id);
    UserDto deleteUser(int id);
 //   UserDto updateUser(UserDto userDto);
    UserDto saveOrUpdateNetwork(SocialNetworkCredentialDto networkCredential);
    UserDto deleteNetwork(int id,String network);
    List<String> getNetworks(int id);

}
