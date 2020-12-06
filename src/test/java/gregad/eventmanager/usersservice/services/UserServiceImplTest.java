package gregad.eventmanager.usersservice.services;

import gregad.eventmanager.usersservice.UsersServiceApplication;
import gregad.eventmanager.usersservice.dao.UserDao;
import gregad.eventmanager.usersservice.dto.UserDto;
import gregad.eventmanager.usersservice.model.UserEntity;
import gregad.eventmanager.usersservice.services.user_service.UserService;
import gregad.eventmanager.usersservice.services.user_service.UserServiceImpl;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ResponseStatusException;

/**
 * @author Greg Adler
 */

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = UsersServiceApplication.class)
class UserServiceImplTest {
    private UserEntity userEntity = 
           new UserEntity(1,"Greg");

    private UserService userService;
    private UserDao userDaoMock;
    @BeforeEach
    public void init(){
         userDaoMock = Mockito.mock(UserDao.class);
         userService=new UserServiceImpl(userDaoMock);
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////    
    @Test
    void addUser() {
        Mockito.when(userDaoMock.save(userEntity)).thenReturn(userEntity);
        
        
        UserDto result = userService.addUser(1,"Greg");
        Assert.assertEquals(1,result.getId());
        Assert.assertEquals("Greg",result.getName());
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void getUser() {
        Mockito.when(userDaoMock.findById(3)).
                thenReturn(java.util.Optional.of(
                        new UserEntity(3,"Moshe")));

        UserDto result = userService.getUser(3);
        Assert.assertEquals(3,result.getId());
        Assert.assertEquals("Moshe",result.getName());

        try {
            userService.getUser(2);
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:2 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void deleteUser() {
        Mockito.when(userDaoMock.findById(1)).thenReturn(java.util.Optional.ofNullable(userEntity));

        UserDto userDto = userService.deleteUser(1);
        Assert.assertEquals(1,userDto.getId());
        Assert.assertEquals("Greg",userDto.getName());
        try {
            userService.deleteUser(2);
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:2 not found",e.getReason());
        }
    }
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Test
    void updateUser() {
        Mockito.when(userDaoMock.findById(3)).
                thenReturn(java.util.Optional.of(
                        new UserEntity(3,"Ron")));

        UserDto result = userService.updateUser(new UserDto(3,"new name"));
        Assert.assertEquals(3,result.getId());
        Assert.assertEquals("new name",result.getName());


        try {
            userService.updateUser(new UserDto(22,"ff"));
        } catch (ResponseStatusException e) {
            Assert.assertEquals(ResponseStatusException.class,e.getClass());
            Assert.assertEquals(HttpStatus.NOT_FOUND,e.getStatus());
            Assert.assertEquals("User with id:22 not found",e.getReason());
        }
    }

}