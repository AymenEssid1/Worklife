package tn.esprit.springfever.entities;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Entity
@ToString
@Data
public class Equipe implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter

    private int Equipeid;
    private String nameEquipe;



    @OneToMany(mappedBy = "equipe")
    private List<User> users;


}