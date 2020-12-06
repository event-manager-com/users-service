package gregad.eventmanager.usersservice.services.user_service;

import gregad.eventmanager.usersservice.dto.UserDto;

/**
 * @author Greg Adler
 */
public interface UserService {
    UserDto addUser(int telegramId, String name);
    UserDto getUser(int id);
    UserDto deleteUser(int id);
    UserDto updateUser(UserDto userDto);

}
