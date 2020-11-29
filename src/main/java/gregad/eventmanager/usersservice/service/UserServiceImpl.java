package gregad.eventmanager.usersservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gregad.eventmanager.usersservice.dao.SequenceDao;
import gregad.eventmanager.usersservice.dao.UserDao;
import gregad.eventmanager.usersservice.dto.NetworkCredentialDto;
import gregad.eventmanager.usersservice.dto.SocialNetwork;
import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.model.DatabaseSequence;
import gregad.eventmanager.usersservice.model.UserEntity;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    UserDao repo;
    @Autowired
    SequenceDao sequenceRepo;
    @Autowired
    RestTemplate restTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public UserDto addUser(String telegramId) {
        long id=getId();
        UserEntity user = UserEntity.builder()
                .id(id)
                .telegramId(telegramId)
                .allowedSocialNetworks(new ArrayList<>())
                .build();
        repo.save(user);
        return toUserDto(user);
    }

    private long getId() {
        DatabaseSequence databaseSequence = sequenceRepo.findById(1).orElse(null);
        long id;
        if (databaseSequence==null){
            sequenceRepo.save(new DatabaseSequence(1,1l));
            id=1;
        }else {
            id=databaseSequence.getSeq()+1;
            databaseSequence.setSeq(id);
        }
        return id;
    }

    private UserDto toUserDto(UserEntity user) {
        UserDto userDto = UserDto.builder()
                .id(user.getId())
                .telegramId(user.getTelegramId())
                .allowedSocialNetworks(user.getAllowedSocialNetworks())
                .build();
        return userDto;
    }
/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto getUser(long id) {
        return toUserDto(repo
                .findById(id)
                .orElseThrow(()->{throw new ResponseStatusException(HttpStatus.NOT_FOUND,"User with id:"+id+" not found");}));
    }
//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public UserDto deleteUser(long id) {
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
    public UserDto saveOrUpdateNetwork(NetworkCredentialDto networkCredential) {
       long id = networkCredential.getId();
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
    private void sendCredentials(NetworkCredentialDto networkCredential) {
        String url = SocialNetwork.getUrl(networkCredential.getNetwork());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", networkCredential.getId());
        jsonObject.put("userName", networkCredential.getUserName());
        jsonObject.put("password", networkCredential.getPassword());
        
        HttpEntity<String> request = new HttpEntity<String>(jsonObject.toString(), headers);
        Boolean isAdded = restTemplate.postForObject(url, request, Boolean.class);
        if (!isAdded){
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,"");//TODO fill message
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    

    @Override
    public UserDto deleteNetwork(long id, String network) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        String networkName = SocialNetwork.getNetwork(network);
        userEntity.getAllowedSocialNetworks().remove(networkName);
        sendToDelete(id,networkName);
        repo.save(userEntity);
        return toUserDto(userEntity);
    }

    private void sendToDelete(long id, String networkName) {
        String url = SocialNetwork.getUrl(networkName);
        restTemplate.delete(url+"/"+id);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public List<String> getNetworks(long id) {
        UserEntity userEntity = repo.findById(id)
                .orElseThrow(() -> {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id:" + id + " not found");});
        return userEntity.getAllowedSocialNetworks();
    }
}
