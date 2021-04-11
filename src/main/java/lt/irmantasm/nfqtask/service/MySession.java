package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.*;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MySession {

    @Autowired
    UtilService utilService;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    SinkService service;

    private static String session;

    //    SortedSet<Visitor> visitors = Collections.synchronizedSortedSet(new TreeSet<>(iDComparator));
    private static Map<Long, TreeSet<Visitor>> visitsMap = new HashMap<>();
    private static Map<Long, TreeSet<Visitor>> custVisitsMap = new HashMap<>();
    private static NavigableSet<Visitor> lastSet = new TreeSet<>();


    public MySession() {

    }



    public Map<Long, TreeSet<Visitor>> getVisitMap() {
        return visitsMap;
    }

    public static String getSession() {
        return session;
    }

    public static void setSession(String ses) {
        session = ses;
    }

    public Long getLAstTimeForSpecialist(Long specId) {
        return (visitsMap.get(specId).last().getVisitTime());
    }

    public boolean existSpecialistAndHisVisits(Long specId) {
        return visitsMap.containsKey(specId) && visitsMap.get(specId).size() > 0;
    }

    @PostConstruct
    public void getMyVisitList() {
        AtomicInteger integer = new AtomicInteger();
        Flux.interval(Duration.ofSeconds(5))
                .switchMap(aLong -> (Flux.fromIterable(visitsMap.entrySet())))
                .flatMap(longTreeSetEntry -> {
                    return Flux.fromIterable(longTreeSetEntry.getValue())
                            .map(visitor -> utilService.getVisitFromVisitor(visitor))
                            .take(1);
                })
//                .reduce(new TreeSet<Visitor>(), (set, visitor) -> {
//                    int i = integer.incrementAndGet();
//                    if (set.c)
//                })
                .doOnNext(myVisit -> System.out.println("sending visit with id: " + myVisit.getVisitId()))
                .map(myVisit -> {
                    service.emitMyNext(myVisit);
                    return "Ok";
                })
                .subscribe();
    }

    public Mono<Void> addVisitorToCollections(Visitor visitor) {
        Long specId = Long.getLong(visitor.getSpecIdCustId().split("-")[0]);
        Long custId = Long.getLong(visitor.getSpecIdCustId().split("-")[1]);
        return Flux.just(visitor)
                .map(visitor1 -> {
                    if (visitsMap.containsKey(specId)) {
                        visitsMap.get(specId).add(visitor);
                    } else {
                        TreeSet<Visitor> set = new TreeSet<>();
                        set.add(visitor);
                        visitsMap.put(specId, set);
                    }
                    return visitor1;
                })
                .map(visitor1 -> {
                    if (custVisitsMap.containsKey(custId)) {
                        custVisitsMap.get(custId).add(visitor);
                    } else {
                        TreeSet<Visitor> set = new TreeSet<>();
                        set.add(visitor);
                        custVisitsMap.put(custId, set);
                    }
                    return visitor1;
                })
                .map(visitor1 -> lastSet.add(visitor))
                .then();
    }



    public Mono<Void> deleteVisitEntryById(Long id) {
        Visitor visitor = new Visitor(id);
        return Flux.fromIterable(visitsMap.entrySet())
                .handle((entry, sink) -> {
                    if (entry.getValue().contains(visitor)) {
                        entry.getValue().remove(visitor);
                        sink.complete();
                    }
                })
                .single()
                .thenMany(Flux.fromIterable(custVisitsMap.entrySet()))
                .handle((entry, sink) -> {
                    if (entry.getValue().contains(visitor)) {
                        entry.getValue().remove(visitor);
                        sink.complete();
                    }
                })
                .single()
                .then(Mono.just(lastSet))
                .map(set -> {
                    if (set.contains(visitor)) {
                        set.remove(visitor);
                    }
                    return "Ok";
                })
                .flatMap(s -> visitsRepo.deleteById(id));
    }

    public Mono<Void> setVisitStatus(Long id, int status) {
        return Flux.fromIterable(this.getVisitMap().entrySet())
                .handle((longTreeSetEntry, sink) -> {
                    Flux.fromIterable(longTreeSetEntry.getValue())
                            .filter(visitor -> visitor.getVisitId() == id)
                            .take(1)
                            .subscribe(visitor -> {
                                visitor.setIntVisitSatus(status);
                                sink.complete();
                            });
                })
                .then();
    }


    public Mono<Void> printVisitors() {
        return Flux.fromIterable(visitsMap.entrySet())
                .map(ent -> {
                    Flux.fromIterable(ent.getValue())
                            .doOnNext(visitor -> System.out.println(visitor)).subscribe();
                    return "Ok";
                })
                .then();
    }
}
