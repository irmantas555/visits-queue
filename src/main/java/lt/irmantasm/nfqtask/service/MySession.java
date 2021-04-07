package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Service
public class MySession {

    @Autowired
    UtilService utilService;


    private static Map<Long, Specialist> specialistMap = new HashMap();

    private static Map<Long, Customer> customerMap = new HashMap();

    private static String session;

    private static Map<Long, TreeMap<Long, Visitor>> visitsMap = new HashMap<>();

    public MySession() {
    }

    public static void addCustomer(Customer customer) {
        customerMap.put(customer.getId(), customer);
    }

    public static Customer getCustomerById(Long id) {
        return customerMap.get(id);
    }
    public static Specialist getSpecialsitById(Long id) {
        return specialistMap.get(id);
    }

    public static void addSpecialist(Specialist specialist) {
        specialistMap.put(specialist.getId(), specialist);
    }

    public static Map<Long, Specialist> getSpecialistMap() {
        return specialistMap;
    }

    public static void setSpecialistMap(Map<Long, Specialist> specialistMap) {
        MySession.specialistMap = specialistMap;
    }

    public static Map<Long, Customer> getCustomerMap() {
        return customerMap;
    }

    public static void setCustomerMap(Map<Long, Customer> customerMap) {
        MySession.customerMap = customerMap;
    }


    public Map<Long, TreeMap<Long, Visitor>> getVisitMap() {
        return visitsMap;
    }

    public static String getSession() {
        return session;
    }

    public static void setSession(String ses) {
        session = ses;
    }

    public Visitor fromVisit(Visit visit, Specialist specialist, Customer customer) {
        Visitor visitor = new Visitor();
        visitor.setVisitId(visit.getId());
        visitor.setSpecIdCustId(specialist.getId() + "-" + customer.getId());
        visitor.setFirstName(customer.getFirstName());
        visitor.setLastName(customer.getLastName());
        visitor.setVisitDuration(15);
        visitor.setSpecFirsLastName(specialist.getFirstName() + " " + specialist.getLastName());
        visitor.setIntVisitSatus(0);
        visitor.setSerial(visit.getSerial());
        return visitor;
    }

    public Flux<MyVisit> getMyVisitList() {
        return Flux.fromIterable(visitsMap.entrySet())
                .flatMap(longTreeMapEntry -> {
                    return Flux.fromIterable(longTreeMapEntry.getValue().entrySet())
                            .map(longVisitorEntry ->
                                    Tuples.of(longTreeMapEntry.getKey(), longVisitorEntry.getKey(), longVisitorEntry.getValue()));
                })
                .map(tuple -> {
                    String visistTime = utilService.getVisitTime(tuple.getT2());
                    String timeLeft = utilService.getTimeLeft(tuple.getT2());
                    return new MyVisit(tuple.getT3().getVisitId(), tuple.getT2(), tuple.getT3().getSerial(), visistTime, timeLeft,
                            tuple.getT3().getFirstName() + " " + tuple.getT3().getLastName(),
                            tuple.getT3().getSpecFirsLastName(), tuple.getT3().getIntVisitSatus());
                });
    }

    public Flux<MyVisit> getMyVisitListStarted() {
       return getMyVisitList().filter(myVisit -> myVisit.getIntVisitStatus() > 0);
    }

    public Flux<MyVisit> getMyVisitListSorted() {
        return getMyVisitList().filter(myVisit -> myVisit.getIntVisitStatus() == 0)
                .sort(Comparator.comparing(MyVisit::getLongTimestamp))
                .take(5);
    }

    public Mono<Void> deleteVisitEntryById(Long id) {
        return Flux.fromIterable(this.getVisitMap().entrySet())
                .map(longTreeMapEntry -> {
                    longTreeMapEntry.getValue().entrySet().removeIf(entry -> entry.getValue().getVisitId() == id);
                    return "OK";
                })
                .then();
    }

    public Mono<Void> setVisitStatus(Long id, int status) {
        return Flux.fromIterable(this.getVisitMap().entrySet())
                .map(longTreeMapEntry -> {
                    Flux.fromIterable(longTreeMapEntry.getValue().entrySet())
                            .filter(longVisitorEntry -> longVisitorEntry.getValue().getVisitId() == id)
                            .take(1)
                            .map(entry -> {
                                entry.getValue().setIntVisitSatus(status);
                                return "Ok";
                            }).subscribe();
                    return "Ok";
                })
                .then();
    }

    public Mono<Void> printVisitors() {
        return Flux.fromIterable(visitsMap.entrySet())
                .map(ent -> {
                    Flux.fromIterable(ent.getValue().entrySet())
                            .doOnNext(longVisitorEntry -> System.out.println(longVisitorEntry.getValue())).subscribe();
                    return "Ok";
                })
                .then();
    }
}

//    Iterator<Map.Entry<Long, TreeMap<Long, Visitor>>> iterator = visitsMap.entrySet().iterator();
//    Map.Entry<Integer, String> entry;