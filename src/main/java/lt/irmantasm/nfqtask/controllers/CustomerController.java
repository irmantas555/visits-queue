package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.WebSession;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;


public class CustomerController {


    @Autowired
    CustomerService customerService;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;



    @GetMapping(value = "/customer/register")
    public Mono<String> getRegissterForm(final Model model) {
        model.addAttribute("customerlog", true);
        return Mono.just("specialist_login");
    }


    @GetMapping(value = "/customer/final")
    public Mono<String> addSessionAtrributeCust(Customer customer, WebSession session,final Model model) {
        Map<String, Object> attributes = session.getAttributes();
        attributes.entrySet().forEach(v -> System.out.println(v));
        return Mono.just("redirect:/customer/myvisits");
    }

    @GetMapping(value = "/customer/redirect")
    public Mono<String> getCustomer(@RequestParam String pname, final Model model){
        return customersRepo.findByEmail(pname)
                .switchIfEmpty(Mono.just(new Customer()))
                .map(customer -> {
                    if (0 != customer.getId()){
                        model.addAttribute("customer", customer);
                        System.out.println("CUSTOMER" + customer);
                        return "redirect:/customer/final";
                    } else {
                        return "redirect:/";
                    }
                });
    }

    @GetMapping(value = "/customer/book/det")
    public Mono<String> getCustomerVisits(final Model model, WebSession session) {
            Customer customer = session.getAttribute("customer");
            Flux<Specialist> specialists = specialistsRepo.findAll();
            model.addAttribute("specialists",new ReactiveDataDriverContextVariable(specialists, 50));
            return Mono.just("vitsits_booking");

    }

    @GetMapping(value = "/customer/myvisits")
    public Mono<String> getCustomerBookedVisits(WebSession session, final Model model) {
        System.out.println("Customer attribute: " + session.getAttribute("customer").toString());
        Customer customer = session.getAttribute("customer");
            Flux<MyVisit> myvisits = customerService.getMyVisits(customer.getId());
            model.addAttribute("customer", customer);
            model.addAttribute("visits", new ReactiveDataDriverContextVariable(myvisits, 50));
            return Mono.just("my_visits");
    }

}
