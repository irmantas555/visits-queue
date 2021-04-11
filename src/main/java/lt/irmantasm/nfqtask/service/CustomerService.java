package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.*;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.TreeMap;
import java.util.TreeSet;

@Service
public class CustomerService {
    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    UtilService utilService;

    @Autowired
    MySession mySession;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;


    public Flux<MyVisit> getMyVisits(Long id) {
        return Flux.fromIterable(mySession.getVisitMap().entrySet())
                .flatMap(visistsEntry -> {
                   return Flux.fromIterable(visistsEntry.getValue())
                            .filter(visitor -> visitor.getSpecIdCustId().split("-")[1].equals(id.toString()))
                            .map(visitor -> utilService.getVisitFromVisitor(visitor));
                });
    }

    public void assignNew(Long specId, Long customerId) {
//        System.out.println("Spec: " + specId + "Customer: " + customerId);
        Long lastentry;
        if (mySession.existSpecialistAndHisVisits(specId)) {
            lastentry = mySession.getLAstTimeForSpecialist(specId);
        } else {
            lastentry = System.currentTimeMillis();
        }
        long nextTime = utilService.nextTime(lastentry);
        String serial = utilService.getSerial(nextTime);
        Visit newVisit = new Visit(specId, customerId, nextTime, 15, serial);
        visitsRepo.save(newVisit)
                .zipWith(specialistsRepo.findById(specId))
                .zipWith(customersRepo.findById(customerId))
                .map(visitspec -> utilService.fromVisit(visitspec.getT1().getT1(), visitspec.getT1().getT2(), visitspec.getT2()))
                .map(visitor -> {
                    TreeSet set = mySession.getVisitMap().get(specId);
                    if (null != set) {
                        set.add(visitor);
                    } else {
                        TreeSet<Visitor> visitorSet =  new TreeSet<>(mySession.getiDComparator());
                        visitorSet.add(visitor);
                        mySession.getVisitMap().put(specId, visitorSet);
                    }
//                    System.out.println("New visitor: " + visitor);
                    return visitor;
                })
                .subscribe();
    }

}
