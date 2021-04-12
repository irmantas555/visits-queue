package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.CustomerService;
import lt.irmantasm.nfqtask.service.InMemoryService;
import lt.irmantasm.nfqtask.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.server.WebSession;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
@SessionAttributes({"specialist", "customer"})
public class UserController {

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    SpecialistService specialistService;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    CustomerService customerService;

    @GetMapping(value = "/specialist/login")
    public Mono<String> getLoginForm(final Model model) {
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/specialist/register")
    public Mono<String> getRegisterForm(final Model model) {
        return Mono.just("specialist_register");
    }

    @GetMapping(value = "/specialist/success")
    public Mono<String> getSuccess(Principal principal, final Model model, WebSession session) {
        Specialist specialist = InMemoryService.getSpecByEmail(principal.getName());
        Customer customer = InMemoryService.getCustByEmail(principal.getName());
        if (null != specialist) {
            session.getAttributes().put("specialist", specialist);
            return Mono.just("redirect:/specialist/myvisits");
        }
        else {
            session.getAttributes().put("customer", customer);
            return Mono.just("redirect:/customer/myvisits");
        }
    }

    @GetMapping(value = "/customer/inter")
    public Mono<RedirectView> attachCustomer(@RequestParam String pname, final Model model){
        Mono<Customer> byEmail = customersRepo.findByEmail(pname);
        model.addAttribute("customer",new ReactiveDataDriverContextVariable(byEmail, 1));
        return Mono.just(new RedirectView("/customer/redirect"));
    }

    @GetMapping(value = "/customer/redirect")
    public Mono<String> getCustomer(@ModelAttribute("customer") Customer customer, final Model model){
        Flux<MyVisit> myvisits = customerService.getMyVisits(customer.getId());
        model.addAttribute("visits", new ReactiveDataDriverContextVariable(myvisits, 50));
        return Mono.just("my_visits");
    }

    @GetMapping(value = "/customer/myvisits")
    public Mono<String> getCustomerBookedVisits(WebSession session, final Model model) {
        Customer customer = session.getAttribute("customer");
        Flux<MyVisit> myvisits = customerService.getMyVisits(customer.getId());
        model.addAttribute("visits", new ReactiveDataDriverContextVariable(myvisits, 50));
        return Mono.just("my_visits");
    }

    @GetMapping(value = "/specialist/failure")
    public Mono<RedirectView> getFail( final Model model) {
        System.out.println("Failure accesed");
        model.addAttribute("failure", true);
        return Mono.just(new RedirectView("/specialis/login", HttpStatus.MOVED_PERMANENTLY));
    }

    @PostMapping(value = "/specialist/register")
    public Mono<String> specialsitRegister(@ModelAttribute("specialist") Specialist specialist) {
        specialistsRepo.findByEmail(specialist.getEmail())
                .switchIfEmpty(specialistsRepo.save(specialist))
                .map(specialist1 -> InMemoryService.specialistSet.add(specialist1))
                .subscribe();
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/specialist/specassign/details")
    public Mono<String> getRegisterForm(WebSession session,@RequestParam Long specId, final Model model) {
        Customer customer = session.getAttribute("customer");
        return customerService.assignNew(specId, customer.getId())
                .then(Mono.just("redirect:/customer/myvisits"));
    }

    @GetMapping(value = "/specialist/myvisits")
    public Mono<String> getMyBoardC(final Model model, WebSession session) {
        Specialist specialist = session.getAttribute("specialist");
        Flux<MyVisit> myVisits = specialistService.getMyVisits(specialist.getId());
        model.addAttribute("specialist", specialist);
        model.addAttribute("visits", new ReactiveDataDriverContextVariable(myVisits, 50));
        return Mono.just("/spec_visits");
    }

    @GetMapping(value = "/customer/register")
    public Mono<String> getRegissterFormC(final Model model) {
        model.addAttribute("customerlog", true);
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/customer/book/det")
    public Mono<String> getCustomerVisits(final Model model, @ModelAttribute("customer") Customer customer) {
        Flux<Specialist> specialists = specialistsRepo.findAll();
        model.addAttribute("specialists",new ReactiveDataDriverContextVariable(specialists, 50));
        return Mono.just("vitsits_booking");
    }

    @GetMapping(value = "/dashboard/det")
    public Mono<String> getCDashBoard(final Model model) {
        return Mono.just("dashboard");
    }

    @GetMapping(value = "/dashboard/spec")
    public Mono<String> getSDashBoard(final Model model) {
        return Mono.just("dashboard_spec");
    }

}
