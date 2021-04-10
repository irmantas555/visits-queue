package lt.irmantasm.nfqtask.controllers;


import lt.irmantasm.nfqtask.model.Customer;
import lt.irmantasm.nfqtask.model.Specialist;
import lt.irmantasm.nfqtask.repositories.CustomRepository;
import lt.irmantasm.nfqtask.repositories.SpecSortingRepository;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import lt.irmantasm.nfqtask.service.MySession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttribute;
import reactor.core.publisher.Mono;

@Controller
public class BasicController {


    @GetMapping(value = "/")
    public String getIndex(final Model model) {
        return "index";
    }

    @GetMapping(value = "/dashboard/det")
    public Mono<String> getCDashBoard(final Model model, @SessionAttribute Customer customer) {
            model.addAttribute("customer", customer);
            model.addAttribute("mysession", MySession.getSession());
            return Mono.just("dashboard");
    }

    @GetMapping(value = "/dashboard/spec")
    public Mono<String> getSDashBoard(final Model model, @SessionAttribute Specialist specialist) {
        model.addAttribute("specialist", specialist);
        return Mono.just("dashboard_spec");
    }

}
