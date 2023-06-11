package tn.esprit.springfever.Services.Interface;

import tn.esprit.springfever.entities.Reclamation;
import tn.esprit.springfever.entities.User;

import java.util.List;

public interface IClaimService {
    public Reclamation AddClaim(Reclamation reclamation, User user);
    public Reclamation treatClaim(Integer id, String descision);
    public List<Reclamation> getClaimsByUser(long id);
}
