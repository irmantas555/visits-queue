package lt.irmantasm.nfqtask.config;

import lt.irmantasm.nfqtask.model.AuthGroup;
import lt.irmantasm.nfqtask.model.User;
import lt.irmantasm.nfqtask.repositories.AuthGroupRepo;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.annotation.PostConstruct;

@Component
@Profile("maria")
public class DataSetupDB {

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    AuthGroupRepo authGroupRepo;


//    @PostConstruct
    public Mono<Void> populateAuthGroups(){
        return customersRepo.findAll()
                .cast(User.class)
                .mergeWith(specialistsRepo.findAll())
                .map(user -> authGroupRepo.save(new AuthGroup(user.getEmail(), "USER")))
                .then();
    }

}
