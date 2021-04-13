package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.Visitor;
import lt.irmantasm.nfqtask.service.InMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Repository
public class CustomRepository {

    @Autowired
    DatabaseClient databaseClient;


    String visitsMapSql = "SELECT v.id, v.visit_time, " +
            "concat(s.id, '-' ,c.id) as SpecIdCustId, " +
            "concat(c.first_name, ' ', c.last_name) as custfl, " +
            "concat(s.first_name, ' ', s.last_name) as specfl, v.visit_duration, " +
            "v.serial  " +
            "from visits v JOIN " +
            "customers c ON v.customer_id = c.id JOIN " +
            "specialists s ON v.specialist_id = s.id;";

    public void getVisitsValues(){
        databaseClient.sql(visitsMapSql)
                .map(row -> {
                    return  new Visitor((row.get(0)), (row.get(1)), (row.get(2)), (row.get(3)),
                                    (row.get(4)), (row.get(5)), (row.get(6)));
                })
                .all()
                .delaySubscription(Duration.ofSeconds(7))
                .subscribe(visitor -> {
                    boolean added = InMemoryService.getVisistorsSet().add(visitor);
                });
    }
}
