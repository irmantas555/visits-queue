package lt.irmantasm.nfqtask.config;

import lt.irmantasm.nfqtask.model.Visitor;
import lt.irmantasm.nfqtask.repositories.CustomRepository;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import lt.irmantasm.nfqtask.service.MySession;
import lt.irmantasm.nfqtask.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Component
public class DataSetup {
    @Autowired
    MySession mySession;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    CustomRepository customRepository;


    @Autowired
    UtilService utilService;

    @PostConstruct
    private void populateInMemoryVisitsMap() {
        Mono.just("start")
                .delaySubscription(Duration.ofSeconds(25))
                .subscribe();
        customRepository.getVisitsValues();
    }


    @PostConstruct
    private void visitsCleanupTwo() {
        final List<Visitor> mmap = new ArrayList<>();
        Flux.interval(Duration.ofSeconds(30))
                .switchMap((aLong -> Flux.fromIterable(mySession.getVisitMap().entrySet())))
                .map(entry -> {
                    Long nowPlus15Min = System.currentTimeMillis() + 15 * 60 * 1000; //current time + visit time(constant 15 min)
                    TreeSet set = entry.getValue();
                    Iterator iterator = set.iterator();

                    while (iterator.hasNext()) {
                        Visitor visitor = (Visitor) iterator.next();
                        if (visitor.getVisitTime() < nowPlus15Min) {
//                            System.out.println("Removing visistor with visit time: " + utilService.getVisitTime(visitor.getVisitTime()));
                            visitsRepo.deleteById(visitor.getVisitId()).subscribe();
                            iterator.remove();
                        }
                    }
                    return "Ok";
                }).subscribe();
    }

}
