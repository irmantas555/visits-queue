package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.SpecSortingRepository;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.CustomerService;
import lt.irmantasm.nfqtask.service.MySession;
import lt.irmantasm.nfqtask.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.server.WebSession;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@SessionAttributes("specialist")
@RequestMapping(value = "/specialist")
public class SpecialistController {

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    SpecialistService specialistService;

    @Autowired
    SpecSortingRepository specSortingRepository;

    @Autowired
    CustomerService customerService;


    @GetMapping(value = "/login")
    public Mono<String> getLoginForm(final Model model) {
        model.addAttribute(new Specialist());
        return Mono.just("specialist_login");
    }


    @PostMapping(value = "/login")
    public Mono<String> specialistLogin(@ModelAttribute("specialist") Specialist specialist, WebSession session) {
        Map<String, Object> attributes = session.getAttributes();
        attributes.entrySet().forEach(v-> System.out.println(v));
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/register")
    public Mono<String> getRegisterForm(final Model model) {
        model.addAttribute(new Specialist());
        return Mono.just("specialist_register");
    }

    @GetMapping(value = "/success")
    public Mono<String> getSuccess(Principal principal, final Model model) {
       return specialistsRepo.findByEmail(principal.getName())
                .flatMap(specialist -> {
                   return addSessionAtrribute(specialist, model);
                }) ;
    }

    @GetMapping(value = "/failure")
    public Mono<String> getFail( final Model model) {
        System.out.println("Failure accesed");
        model.addAttribute("failure", true);
        return Mono.just("specialist_login");
    }

    public Mono<String> addSessionAtrribute(Specialist specialist, final Model model) {
        return Mono.just("redirect:/specialist/myvisits");
    }


    @PostMapping(value = "/register")
    public Mono<String> specialsitRegister(@ModelAttribute("specialist") Specialist specialist) {
        specialistsRepo.findByEmail(specialist.getEmail())
                .switchIfEmpty(specialistsRepo.save(specialist))
                .subscribe();
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/specassign/details")
    public Mono<String> getRegisterForm(@RequestParam Long specId, @RequestParam Long cid, @RequestParam String s, final Model model) {
        customerService.assignNew(specId, cid);
        Flux<Specialist> specialistFlux = specSortingRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("specialists", new ReactiveDataDriverContextVariable(specialistFlux, 50));
        return Mono.just("redirect:/customer/myvisits/det?cid=" + cid + "&s=" + s);
    }

    @GetMapping(value = "/myvisits")
    public Mono<String> getMyBoard(final Model model, WebSession session) {
        Specialist specialist = session.getAttribute("specialist");
        Flux<MyVisit> myVisits = specialistService.getMyVisits(specialist.getId());
        model.addAttribute("specialist", specialist);
        model.addAttribute("visits", new ReactiveDataDriverContextVariable(myVisits, 50));
        return Mono.just("/spec_visits");
    }
}
