package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public abstract class User {
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

