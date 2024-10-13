package dart.dartProfile.darts_app.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Data
@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
@RedisHash("profiles")
public class ProfileCacheModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID uuid;
    private String first_name;
    private String last_name;
    private String phone_number;
    private LocalDate date_of_birth;
    private String organisation_id;
    private String gender;
    private String bio;
    private boolean status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
