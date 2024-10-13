package dart.dartProfile.darts_app.entity;

import lombok.*;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FetchProfileFromCacheModel {
    private Boolean status;
    private Integer event;
    private String message;
    private ProfileCacheModel profile;
}
