package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "auth_group")
public class AuthGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String email;
    private String authGroup;


    public AuthGroup(String email, String authGroup) {
        this.email = email;
        this.authGroup = authGroup;
    }
}
