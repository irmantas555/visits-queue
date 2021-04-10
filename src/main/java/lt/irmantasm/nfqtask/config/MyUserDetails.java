package lt.irmantasm.nfqtask.config;

import lt.irmantasm.nfqtask.model.AuthGroup;
import lt.irmantasm.nfqtask.model.User;
import lt.irmantasm.nfqtask.repositories.AuthGroupRepo;
import lt.irmantasm.nfqtask.repositories.CustomersRepo;
import lt.irmantasm.nfqtask.repositories.SpecialistsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

@Component
public class MyUserDetails implements UserDetails {


    User user;
    List<AuthGroup> authGroups;

    public MyUserDetails(User user, List<AuthGroup> authGroups) {
        this.user = user;
        this.authGroups = authGroups;
    }

    @Autowired
    AuthGroupRepo authGroupRepo;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (null != authGroups) {
            Collection<SimpleGrantedAuthority> authorities = new HashSet<>();
            for (int i = 0; i < authGroups.size(); i++) {
                authorities.add(new SimpleGrantedAuthority(authGroups.get(i).getAuthGroup()));
            }
            return authorities;
        }
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
