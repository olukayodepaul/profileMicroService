package dart.dartProfile.utilities;

import dart.dartProfile.darts_app.entity.ProfileDbModel;
import lombok.*;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaveAndUpdateProfileResponse {
    private Boolean status;
    private String error;
    private ProfileDbModel users;
}
