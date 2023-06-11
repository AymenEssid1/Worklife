package tn.esprit.springfever.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@ToString
@Data
public class Reclamation implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int ClaimId;
    private String Subject;
    private String Contenu;
    private boolean EtatClaim;
    private Date DateClaim;
    private  String Decision;

    private TypeClaim typeClaim;
    @ManyToMany(mappedBy="claims", cascade = CascadeType.ALL)
    @JsonIgnore

    private List<User> users = new ArrayList<>()  ;

}
