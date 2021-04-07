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
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicBoolean;

@Controller
@RequestMapping(value = "/specialist")
public class SpecialistController {

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    SpecialistService specialistService;

    @Autowired
    SpecSortingRepository specSortingRepository;

    @Autowired
    MySession mySession;

    @Autowired
    CustomerService customerService;


    @GetMapping(value = "/login")
    public Mono<String> getLoginForm(final Model model) {
        model.addAttribute(new Specialist());
        return Mono.just("specialist-login");
    }

    @PostMapping(value = "/login")
    public Mono<String> specialistLogin(@ModelAttribute("specialist") Specialist specialist, final Model model) {
        AtomicBoolean logged = new AtomicBoolean(false);
        model.addAttribute("logged", logged.get());
        String s = MySession.getSession();
        return specialistsRepo.findByEmail(specialist.getEmail())
                .switchIfEmpty(Mono.just(new Specialist("none","none","none","none")))
                .handle((specialist1, sink) -> {
                    if (!specialist1.getLastName().equals("none")) {
                        if (specialist1.getPassword().equals(specialist.getPassword())) {
                            logged.set(true);
                            sink.next("redirect:/specialist/myvisits/det?sid=" + specialist1.getId() + "&s=" + s);
                        } else {
                            sink.next("redirect:/specialist/login");
                        System.out.println("Bad specialist password: " + specialist);
                        }
                    } else {
                        System.out.println("Bad specialist: " + specialist);
                        sink.next("redirect:/specialist/login");
                    }
                });
    }

    @GetMapping(value = "/register")
    public Mono<String> getRegisterForm(final Model model) {
        model.addAttribute(new Specialist());
        return Mono.just("specialist-register");
    }


    @PostMapping(value = "/register")
    public Mono<String> specialsitRegister(@ModelAttribute("specialist") Specialist specialist) {
        specialistsRepo.findByEmail(specialist.getEmail())
                .switchIfEmpty(specialistsRepo.save(specialist))
                .map(specialist1 -> {
                    MySession.addSpecialist(specialist1);
                    return "Ok";
                })
                .subscribe();
        return Mono.just("specialist-login");
    }

    @GetMapping(value = "/specassign/details")
    public Mono<String> getRegisterForm(@RequestParam Long specId, @RequestParam Long cid, @RequestParam String s, final Model model) {
        customerService.assignNew(specId, cid);
        Flux<Specialist> specialistFlux = specSortingRepository.findAll(Sort.by(Sort.Direction.ASC, "id"));
        model.addAttribute("specialists", new ReactiveDataDriverContextVariable(specialistFlux, 50));
        return Mono.just("redirect:/customer/myvisits/det?cid=" + cid + "&s=" + s);
    }

    @GetMapping(value = "/myvisits/det")
    public Mono<String> getMyBoard(@RequestParam Long sid, @RequestParam String s, final Model model) {
        Specialist specialist = MySession.getSpecialsitById(sid);
        Flux<MyVisit> myVisits = specialistService.getMyVisits(sid);
        model.addAttribute("specialist", specialist);
        model.addAttribute("mysession", s);
        model.addAttribute("visits", new ReactiveDataDriverContextVariable(myVisits, 50));
        return Mono.just("/spec-visits");
    }
}
