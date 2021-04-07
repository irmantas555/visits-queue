package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.User;
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

    @Autowired
    MySession mySession;

    public Flux<MyVisit> getMyVisits(Long id) {
        if (mySession.getVisitMap().containsKey(id)) {
            return Flux.fromIterable(mySession.getVisitMap().get(id).entrySet())
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
//                        System.out.println(myVisit);
                        return myVisit;
                    });
        } else {
            return Flux.empty();
        }
    }

    public Mono<? extends User> findByEmailUser(String email){
        return specialistsRepo.findByEmail(email).cast(User.class)
                .doOnNext(user -> System.out.println("Found such specialist" + user));
    };
}
