package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.Session;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.repositories.VisistsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@Controller
public class CustomerController {

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    VisistsRepo visistsRepo;

    @Autowired
    SpecialistsRepo specialistsRepo;

    @GetMapping(value = "/")
    public String getIndex(final Model model) {
        return ("index");
    }

    @GetMapping(value = "/register")
    public String getRegissterForm(final Model model) {
        model.addAttribute(new Customer());
        return "customer-enter";
    }



    @PostMapping(value = "/register")
    public String customerRegister(@ModelAttribute("customer") Customer customer) {
        customersRepo.findByEmail(customer.getEmail())
                .switchIfEmpty(customersRepo.save(customer))
                .map(customer1 -> {
                    Session session = new Session(customer1);
                    return customer1;
                }).subscribe();
        return "redirect:/dashboard";
    }


    @GetMapping(value = "/dashboard")
    public String getDashBoard(final Model model) {
        Flux<Specialist> specialistFlux = specialistsRepo.findAll();
        model.addAttribute("specialists", new ReactiveDataDriverContextVariable(specialistFlux, 1));
        return ("dashboard");
    }


}
