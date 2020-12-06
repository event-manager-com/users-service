package gregad.eventmanager.usersservice.rest;

import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.services.user_service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static gregad.eventmanager.usersservice.api.ApiConstants.USERS;

/**
 * @author Greg Adler
 */
@RestController
@RequestMapping(USERS)
public class UserController {
    
    @Autowired
    UserService userService;
    
    @GetMapping(value = "/{id}")
    UserDto getUser(@PathVariable int id){
        return userService.getUser(id);
    }
    
    @PostMapping
    UserDto addUser(@RequestParam int telegramId,@RequestParam String name){
        return userService.addUser(telegramId,name);
    }
    
    @PatchMapping
    UserDto updateUser(@RequestBody UserDto userDto){
        return userService.updateUser(userDto);
    }
    
    @DeleteMapping(value = "/{id}")
    UserDto deleteUser(@PathVariable int id){
       return userService.deleteUser(id);
    }

}
