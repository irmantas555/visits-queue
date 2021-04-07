package lt.irmantasm.nfqtask.repositories;

import lt.irmantasm.nfqtask.model.Specialist;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;

public interface SpecSortingRepository extends ReactiveSortingRepository<Specialist, Long> {
}
