package gregad.eventmanager.usersservice.services.user_service;

import gregad.eventmanager.usersservice.dao.UserDao;
import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Greg Adler
 */
@Service
@RefreshScope
public class UserServiceImpl implements UserService {

    private UserDao repo;
    
    @Autowired
    public UserServiceImpl(UserDao repo) {
        this.repo = repo;
    }

    @Override
    public UserDto addUser(int telegramId, String name) {
        UserEntity user =repo.findById(telegramId).orElse(null);
        if (user!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User id:"+telegramId+" already exists");
        }
        user=new UserEntity(telegramId,name);
        repo.save(user);
        return toUserDto(user);
    }

    private UserDto toUserDto(UserEntity user) {
        return new UserDto(user.getId(),user.getName());
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto getUser(int id) {
        return toUserDto(repo
                .findById(id)
                .orElseThrow(()->{throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with id:"+id+" not found");}));
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto deleteUser(int id) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        repo.deleteById(id);
        return toUserDto(userEntity);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = repo.findById(userDto.getId())
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + userDto.getId() + " not found");});
        userEntity.setName(userDto.getName());
        repo.save(userEntity);
        return toUserDto(userEntity);
    }

}
