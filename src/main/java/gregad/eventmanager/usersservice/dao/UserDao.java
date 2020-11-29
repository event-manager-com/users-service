package gregad.eventmanager.usersservice.dao;

import gregad.eventmanager.usersservice.model.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Greg Adler
 */
public interface UserDao extends MongoRepository<UserEntity,Long> {
}
