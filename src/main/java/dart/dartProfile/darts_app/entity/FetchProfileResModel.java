package dart.dartProfile.darts_app.entity;


import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class FetchProfileResModel {

    private boolean status;
    private String message;
    private Profile profile;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Profile {
        private String first_name;
        private String last_name;
        private String phone_number;
        private LocalDate date_of_birth;
        private String gender;
        private String bio;
        private boolean status;
        private LocalDateTime created_at;
        private LocalDateTime updated_at;
    }
}