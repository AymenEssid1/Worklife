package tn.esprit.springfever.entities;



import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.io.Serializable;



@Entity
@ToString
@Data
public class Priority implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter
    @Setter
    private int PriorityId;

    private int VacationDaysDisp;
    private int NbMaladie;
    private int NbMaternite;
    private int NbVacation;
    private int NbEmergency;
    private int NbDeath;
    private int NbMarriage;





    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;




}
