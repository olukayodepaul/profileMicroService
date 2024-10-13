package dart.dartProfile.darts_app.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "profiles")
public class ProfileDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private UUID uuid;
    private String first_name;
    private String last_name;
    private String phone_number;
    private String organisation_id;
    private LocalDate date_of_birth;
    private String gender;
    private String bio;
    private boolean status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
