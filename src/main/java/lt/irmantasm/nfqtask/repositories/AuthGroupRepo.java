package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.AuthGroup;
import lt.irmantasm.nfqtask.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface AuthGroupRepo extends ReactiveCrudRepository<AuthGroup, Long> {

    Flux<AuthGroup> findByEmail(String email);

}
