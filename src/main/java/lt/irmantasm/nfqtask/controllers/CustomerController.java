package lt.irmantasm.nfqtask.controllers;

import javassist.tools.web.Webserver;
import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.CustomerService;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping(value = "/customer")
public class CustomerController {


    @Autowired
    CustomerService customerService;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    MySession mySessionUtils;




    @GetMapping(value = "/register")
    public String getRegissterForm(final Model model) {
        model.addAttribute(new Customer());
        return "customer_register";
    }

    @PostMapping(value = "/register")
    public Mono<String> customerRegister(@ModelAttribute("customer") Customer customer,  ServerHttpResponse response) {
        return customersRepo.findByEmail(customer.getEmail())
                .switchIfEmpty(customersRepo.save(new Customer(customer.getEmail(), customer.getFirstName(), customer.getLastName(), "customer")))
                .map(customer1 -> {
                    return customer1;
                })
                .map(customer1 -> "redirect:/customer/myvisits/det?cid=" + customer1.getId() + "&s=" + MySession.getSession());
    }

    @GetMapping(value = "/book/det")
    public Mono<String> getCustomerVisits(final Model model,@RequestParam String s ,@RequestParam Long cid) {
        if (s.equals(MySession.getSession())) {
            Flux<Specialist> specialists = specialistsRepo.findAll();
            model.addAttribute("mysession", mySessionUtils.getSession());
            model.addAttribute("specialists",new ReactiveDataDriverContextVariable(specialists, 50));
            return Mono.just("vitsits_booking");
        } else {
            return Mono.just("redirect:/");
        }
    }

    @GetMapping(value = "/myvisits/det")
    public Mono<String> getCustomerBookedVisits(WebSession session, final Model model) {
        Customer customer = session.getAttribute("customer");
            Flux<MyVisit> myvisits = customerService.getMyVisits(customer.getId());
            model.addAttribute("customer", customer);
            model.addAttribute("visits", new ReactiveDataDriverContextVariable(myvisits, 50));
            return Mono.just("my_visits");
    }

}
