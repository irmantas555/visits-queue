package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.model.User;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface SpecialistsRepo extends ReactiveCrudRepository<Specialist, Long> {
    public Mono<Boolean> existsByEmail(String email);
    public Mono<Specialist> findByEmail(String email);
}
