package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.Customer;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CustomersRepo extends ReactiveCrudRepository<Customer, Long> {
    public boolean existsByEmail(String email);
    public Mono<Customer> findByEmail(String email);
}
