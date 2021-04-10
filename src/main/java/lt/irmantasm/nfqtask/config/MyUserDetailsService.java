package lt.irmantasm.nfqtask.config;

import lt.irmantasm.nfqtask.repositories.AuthGroupRepo;
import lt.irmantasm.nfqtask.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
@Component
public class MyUserDetailsService implements ReactiveUserDetailsService {
    @Autowired
    UserService userService;

    @Autowired
    AuthGroupRepo authGroupRepo;

    @Override
    public Mono<UserDetails> findByUsername(String s) {
        return authGroupRepo.findByEmail(s)
                .buffer()
                .single()
                .zipWith(userService.getUserByEmail(s))
                .map(userAuthGroup -> new MyUserDetails(userAuthGroup.getT2(), userAuthGroup.getT1()));
    }

}
