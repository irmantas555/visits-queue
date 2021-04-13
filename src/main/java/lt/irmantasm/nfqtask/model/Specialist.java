package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "specialists")
public class Specialist extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;


    public Specialist(String email, String first_name, String last_name, String password) {
        super(email,first_name, last_name, password);
    }

    public Specialist(String email, String password) {
        super(email, password);
    }

    @Override
    public String toString() {
        return " Specialist id=  " + id + "," +
                super.toString();
    }
}
