package dart.dartProfile.darts_app.repository;

import dart.dartProfile.darts_app.entity.AddressDbModel;
import dart.dartProfile.darts_app.entity.ProfileDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProfileRepo extends JpaRepository<ProfileDbModel, Long> {
    Optional<ProfileDbModel> findByUuid(UUID uuid);
}
