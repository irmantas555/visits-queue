package lt.irmantasm.nfqtask.config;

import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@Profile("maria")
public class DataSetupDB {

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    MySession mySession;
    
    @PostConstruct
    private void populateCustomersSpecialists() {
        customersRepo.findAll()
                .map(customer -> {
                    MySession.addCustomer(customer);
                    return 1;
                })
                .subscribe();
        specialistsRepo.findAll()
                .map(specialist -> {
                    MySession.addSpecialist(specialist);
                    return 1;
                })
                .subscribe();
    }

}
