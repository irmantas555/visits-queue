package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.*;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;

@Service
public class InMemoryService {

    @Autowired
    UtilService utilService;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    SinkService service;

    public static Set<Specialist> specialistSet = new HashSet<>();
    public static Set<Customer> customerSet = new HashSet<>();

    static Comparator<Visitor> vistorTimeComparator = Comparator.comparing(Visitor::getVisitTime).reversed();

    public static Set<Visitor> getVisistorsSet() {
        return visistorsSet;
    }

    public static void setVisistorsSet(Set<Visitor> visistorsSet) {
        InMemoryService.visistorsSet = visistorsSet;
    }

    private static Set<Visitor> visistorsSet = new HashSet<>();

    @PostConstruct
    public void getMyVisitList() {
        Flux.interval(Duration.ofSeconds(5))
                .flatMap(aLong -> Flux.fromIterable(visistorsSet)
                        .sort(vistorTimeComparator)
                        .filter(visitor -> visitor.getIntVisitStatus() != 1)
                        .take(5)
                )
                .map(visitor -> utilService.getVisitFromVisitor(visitor))
                .map(visit -> {
                    service.emitMyNext(visit);
                    return visit;
                }).subscribe();
        Flux.interval(Duration.ofSeconds(5))
                .flatMap(aLong -> Flux.fromIterable(visistorsSet))
                .filter(visitor -> visitor.getIntVisitStatus() == 1)
                .doOnNext(System.out::println)
                .map(visitor -> utilService.getVisitFromVisitor(visitor))
                .map(visit -> {
                    service.emitMyNextCurrent(visit);
                    return visit;
                })
            .subscribe(v->{});
    }

    public static void addVisitorToSet(Visitor visitor) {
        boolean add = visistorsSet.add(visitor);
    }

    public static Specialist getSpecByEmail(String email) {
        for (Specialist specialist : specialistSet) {
            if (specialist.getEmail().equals(email)) {
                return specialist;
            }
        }
        return null;
    }

    public static Specialist getSpecById(Long id) {
        for (Specialist specialist : specialistSet) {
            if (specialist.getId() == id) {
                return specialist;
            }
        }
        return null;
    }

    public static Customer getCustByEmail(String email) {
        for (Customer customer : customerSet) {
            if (customer.getEmail().equals(email)) {
                return customer;
            }
        }
        return null;
    }

    public static Customer getCustById(Long id) {
        for (Customer customer : customerSet) {
            if (customer.getId() == id) {
                return customer;
            }
        }
        return null;
    }

    public static Mono<Void> deleteVisitEntryById(Long id) {
        Visitor visitor = new Visitor(id);
        visistorsSet.remove(visitor);
        return Mono.empty();
    }

    public static Mono<Void> setVisitStatus(Long visitId, int status) {
        return Flux.fromIterable(visistorsSet)
                .filter(visitor -> visitor.getVisitId() == visitId)
                .single()
                .map(visitor -> {
                    visitor.setIntVisitStatus(status);
                    return "Ok";
                })
                .doOnNext(System.out::println)
                .then();
    }

    public static Flux<Visitor> getVisitorsForCustomer(Long customerId) {
        return Flux.fromIterable(visistorsSet)
                .filter(visitor -> Long.parseLong(visitor.getSpecIdCustId().split("-")[1]) ==  customerId);
    }

    public static Flux<Visitor> getVisitorsForSpecialist(Long specialistId) {
        return Flux.fromIterable(visistorsSet)
                .filter(visitor -> Long.parseLong(visitor.getSpecIdCustId().split("-")[0]) == specialistId);
    }


    public static Mono<Void> printVisitors() {
        Flux.fromIterable(visistorsSet)
                .subscribe(v -> System.out.println(v));
        return Mono.empty();
    }

    public static Mono<Long> getLastTimeForSpecialist(Long specId) {
       return Flux.fromIterable(visistorsSet)
               .filter(visitor -> null != visitor.getSpecIdCustId())
               .filter(visitor -> Long.parseLong(visitor.getSpecIdCustId().split("-")[0]) == specId)
               .sort(vistorTimeComparator)
               .map(visitor -> visitor.getVisitTime())
               .take(1)
               .doOnNext(along -> System.out.println("LAST TIME" + along))
               .delayElements(Duration.ofMillis(300))
               .switchIfEmpty(Mono.just(System.currentTimeMillis())).single();

    }


}
