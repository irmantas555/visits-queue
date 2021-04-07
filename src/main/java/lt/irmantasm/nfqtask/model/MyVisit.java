package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MyVisit {
    private long visitId;
    private long longTimestamp;
    private String serial;
    private String visitTime;
    private String timeLeft;
    private String specFullName;
    private String custFullName;
    private int intVisitStatus;
}
