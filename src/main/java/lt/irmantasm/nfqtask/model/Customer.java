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
@Table(value = "customers")
public class Customer extends User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    public Customer(String email, String first_name, String last_name, String password) {
        super(email, first_name, last_name, password);
    }

    @Override
    public String toString() {
        return " Customer id=  " + id +
                super.toString();
    }
}
