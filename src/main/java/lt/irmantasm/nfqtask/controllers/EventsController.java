package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.AuthGroup;
import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.repositories.AuthGroupRepo;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import lt.irmantasm.nfqtask.service.InMemoryService;
import lt.irmantasm.nfqtask.service.SinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@RestController
public class EventsController {

    @Autowired
    VisitsRepo visitsRepo;

    @Autowired
    SinkService service;

    @Autowired
    AuthGroupRepo authGroupRepo;

    @Autowired
    CustomersRepo customersRepo;

    @GetMapping(value = "/visits/sse")
    public Flux<List<MyVisit>> visitsStream() {
        return service.replaySink.asFlux().mergeWith(service.replaySinkCurrent.asFlux())
                .buffer(Duration.ofSeconds(1))
                ;
    }

    @GetMapping(value = "/visit/delete/{id}")
    public Mono<String> deleteVisitEntry(@PathVariable Long id) {
        return visitsRepo.deleteById(id)
                .then(InMemoryService.deleteVisitEntryById(id))
                .thenReturn("{\"status\":\"Success\"}");
    }

    @GetMapping(value = "/customer/add")
    public Mono<String> addCustomer(@RequestParam String email, @RequestParam String fname, @RequestParam String lname) {
        if (!email.equals("") && !fname.equals("") && !lname.equals("")) {
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);
            String pwd = encoder.encode("Cust123");
            Customer customer = new Customer(email, fname, lname, pwd);
            return authGroupRepo.findByEmail(email)
                    .switchIfEmpty(authGroupRepo.save(new AuthGroup(email, "USER")))
                    .then(customersRepo.findByEmail(email))
                    .delayElement(Duration.ofMillis(300))
                    .switchIfEmpty(customersRepo.save(customer))
                    .map(customer1 -> InMemoryService.customerSet.add(customer1))
                    .thenReturn("{\"status\":\"Success\"}");
        } else return Mono.error(new Throwable("There was an input error"));
//        return Mono.just("That is my replay");
    }

    @GetMapping(value = "/visit/started/details")
    public Mono<String> visitSatrt(@RequestParam Long visitId, @RequestParam Long specialistId) {
        return InMemoryService.setVisitStatus(visitId, 1)
                .then(Mono.just("Ok"));
    }

    @GetMapping(value = "/visit/finished/details")
    public Mono<String> visitFinish(@RequestParam Long visitId, @RequestParam Long specialistId) {
        return InMemoryService.setVisitStatus(visitId, 2)
                .then(Mono.just("Ok"));
    }

    @GetMapping(value = "/visits")
    public Mono<String> visitPrint() {
        return InMemoryService.printVisitors()
                .then(Mono.just("Ok"));
    }
}
