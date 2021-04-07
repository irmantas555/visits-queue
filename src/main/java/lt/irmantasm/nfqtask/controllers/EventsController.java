package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.repositories.VisitsRepo;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;

@RestController
public class EventsController {
    @Autowired
    MySession mySession;

    @Autowired
    VisitsRepo visitsRepo;


    @GetMapping(value = "/visits/sse")
    public Flux<List<MyVisit>> visitsStream() {
        return mySession.getMyVisitListStarted()
                .mergeWith(mySession.getMyVisitListSorted())
                .doOnNext(myVisit -> System.out.println(myVisit))
                .buffer(Duration.ofSeconds(1));
    }

    @GetMapping(value = "/visit/delete/{id}")
    public Mono<String> deleteVisitEntry(@PathVariable Long id) {
        return visitsRepo.deleteById(id)
                .then(mySession.deleteVisitEntryById(id))
                .thenReturn("{\"status\":\"Success\"}");
    }

    @GetMapping(value = "/visit/started/details")
    public Mono<String> visitSatrt(@RequestParam Long visitId, @RequestParam Long specialistId) {
        return mySession.setVisitStatus(visitId, 1)
                .then(Mono.just("Ok"));
    }

    @GetMapping(value = "/visit/finished/details")
    public Mono<String> visitFinish(@RequestParam Long visitId, @RequestParam Long specialistId) {
        return mySession.setVisitStatus(visitId, 2)
                .then(Mono.just("Ok"));
    }

    @GetMapping(value = "/visits")
    public Mono<String> visitPrint() {
        return mySession.printVisitors()
                .then(Mono.just("Ok"));
    }
}
