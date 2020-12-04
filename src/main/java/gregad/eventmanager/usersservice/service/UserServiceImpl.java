package gregad.eventmanager.usersservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gregad.eventmanager.usersservice.api.ApiConstants;
import gregad.eventmanager.usersservice.dao.UserDao;
import gregad.eventmanager.usersservice.dto.*;
import gregad.eventmanager.usersservice.model.UserEntity;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Greg Adler
 */
@Service
public class UserServiceImpl implements UserService {
    
    private UserDao repo;
    
    
    private RestTemplate restTemplate;
    
    private ObjectMapper objectMapper;
    
    @Value("${router.service.url}")
    private String routerUrl;
    @Value("${security.service.url}")
    private String securityServiceUrl;
    
    private String token;
    @Value("${security.user.name}")
    private String secUserName;
    @Value("${security.user.password}")
    private String secPassword;
    private NamePassword namePassword;

    @Autowired
    public UserServiceImpl(UserDao repo, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.repo = repo;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }
    
    public void initToken(){
        namePassword=new NamePassword(secUserName,secPassword);
        updateToken();
    }
    
    @SneakyThrows
    @Scheduled(cron = "0 5 0 * * *")
    private void updateToken(){
        String jsonNamePassword = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(namePassword);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonNamePassword, httpHeaders);
        token = restTemplate.postForObject(securityServiceUrl + "/generate", request, Token.class).getToken();
    }


    @Override
    public UserDto addUser(int telegramId) {
        UserEntity user =repo.findById(telegramId).orElse(null);
        if (user!=null){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"User id:"+telegramId+" already exists");
        }
        user=new UserEntity(telegramId,new ArrayList<>());
        repo.save(user);
        return toUserDto(user);
    }

    private UserDto toUserDto(UserEntity user) {
        UserDto userDto = new UserDto(user.getId(),user.getAllowedSocialNetworks());
        return userDto;
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
        userEntity.getAllowedSocialNetworks().forEach(s->sendToDelete(id,s));
        return toUserDto(userEntity);
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//    @Override
//    public UserDto updateUser(UserDto userDto) {
//        UserEntity userEntity = repo.findById(userDto.getId())
//                .orElseThrow(() -> {
//                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + userDto.getId() + " not found");});
//        userEntity.setTelegramId(userDto.getTelegramId());
//        repo.save(userEntity);
//        return toUserDto(userEntity);
//    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto saveOrUpdateNetwork(SocialNetworkCredentialDto networkCredential) {
       int id = networkCredential.getId();
       UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        String network = SocialNetwork.getNetwork(networkCredential.getNetwork());
        if (!userEntity.getAllowedSocialNetworks().contains(network)) {
            userEntity.getAllowedSocialNetworks().add(network);
        }
       sendCredentials(networkCredential);
       repo.save(userEntity);
        return toUserDto(userEntity);
    }

    @SneakyThrows
    private void sendCredentials(SocialNetworkCredentialDto networkCredential) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String networkCredentialJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(networkCredential);
        HttpEntity<String> request = new HttpEntity<String>(networkCredentialJson, headers);
        restTemplate.postForObject(routerUrl+ ApiConstants.CREDENTIALS, request, Boolean.class);
 
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    

    @Override
    public UserDto deleteNetwork(int id, String network) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        String networkName = SocialNetwork.getNetwork(network);
        userEntity.getAllowedSocialNetworks().remove(networkName);
        sendToDelete(id,networkName);
        repo.save(userEntity);
        return toUserDto(userEntity);
    }

    private void sendToDelete(int id, String networkName) {
        restTemplate.delete(routerUrl+ApiConstants.CREDENTIALS+"?userId="+id+"&network="+networkName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<String> getNetworks(int id) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        List<String> allowedSocialNetworks = userEntity.getAllowedSocialNetworks();
        return allowedSocialNetworks;
    }
}
