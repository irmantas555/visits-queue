package lt.irmantasm.nfqtask.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MyVisit extends Visitor {
    private String timeLeft;
    private String visitTimeString;

    public MyVisit(Visitor visitor, String timeLeft, String visitTimeString) {
        super(visitor.getVisitId(), visitor.getVisitTime(), visitor.getSpecIdCustId(),
                visitor.getSpecFullName(), visitor.getCustFullName(), visitor.getVisitDuration(),
                visitor.getSerial(), visitor.getIntVisitStatus());
        this.timeLeft = timeLeft;
        this.visitTimeString = visitTimeString;
    }

    public MyVisit(String timeLeft, String visitTimeString) {
        this.timeLeft = timeLeft;
        this.visitTimeString = visitTimeString;
    }

    public MyVisit(Object o, Object o1, Object o2, Object o3, Object o4, Object o5, Object o6, String timeLeft, String visitTimeString) {
        super(o, o1, o2, o3, o4, o5, o6);
        this.timeLeft = timeLeft;
        this.visitTimeString = visitTimeString;
    }
}
