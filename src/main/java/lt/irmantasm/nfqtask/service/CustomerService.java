package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.*;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class CustomerService {
    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    UtilService utilService;

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    public Mono<?> findByEmail(String email) {
        return customersRepo.findByEmail(email);
    }


    public Flux<MyVisit> getMyVisits(Long id) {
        return Flux.fromIterable(InMemoryService.getVisistorsSet())
                .filter(visitor -> Long.parseLong(visitor.getSpecIdCustId().split("-")[1]) == id)
                .map(visitor -> utilService.getVisitFromVisitor(visitor));
    }

    public Mono<Void> assignNew(Long specId, Long customerId) {
        Customer customer = InMemoryService.getCustById(customerId);
        Specialist specialist = InMemoryService.getSpecById(specId);
        return InMemoryService.getLastTimeForSpecialist(specId)
                .map(aLong -> makeVisit(aLong, specId, customerId))
                .flatMap(newVisit -> visitsRepo.save(newVisit))
                .map(visitspec -> makeVisitor(visitspec, specialist, customer))
                .then();
    }

    Visit makeVisit(Long visitLastTime, Long specId, Long custId) {
        Long next = utilService.nextTime(visitLastTime);
        String serial = utilService.getSerial(next);
        return new Visit(specId, custId, next, 15, serial);
    }

    Visitor makeVisitor(Visit visit, Specialist specialist, Customer customer) {
        Visitor visitor = utilService.visitorFromVisit(visit, specialist, customer);
        InMemoryService.addVisitorToSet(visitor);
                    return visitor;
    }

}
