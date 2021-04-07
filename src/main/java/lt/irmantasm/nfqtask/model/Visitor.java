package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Visitor {
    private long visitId;
    private String specIdCustId; //specialistId-customerId
    private String firstName;
    private String lastName;
    private String specFirsLastName;
    private int visitDuration;
    private String serial;
    private int intVisitSatus;  //0 -not satrted, 1 - started, 2- finished


    public Visitor(Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6) {
        this.visitId = (long) o;
        this.specIdCustId = (String) o1;
        this.firstName = (String) o2;
        this.lastName = (String) o3;
        this.specFirsLastName = (String) o4;
        this.visitDuration = (int) o5;
        this.serial = (String) o6;
        intVisitSatus = 0;
    }

}
