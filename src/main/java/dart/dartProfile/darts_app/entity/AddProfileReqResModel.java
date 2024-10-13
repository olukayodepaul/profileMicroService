package dart.dartProfile.darts_app.entity;

import lombok.*;


import java.time.LocalDateTime;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class AddProfileReqResModel {
    private String uuid;
    private String first_name;
    private String last_name;
    private String phone_number;
    private String organisation_id;
    private String date_of_birth;
    private String gender;
    private String bio;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
