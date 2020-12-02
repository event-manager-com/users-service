package gregad.eventmanager.usersservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gregad.eventmanager.usersservice.UsersServiceApplication;
import gregad.eventmanager.usersservice.dao.SequenceDao;
import gregad.eventmanager.usersservice.dao.UserDao;
import gregad.eventmanager.usersservice.dto.SocialNetworkCredentialDto;
import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.model.DatabaseSequence;
import gregad.eventmanager.usersservice.model.UserEntity;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Greg Adler
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UsersServiceApplication.class)
class UserServiceImplTest {
    private UserEntity userEntity = 
           new UserEntity("1","@GregAdler",new ArrayList<>());

    private UserService userService;
    private SequenceDao sequenceDaoMock;
    private UserDao userDaoMock;
    private RestTemplate restTemplateMock;
    @BeforeEach
    public void init(){
         userDaoMock = Mockito.mock(UserDao.class);
         sequenceDaoMock = Mockito.mock(SequenceDao.class);
         restTemplateMock = Mockito.mock(RestTemplate.class);
         userService=new UserServiceImpl(userDaoMock,sequenceDaoMock,restTemplateMock,new ObjectMapper());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    @Test
    void addUser() {
        Mockito.when(userDaoMock.save(userEntity)).thenReturn(userEntity);
        DatabaseSequence databaseSequence = new DatabaseSequence(1, 1);
        Mockito.when(sequenceDaoMock.findById(1)).thenReturn(java.util.Optional.of(databaseSequence));
        Mockito.when(sequenceDaoMock.save(databaseSequence)).thenReturn(databaseSequence);
        
        
        UserDto result = userService.addUser("@Any");
        Assert.assertEquals("2",result.getId());
        Assert.assertEquals("@Any",result.getTelegramId());
        Assert.assertEquals(0,result.getAllowedSocialNetworks().size());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getUser() {
        Mockito.when(userDaoMock.findById("3")).
                thenReturn(java.util.Optional.of(
                        new UserEntity("3","Example",Arrays.asList("facebook", "twitter"))));

        UserDto result = userService.getUser("3");
        Assert.assertEquals("3",result.getId());
        Assert.assertEquals("Example",result.getTelegramId());
        Assert.assertEquals(2,result.getAllowedSocialNetworks().size());

        try {
            userService.getUser("2");
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:2 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void deleteUser() {
        Mockito.when(userDaoMock.findById("1")).thenReturn(java.util.Optional.ofNullable(userEntity));

        UserDto userDto = userService.deleteUser("1");
        Assert.assertEquals("1",userDto.getId());
        Assert.assertEquals("@GregAdler",userDto.getTelegramId());
        Assert.assertEquals(0,userDto.getAllowedSocialNetworks().size());
        try {
            userService.deleteUser("2");
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:2 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void updateUser() {
        Mockito.when(userDaoMock.findById("3")).
                thenReturn(java.util.Optional.of(
                        new UserEntity("3","Example",Arrays.asList("facebook", "twitter"))));

        UserDto result = userService.updateUser(new UserDto("3","new name",new ArrayList<>()));
        Assert.assertEquals("3",result.getId());
        Assert.assertEquals("new name",result.getTelegramId());
        Assert.assertEquals(2,result.getAllowedSocialNetworks().size());
        
        
        try {
            userService.updateUser(new UserDto("22","ff",new ArrayList<>()));
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:22 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void saveOrUpdateNetwork() {
        Mockito.when(restTemplateMock.postForObject("routerUrl"+"/credentials", 
                new HttpEntity<String>("",new HttpHeaders()), Boolean.class))
                .thenReturn(true);
        Mockito.when(userDaoMock.findById("1")).thenReturn(java.util.Optional.ofNullable(userEntity));

        UserDto actualUser = userService.saveOrUpdateNetwork(new SocialNetworkCredentialDto("1", "facebook", "g", "d"));
        Assert.assertEquals("1",actualUser.getId());
        Assert.assertEquals(1,actualUser.getAllowedSocialNetworks().size());
        Assert.assertTrue(actualUser.getAllowedSocialNetworks().contains("facebook"));

        try {
            userService.saveOrUpdateNetwork(new SocialNetworkCredentialDto("1","face","g","d"));
        } catch (IllegalArgumentException e) {
            Assert.assertEquals("Network: face not supported yet",e.getMessage());
        }

        try {
            userService.saveOrUpdateNetwork(new SocialNetworkCredentialDto("4","facebook","g","d"));
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:4 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void deleteNetwork() {
        Mockito.when(userDaoMock.findById("1")).thenReturn(java.util.Optional.of(
                new UserEntity("1","Example",new ArrayList<>(Arrays.asList("facebook", "twitter")))));

        UserDto actualUser = userService.deleteNetwork("1", "twitter");
        Assert.assertEquals("1",actualUser.getId());
        Assert.assertEquals("Example",actualUser.getTelegramId());
        Assert.assertEquals(1,actualUser.getAllowedSocialNetworks().size());
        Assert.assertFalse(actualUser.getAllowedSocialNetworks().contains("twitter"));
        Assert.assertTrue(actualUser.getAllowedSocialNetworks().contains("facebook"));

        try {
            userService.deleteNetwork("4","facebook");
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:4 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getNetworks() {
        Mockito.when(userDaoMock.findById("1")).thenReturn(java.util.Optional.of(
                new UserEntity("1","Example",new ArrayList<>(Arrays.asList("facebook", "twitter")))));

        List<String> networks = userService.getNetworks("1");
        Assert.assertTrue(networks.contains("facebook"));
        Assert.assertTrue(networks.contains("twitter"));
        Assert.assertEquals(2,networks.size());



        try {
            userService.getNetworks("4");
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:4 not found",e.getReason());
        }
    }
}