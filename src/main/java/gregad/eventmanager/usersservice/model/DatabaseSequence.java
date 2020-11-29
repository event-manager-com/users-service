package gregad.eventmanager.usersservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @author Greg Adler
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "user_sequences")
public class DatabaseSequence {
    @Id
    private int id;
    private long seq;

}

