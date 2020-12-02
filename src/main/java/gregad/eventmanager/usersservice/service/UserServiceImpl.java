package gregad.eventmanager.usersservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gregad.eventmanager.usersservice.api.ApiConstants;
import gregad.eventmanager.usersservice.dao.SequenceDao;
import gregad.eventmanager.usersservice.dao.UserDao;
import gregad.eventmanager.usersservice.dto.SocialNetwork;
import gregad.eventmanager.usersservice.dto.SocialNetworkCredentialDto;
import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.model.DatabaseSequence;
import gregad.eventmanager.usersservice.model.UserEntity;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Greg Adler
 */
@Service
public class UserServiceImpl implements UserService {
    
    private UserDao repo;
    
    private SequenceDao sequenceRepo;
    
    private RestTemplate restTemplate;
    
    private ObjectMapper objectMapper;
    
    @Value("${router.service}")
    private String routerUrl;

    @Autowired
    public UserServiceImpl(UserDao repo, SequenceDao sequenceRepo, RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.repo = repo;
        this.sequenceRepo = sequenceRepo;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public UserDto addUser(String telegramId) {
        String id=getId();
        UserEntity user = new UserEntity(id,telegramId,new ArrayList<>());
        repo.save(user);
        return toUserDto(user);
    }

    private String getId() {
        DatabaseSequence databaseSequence = sequenceRepo.findById(1).orElse(null);
        long id;
        if (databaseSequence==null){
            sequenceRepo.save(new DatabaseSequence(1,1));
            id=1;
        }else {
            id=databaseSequence.getSeq()+1;
            databaseSequence.setSeq(id);
            sequenceRepo.save(databaseSequence);
        }
        return Long.toString(id);
    }

    private UserDto toUserDto(UserEntity user) {
        UserDto userDto = new UserDto(user.getId(),user.getTelegramId(),user.getAllowedSocialNetworks());
        return userDto;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto getUser(String id) {
        return toUserDto(repo
                .findById(id)
                .orElseThrow(()->{throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with id:"+id+" not found");}));
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto deleteUser(String id) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        repo.deleteById(id);
        userEntity.getAllowedSocialNetworks().forEach(s->sendToDelete(id,s));
        return toUserDto(userEntity);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto updateUser(UserDto userDto) {
        UserEntity userEntity = repo.findById(userDto.getId())
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + userDto.getId() + " not found");});
        userEntity.setTelegramId(userDto.getTelegramId());
        repo.save(userEntity);
        return toUserDto(userEntity);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto saveOrUpdateNetwork(SocialNetworkCredentialDto networkCredential) {
       String id = networkCredential.getId();
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
    public UserDto deleteNetwork(String id, String network) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        String networkName = SocialNetwork.getNetwork(network);
        userEntity.getAllowedSocialNetworks().remove(networkName);
        sendToDelete(id,networkName);
        repo.save(userEntity);
        return toUserDto(userEntity);
    }

    private void sendToDelete(String id, String networkName) {
        restTemplate.delete(routerUrl+ApiConstants.CREDENTIALS+"?userId="+id+"&network="+networkName);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<String> getNetworks(String id) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        List<String> allowedSocialNetworks = userEntity.getAllowedSocialNetworks();
        return allowedSocialNetworks;
    }
}
