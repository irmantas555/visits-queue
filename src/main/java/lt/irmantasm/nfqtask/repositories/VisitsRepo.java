package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.Visit;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitsRepo extends ReactiveCrudRepository<Visit, Long> {

}
