package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.Visitor;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

@Repository
public class CustomRepository {

    @Autowired
    DatabaseClient databaseClient;

    @Autowired
    MySession mySession;


    String visitsMapSql = "SELECT v.id, concat(s.id, '-' ,c.id) as SpecIdCustId, " +
            "c.first_name, c.last_name, " +
            "concat(s.first_name, ' ', s.last_name) as specfl, v.visit_duration, " +
            "v.serial, v.visit_time, s.id " +
            "from visits v JOIN " +
            "customers c ON v.customer_id = c.id JOIN " +
            "specialists s ON v.specialist_id = s.id;";

    public void getVisitsValues(){
        databaseClient.sql(visitsMapSql)
                .map(row -> {
                    Long visitTime = (Long) row.get(7);
                    Long specId = (Long) row.get(8);
                    Visitor visitor =
                            new Visitor((row.get(0)), (row.get(1)), (row.get(2)), (row.get(3)),
                                    (row.get(4)), (row.get(5)), (row.get(6)), (row.get(7)));
                    return Tuples.of(specId, visitor);
                })
                .all()
                .delaySubscription(Duration.ofSeconds(5))
                .map(tuple -> {
                    if (mySession.getVisitMap().containsKey(tuple.getT1())) {
                        TreeSet set = mySession.getVisitMap().get(tuple.getT1());
                        if (null != set) {
                            set.add(tuple.getT2());
                        } else {
                            TreeSet<Visitor> newSet = new TreeSet<>();
                            newSet.add(tuple.getT2());
                            mySession.getVisitMap().put(tuple.getT1(), newSet);
                        }
                    } else {
                            TreeSet<Visitor> newSet = new TreeSet<>();
                        newSet.add(tuple.getT2());
                        mySession.getVisitMap().put(tuple.getT1(), newSet);
                    }
                    return tuple;
                })
//                .thenMany(Flux.fromIterable(mySession.getVisitMap().entrySet()))
//                .doOnNext(longMapEntry -> System.out.println(longMapEntry))
                .subscribe();
    }
}
