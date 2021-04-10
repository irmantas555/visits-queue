package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Visitor {
    private long visitId;
    private long visiteTime;
    private String specIdCustId; //specialistId-customerId
    private String firstName;
    private String lastName;
    private String specFirsLastName;
    private int visitDuration;
    private String serial;
    private int intVisitSatus;  //0 -not satrted, 1 - started, 2- finished


    public Visitor(Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        this.visitId = (long) o;
        this.visiteTime = (long) o7;
        this.specIdCustId = (String) o1;
        this.firstName = (String) o2;
        this.lastName = (String) o3;
        this.specFirsLastName = (String) o4;
        this.visitDuration = (int) o5;
        this.serial = (String) o6;
        intVisitSatus = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visitor visitor = (Visitor) o;

        if (visitId != visitor.visitId) return false;
        if (intVisitSatus != visitor.intVisitSatus) return false;
        if (firstName != null ? !firstName.equals(visitor.firstName) : visitor.firstName != null) return false;
        if (lastName != null ? !lastName.equals(visitor.lastName) : visitor.lastName != null) return false;
        return specFirsLastName != null ? specFirsLastName.equals(visitor.specFirsLastName) : visitor.specFirsLastName == null;
    }

    @Override
    public int hashCode() {
        return (int) (visitId ^ (visitId >>> 32));
    }
}
