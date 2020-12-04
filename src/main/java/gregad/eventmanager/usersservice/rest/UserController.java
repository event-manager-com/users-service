package gregad.eventmanager.usersservice.rest;

import gregad.eventmanager.usersservice.dto.SocialNetworkCredentialDto;
import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static gregad.eventmanager.usersservice.api.ApiConstants.*;

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
    UserDto addUser(@RequestParam int telegramId){
        return userService.addUser(telegramId);
    }
    
//    @PatchMapping
//    UserDto updateUser(@RequestBody UserDto userDto){
//        return userService.updateUser(userDto);
//    }
    
    @DeleteMapping(value = "/{id}")
    UserDto deleteUser(@PathVariable int id){
       return userService.deleteUser(id);
    }
    
    @PostMapping(value = NETWORKS)
    UserDto saveOrUpdateNetworkCredentials(@RequestBody SocialNetworkCredentialDto networkCredential){
        return userService.saveOrUpdateNetwork(networkCredential);
    }
    
    @DeleteMapping(value = NETWORKS)
    UserDto deleteNetwork(@RequestParam int id,@RequestParam String network){
        return userService.deleteNetwork(id,network);
    }
    
    @GetMapping(value = NETWORKS+"/{id}")
    List<String> getNetworks(@PathVariable int id){
        return userService.getNetworks(id);
    }
}
