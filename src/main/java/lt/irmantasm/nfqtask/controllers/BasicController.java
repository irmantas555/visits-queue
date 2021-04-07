package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.*;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
public class BasicController {

    @Autowired
    CustomRepository customRepository;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    SpecSortingRepository specSortingRepository;

    @Autowired
    MySession mySessionUtility;

    int pageSize = 10;

    @GetMapping(value = "/")
    public Mono<String> getIndex(final Model model) {
        return Mono.just("index");
    }

    @GetMapping(value = "/dashboard/det")
    public Mono<String> getCDashBoard(final Model model, @RequestParam String s, @RequestParam Long cid) {
        Customer customer = MySession.getCustomerById(cid);
        if (s.equals(MySession.getSession())) {
            model.addAttribute("customer", customer);
            model.addAttribute("mysession", MySession.getSession());
            return Mono.just("dashboard");
        } else {
            return Mono.just("redirect:/");
        }
    }

    @GetMapping(value = "/dashboard/spec/det")
    public Mono<String> getSDashBoard(final Model model, @RequestParam String s, @RequestParam Long sid) {
        Specialist specialist = MySession.getSpecialsitById(sid);
        if (s.equals(MySession.getSession())) {
            model.addAttribute("specialist", specialist);
            model.addAttribute("mysession", s);
            return Mono.just("dashboard-spec");
        } else {
            return Mono.just("redirect:/");
        }
    }



}
