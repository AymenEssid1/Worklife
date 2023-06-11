package tn.esprit.springfever.Services.Implementation;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import tn.esprit.springfever.Repositories.ClaimRepository;
import tn.esprit.springfever.Repositories.UserRepo;
import tn.esprit.springfever.Services.Interface.IClaimService;
import tn.esprit.springfever.entities.Reclamation;
import tn.esprit.springfever.entities.User;

import java.util.Date;
import java.util.List;


@Slf4j
@Service
public class ClaimService implements IClaimService {
    @Autowired
    ClaimRepository claimRepository;

    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    UserRepo userRepository;


    public Reclamation AddClaim(Reclamation reclamation, User user){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("New claim submitted");
        message.setText("A new claim has been submitted.");
        message.setTo("shaimaaloulou662@gmail.com"); // to change with the email of the user
        javaMailSender.send(message);
        log.info("claim was successfully added !");
        //return claimRepository.save(reclamation);

        reclamation.getUsers().add(user);
        user.getClaims().add(reclamation);
        userRepository.save(user);
        return claimRepository.save(reclamation);

    }

    public Reclamation treatClaim(Integer id, String descision) {
        Reclamation
                claim = claimRepository.findById(id).orElse(null);
        if (claim != null) {
            claim.setDecision(descision);
            claim.setDateClaim(new Date());
            claim.setEtatClaim(true);
            claimRepository.save(claim);
            log.info("claim was treated ");

        } else {
            log.info("claim not found ");
        }
        return claim;
    }

    public List<Reclamation> getClaimsByUser(long id) {
        return claimRepository.findByUsersUserid(id);

    }

}
