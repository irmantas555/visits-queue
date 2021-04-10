package lt.irmantasm.nfqtask.service;

import lt.irmantasm.nfqtask.model.User;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    public Mono<User> getUserByEmail(String email) {
        return customersRepo.findByEmail(email)
                .cast(User.class)
                .switchIfEmpty(specialistsRepo.findByEmail(email));
    }

}
