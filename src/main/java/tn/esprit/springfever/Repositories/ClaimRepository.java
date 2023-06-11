package tn.esprit.springfever.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import tn.esprit.springfever.entities.Reclamation;
import tn.esprit.springfever.entities.User;

import java.util.List;

@EnableJpaRepositories
public interface ClaimRepository  extends JpaRepository<Reclamation,Integer> {
    public List<Reclamation> findByUsersUserid(long id);
}
