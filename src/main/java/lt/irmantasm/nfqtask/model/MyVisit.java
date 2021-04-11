package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

@Data
@NoArgsConstructor
public class MyVisit extends Visitor {
    private String timeLeft;
    private String visitTimeString;

    public MyVisit(Visitor visitor, String timeLeft, String visitTimeString) {
        super(visitor.getVisitId(), visitor.getVisitTime(), visitor.getSpecIdCustId(),
                visitor.getSpecFullName(), visitor.getCustFullName(), visitor.getVisitDuration(),
                visitor.getSerial(), visitor.getIntVisitSatus());
        this.timeLeft = timeLeft;
        this.visitTimeString = visitTimeString;
    }

    public MyVisit(String timeLeft, String visitTimeString) {
        this.timeLeft = timeLeft;
        this.visitTimeString = visitTimeString;
    }

    public MyVisit(Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, Object o7, String timeLeft, String visitTimeString) {
        super(o, o1, o2, o3, o4, o5, o6, o7);
        this.timeLeft = timeLeft;
        this.visitTimeString = visitTimeString;
    }
}
