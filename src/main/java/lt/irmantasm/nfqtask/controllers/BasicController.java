package lt.irmantasm.nfqtask.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@Controller
public class BasicController {


    @GetMapping(value = "/")
    public Mono<String> getIndex(final Model model) {
        return Mono.just("index");
    }

}
