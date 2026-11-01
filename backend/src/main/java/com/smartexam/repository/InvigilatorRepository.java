
package com.smartexam.repository;

import com.smartexam.entity.Invigilator;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface InvigilatorRepository
        extends JpaRepository<Invigilator, String> {

    Optional<Invigilator> findByEmail(String email);

    boolean existsByEmail(String email);
}
