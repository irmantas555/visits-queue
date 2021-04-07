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

@Repository
public class CustomRepository {

    @Autowired
    DatabaseClient databaseClient;

    @Autowired
    MySession mySession;

    String visitsMapSql = "SELECT c.id, (s.id  || '-' || c.id) as SpecIdCustId, c.first_name, c.last_name, s.id, " +
            "s.first_name || ' ' || s.last_name as specfl, v.id, v.visit_time, v.visit_duration, " +
            "v.serial " +
            "from visits v JOIN " +
            "customers c ON v.customer_id = c.id JOIN " +
            "specialists s ON v.specialist_id = s.id;";

    public void getVisitsValues(){
        databaseClient.sql(visitsMapSql)
                .map(row -> {
                    Long visitTime = (Long) row.get(7);
                    Visitor visitor =
                            new Visitor((row.get(6)), (row.get(1)), (row.get(2)), (row.get(3)),
                                    (row.get(5)), (row.get(8)), (row.get(9)));
                    Long specId = (Long) row.get(4);
                    return Tuples.of(specId, visitTime, visitor);
                })
                .all()
                .delaySubscription(Duration.ofSeconds(5))
                .doOnNext(objects -> System.out.println(objects))
                .map(tuple -> {
                    if (mySession.getVisitMap().containsKey(tuple.getT1())) {
                        Map map = mySession.getVisitMap().get(tuple.getT1());
                        if (null != map) {
                            map.put(tuple.getT2(), tuple.getT3());
                        } else {
                            TreeMap<Long, Visitor> newMap = new TreeMap();
                            newMap.put(tuple.getT2(), tuple.getT3());
                            mySession.getVisitMap().put(tuple.getT1(), newMap);
                        }
                    } else {
                        TreeMap<Long, Visitor> newMap = new TreeMap();
                        newMap.put(tuple.getT2(), tuple.getT3());
                        mySession.getVisitMap().put(tuple.getT1(), newMap);
                    }
                    return tuple;
                })
                .thenMany(Flux.fromIterable(mySession.getVisitMap().entrySet()))
//                .doOnNext(longMapEntry -> System.out.println(longMapEntry))
                .subscribe();
    }
}
