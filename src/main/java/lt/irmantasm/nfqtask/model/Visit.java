package lt.irmantasm.nfqtask.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import javax.persistence.*;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(value = "visits")
public class Visit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private long specialistId;
    private long customerId;
    private long visitTime;
    private int visitDuration;
    private String serial;

    public Visit(long specialistId, long customerId, long visitTime, int visiDuration, String serial) {
        this.specialistId = specialistId;
        this.customerId = customerId;
        this.visitTime = visitTime;
        this.visitDuration = visiDuration;
        this.serial = serial;
    }

}
