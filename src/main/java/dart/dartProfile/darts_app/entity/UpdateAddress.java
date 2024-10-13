package dart.dartProfile.darts_app.entity;


import lombok.*;
import java.util.List;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAddress {
    private Boolean status;
    private String error;
    private AddressDbModel address;
}
