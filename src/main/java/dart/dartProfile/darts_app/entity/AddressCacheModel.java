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
@Setter
@AllArgsConstructor
@RedisHash("addresses")
public class AddressCacheModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID uuid;
    private String type;
    private String organisation_id;
    private String address_line1;
    private String address_line2;
    private String street;
    private String city;
    private String state;
    private String zip;
    private String country;
    private String status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
