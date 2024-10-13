package dart.dartProfile.darts_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "addresses")
public class AddressDbModel {

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
    private boolean status;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}
