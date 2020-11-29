package gregad.eventmanager.usersservice.dao;

import gregad.eventmanager.usersservice.model.DatabaseSequence;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @author Greg Adler
 */
@Repository
public interface SequenceDao extends MongoRepository<DatabaseSequence,Integer> {
}
