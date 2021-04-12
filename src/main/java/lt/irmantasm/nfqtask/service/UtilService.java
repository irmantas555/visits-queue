package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UtilService {
    public static Map<Integer, Integer> serialMap = new ConcurrentHashMap<>();

    public String getVisitTime(Long tStamp) {
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(tStamp), ZoneId.systemDefault()).format(myFormat);
    }

    public String getTimeLeft(Long tStamp) {
        Duration durationLeft = Duration.between(Instant.now(), Instant.ofEpochMilli(tStamp));
        long secondsLeft = durationLeft.getSeconds();
        return String.format("%d:%02d:%02d", secondsLeft / 3600, (secondsLeft % 3600) / 60, (secondsLeft % 60));
    }

    public long nextTime(long lastTime) {
        LocalDateTime localDateTime = LocalDateTime.from(Instant.ofEpochMilli(lastTime).atZone(ZoneId.systemDefault()));
        if (localDateTime.getHour() > 17) {
            lastTime = LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth() + 1, 8, 0, 0)
                    .atZone(ZoneId.systemDefault())
                    .toInstant().toEpochMilli();
            Long next =  (Instant.ofEpochMilli(lastTime).plusSeconds(Duration.ofMinutes(15).getSeconds())).toEpochMilli();
            return next;
        }
        return (Instant.ofEpochMilli(lastTime).plusSeconds(Duration.ofMinutes(15).getSeconds())).toEpochMilli();
    }

    public String getSerial(long visitTime) {
        Integer lastSerialForDay;
        LocalDateTime localDateTime = LocalDateTime.from(Instant.ofEpochMilli(visitTime).atZone(ZoneId.systemDefault()));
        DateTimeFormatter myFormat = DateTimeFormatter.ofPattern("yyMMdd");
        String datestring = localDateTime.format(myFormat);
        Integer nInt = Integer.valueOf(datestring);
        if (serialMap.containsKey(nInt)) {
            lastSerialForDay = serialMap.get(nInt);
            lastSerialForDay = lastSerialForDay + 1;
            serialMap.put(nInt, lastSerialForDay);
        } else {
            lastSerialForDay =  1;
            serialMap.put(nInt, lastSerialForDay);
        }
        String serial = String.format("%04d", lastSerialForDay);
        return serial;
    }

    public MyVisit getVisitFromVisitor(Visitor visitor) {
        return new MyVisit(visitor, getTimeLeft(visitor.getVisitTime()), getVisitTime(visitor.getVisitTime()));
    }

    public Visitor visitorFromVisit(Visit visit, Specialist specialist, Customer customer) {
        Visitor visitor = new Visitor();
        visitor.setVisitId(visit.getId());
        visitor.setVisitTime(visit.getVisitTime());
        visitor.setSpecIdCustId(specialist.getId() + "-" + customer.getId());
        visitor.setCustFullName(customer.getFirstName() + " " + customer.getLastName());
        visitor.setSpecFullName(specialist.getFirstName() + " " + specialist.getLastName());
        visitor.setVisitDuration(15);
        visitor.setSerial(visit.getSerial());
        visitor.setIntVisitStatus(0);
        return visitor;
    }

    public Visit visitFromVisitor(Visitor visitor) {
        String[] split = visitor.getSpecIdCustId().split("-");
        return new Visit(visitor.getVisitId(), Long.parseLong(split[0]), Long.parseLong(split[1]), visitor.getVisitTime(), 15, visitor.getSerial());
    }

}
