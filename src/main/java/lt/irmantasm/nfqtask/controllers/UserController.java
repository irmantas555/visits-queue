package lt.irmantasm.nfqtask.controllers;

import lt.irmantasm.nfqtask.model.AuthGroup;
import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.MyVisit;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.AuthGroupRepo;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.CustomerService;
import lt.irmantasm.nfqtask.service.InMemoryService;
import lt.irmantasm.nfqtask.service.SpecialistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.reactive.result.view.CsrfRequestDataValueProcessor;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.result.view.RedirectView;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.security.Principal;

@Controller
public class UserController {

    @Autowired
    SpecialistsRepo specialistsRepo;

    @Autowired
    AuthGroupRepo authGroupRepo;

    @Autowired
    SpecialistService specialistService;

    @Autowired
    CustomersRepo customersRepo;

    @Autowired
    CustomerService customerService;

    @GetMapping(value = "/specialist/login")
    public Mono<String> getLoginForm(final Model model, WebSession session) {
        session.invalidate();
        model.addAttribute("customerlog", false);
        model.addAttribute("failure", false);
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/specialist/success")
    public Mono<String> getSuccess(Principal principal, final Model model, WebSession session) {
        Customer customer = InMemoryService.getCustByEmail(principal.getName());
        Specialist specialist = InMemoryService.getSpecByEmail(principal.getName());
        if (null != customer) {
            session.getAttributes().put("customer", customer);
            return Mono.just("redirect:/customer/myvisits");
        } else if (null != specialist) {
            session.getAttributes().put("specialist", specialist);
            return Mono.just("redirect:/specialist/myvisits");
        } else {
            return Mono.just("redirect:/specialist/login");
        }
    }


    @GetMapping(value = "/customer/myvisits")
    public Mono<String> getCustomerBookedVisits(WebSession session, final Model model) {
        Customer customer = session.getAttribute("customer");
        Flux<MyVisit> myvisits = customerService.getMyVisits(customer.getId());
        model.addAttribute("customer", customer);
        model.addAttribute("visits", new ReactiveDataDriverContextVariable(myvisits, 50));
        return Mono.just("my_visits");
    }

    @GetMapping(value = "/specialist/failure")
    public Mono<String> getFail(final Model model) {
        model.addAttribute("customerlog", false);
        model.addAttribute("failure", true);
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/specialist/register")
    public Mono<String> getRegisterForm(final Model model) {
        model.addAttribute("specialist", new Specialist());
        return Mono.just("specialist_register");
    }

    @PostMapping(value = "/specialist/register")
    public Mono<String> specialsitRegister(@ModelAttribute("specialist") Specialist specialist) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(5);
        specialist.setPassword(encoder.encode(specialist.getPassword()));
        authGroupRepo.findByEmail(specialist.getEmail())
                .switchIfEmpty(authGroupRepo.save(new AuthGroup(specialist.getEmail(), "USER")))
                .then(specialistsRepo.findByEmail(specialist.getEmail()))
                .switchIfEmpty(specialistsRepo.save(specialist))
                .map(specialist1 -> InMemoryService.specialistSet.add(specialist1))
                .subscribe();
        return Mono.just("redirect:/specialist/login");
    }

    @ControllerAdvice
    public class SecurityControllerAdvice {
        @ModelAttribute
        Mono<CsrfToken> csrfToken(ServerWebExchange exchange) {
            Mono<CsrfToken> csrfToken = exchange.getAttribute(CsrfToken.class.getName());
            return csrfToken.doOnSuccess(token -> exchange.getAttributes()
                    .put(CsrfRequestDataValueProcessor.DEFAULT_CSRF_ATTR_NAME, token));
        }
    }

    @GetMapping(value = "/specialist/specassign/details")
    public Mono<String> getRegisterForm(WebSession session, @RequestParam Long specId, final Model model) {
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
    public Mono<String> getRegissterFormC(final Model model, SessionStatus status) {
        status.setComplete();
        model.addAttribute("customerlog", true);
        return Mono.just("specialist_login");
    }

    @GetMapping(value = "/customer/book/det")
    public Mono<String> getCustomerVisits(final Model model, @ModelAttribute("customer") Customer customer) {
        Flux<Specialist> specialists = specialistsRepo.findAll();
        model.addAttribute("specialists", new ReactiveDataDriverContextVariable(specialists, 50));
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
