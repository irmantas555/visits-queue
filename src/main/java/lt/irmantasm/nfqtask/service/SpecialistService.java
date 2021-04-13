package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.User;
import lt.irmantasm.nfqtask.model.Visitor;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class SpecialistService {

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    UtilService utilService;

    public Mono<?> findByEmail(String email) {
        return specialistsRepo.findByEmail(email);
    }

    public Flux<MyVisit> getMyVisits(Long id) {
        return InMemoryService.getVisitorsForSpecialist(id).map(visitor -> utilService.getVisitFromVisitor(visitor)).switchIfEmpty(Mono.just(new MyVisit()));
    }

    public Mono<? extends User> findByEmailUser(String email){
        return specialistsRepo.findByEmail(email).cast(User.class)
                ;
    };

}
