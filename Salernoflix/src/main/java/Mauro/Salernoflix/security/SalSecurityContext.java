package Mauro.Salernoflix.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SalSecurityContext {

    public static SecurityPrincipal getPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal() instanceof SecurityPrincipal) {
            return ((SecurityPrincipal) authentication.getPrincipal());
        } else {
            return new SecurityPrincipal();
        }
    }

}
