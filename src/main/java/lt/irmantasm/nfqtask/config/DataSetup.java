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

//    @PostConstruct
//    private void setSession() {
//        UUID uuid = UUID.randomUUID();
//        long now = System.currentTimeMillis();
//        String partial = uuid.toString().split("-")[4];
//        mySession.setSession(partial);
//        final List<Visitor> mmap = new ArrayList<>();
//        Flux.interval(Duration.ofSeconds(60))
//                .map(v-> {
//                    UUID uuid1 = UUID.randomUUID();
//                    String partial1 = uuid1.toString().split("-")[4];
//                    mySession.setSession(partial1);
//                    return "Ok";
//                }).subscribe();
//    }

    @PostConstruct
    private void populateInMemoryVisitsMap() {
        Mono.just("start")
                .delaySubscription(Duration.ofSeconds(3))
                .subscribe();
        customRepository.getVisitsValues();
    }


    @PostConstruct
    private void visitsCleanupTwo() {
        final List<Visitor> mmap = new ArrayList<>();
        Flux.interval(Duration.ofSeconds(60))
                .switchMap((aLong -> Flux.fromIterable(mySession.getVisitMap().entrySet())))
                .map(entry -> {
                    entry.getValue().entrySet().removeIf(visitorEntry -> Instant.ofEpochMilli(visitorEntry.getKey()).plusSeconds(Duration.ofMinutes(15).getSeconds()).toEpochMilli() < System.currentTimeMillis());
                    return "Ok";
                })
                .subscribe();
        Flux.interval(Duration.ofSeconds(60))
                .switchMap((aLong -> Flux.fromIterable(mySession.getVisitMap().entrySet())))
                .flatMap(entry -> Flux.fromIterable(entry.getValue().entrySet())
                        .filter(lEntry -> {
                            return Instant.ofEpochMilli(lEntry.getKey()).plusSeconds(Duration.ofMinutes(15).getSeconds()).toEpochMilli() < System.currentTimeMillis();
                        })
                        .map(lentry -> lentry.getValue().getVisitId())
                )
                .flatMap(id -> visitsRepo.deleteById(id))
                .subscribe();
    }

}
