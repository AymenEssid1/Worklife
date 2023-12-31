package tn.esprit.springfever.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.springfever.entities.Image;
import tn.esprit.springfever.entities.Image;

import java.util.Optional;

public interface ImageDataRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByName(String name);
}
