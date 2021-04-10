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
                   return Flux.fromIterable(visistsEntry.getValue().entrySet())
                            .filter(entry -> entry.getValue().getSpecIdCustId().split("-")[1].equals(id.toString()))
                            .map(entry -> {
                                String visistTime = utilService.getVisitTime(entry.getKey());
                                String timeLeft = utilService.getTimeLeft(entry.getKey());
                                MyVisit myVisit = new MyVisit(
                                        entry.getValue().getVisitId(),
                                        entry.getKey(),
                                        entry.getValue().getSerial(),
                                        visistTime,
                                        timeLeft,
                                        entry.getValue().getSpecFirsLastName(),
                                        entry.getValue().getFirstName() + " " + entry.getValue().getLastName(),
                                        entry.getValue().getIntVisitSatus());
                                return myVisit;
                            });
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
        long nextTime = (Instant.ofEpochMilli(lastentry).plusSeconds(Duration.ofMinutes(15).getSeconds())).toEpochMilli();
        String serial = utilService.getSerial(nextTime);
        Visit newVisit = new Visit(specId, customerId, nextTime, 15, serial);
        visitsRepo.save(newVisit)
                .zipWith(specialistsRepo.findById(specId))
                .zipWith(customersRepo.findById(customerId))
                .map(visitspec -> mySession.fromVisit(visitspec.getT1().getT1(), visitspec.getT1().getT2(), visitspec.getT2()))
                .map(visitor -> {
                    TreeMap map = mySession.getVisitMap().get(specId);
                    if (null != map) {
                        map.put(nextTime, visitor);
                    } else {
                        TreeMap<Long, Visitor> visitorMap = new TreeMap();
                        visitorMap.put(nextTime, visitor);
                        mySession.getVisitMap().put(specId, visitorMap);
                    }
//                    System.out.println("New visitor: " + visitor);
                    return visitor;
                })
                .subscribe();
    }

}
