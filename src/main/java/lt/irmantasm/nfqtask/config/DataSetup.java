package lt.irmantasm.nfqtask.config;

import lt.irmantasm.nfqtask.model.Visitor;
import lt.irmantasm.nfqtask.repositories.CustomRepository;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import lt.irmantasm.nfqtask.service.InMemoryService;
import lt.irmantasm.nfqtask.service.UtilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.util.*;

@Component
public class DataSetup {


    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    CustomRepository customRepository;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;


    @Autowired
    UtilService utilService;

    @PostConstruct
    private void populateInMemoryVisitsMap() {
        customRepository.getVisitsValues();
    }

    @PostConstruct
    private void addSpecialists(){
        specialistsRepo.findAll()
                .delaySubscription(Duration.ofSeconds(8))
                .map(specialist -> InMemoryService.specialistSet.add(specialist))
                .thenMany(customersRepo.findAll())
                .map(customer -> InMemoryService.customerSet.add(customer))
                .subscribe();
    }

    @PostConstruct
    public void makeCleanup() {
        Flux.interval(Duration.ofSeconds(60))
                .map(aLong -> {
                    visitsCleanupOperations();
                    return aLong;
                }).subscribe();
    }

    private void visitsCleanupOperations() {
        Long nowMinus15Min = System.currentTimeMillis() - 15 * 60 * 1000; //current time + visit time(constant 15 min)
        Set<Visitor> set = InMemoryService.getVisistorsSet();
        Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            Visitor visitor = (Visitor) iterator.next();
            if (visitor.getVisitTime() < nowMinus15Min) {
                visitsRepo.deleteById(visitor.getVisitId()).subscribe();
                iterator.remove();
            }
        }
    }
}
