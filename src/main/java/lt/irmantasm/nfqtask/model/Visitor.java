package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Comparator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Visitor implements Comparator {
    private long visitId;
    private long visitTime;
    private String specIdCustId; //specialistId-customerId
    private String specFullName;
    private String custFullName;
    private int visitDuration;
    private String serial;
    private int intVisitSatus;  //0 -not satrted, 1 - started, 2- finished


    public Visitor(Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7) {
        this.visitId = (long) o;
        this.visitTime = (long) o7;
        this.specIdCustId = (String) o1;
        this.custFullName = (String) o2 + " " + (String) o2;
        this.specFullName = (String) o4;
        this.visitDuration = (int) o5;
        this.serial = (String) o6;
        intVisitSatus = 0;
    }

    public Visitor(long visitId) {
        this.visitId = visitId;
    }

    @Override
    public int compare(Object o1, Object o2) {
        return o1.hashCode() - o2.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Visitor visitor = (Visitor) o;

        return visitId == visitor.visitId;
    }

    @Override
    public int hashCode() {
        return (int) (visitId ^ (visitId >>> 32));
    }
}
