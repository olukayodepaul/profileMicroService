package dart.dartProfile.darts_app.entity;

import lombok.*;

import java.util.List;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchAddressFromCacheModel {
    private Boolean status;
    private String message;
    private List<AddressCacheModel> address;
}
