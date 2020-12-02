package gregad.eventmanager.usersservice.dao;

import gregad.eventmanager.usersservice.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Greg Adler
 */
@Repository
public interface UserDao extends MongoRepository<UserEntity,String> {
}
