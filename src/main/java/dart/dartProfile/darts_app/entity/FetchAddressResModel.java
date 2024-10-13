package dart.dartProfile.darts_app.entity;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class FetchAddressResModel {

    private boolean status;
    private String message;
    private List<Address> data;

    @Builder
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private int id;
        private String type;
        private String address_line1;
        private String address_line2;
        private String street;
        private String city;
        private String state;
        private String zip;
        private String country;
        private boolean status;
        private LocalDateTime updated_at;
        private LocalDateTime created_at;

    }

}

