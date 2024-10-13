package dart.dartProfile.darts_app.entity;

import lombok.*;
import java.util.List;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveAddress {
    private Boolean status;
    private String error;
    private List<AddressDbModel> address;
}
