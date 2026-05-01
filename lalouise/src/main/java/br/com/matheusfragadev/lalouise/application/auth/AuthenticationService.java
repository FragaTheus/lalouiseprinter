package br.com.matheusfragadev.lalouise.application.auth;

import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public LoginResult authenticate(String email, String password){
        try{
            log.info("Authenticating user with email: {}", email);
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            var userDetails = (UserDetailsImpl) auth.getPrincipal();
            var id = userDetails.getId().toString();
            var role = userDetails.getRole().name();
            var token = jwtService.generateToken(id, role);
            log.info("User authenticated successfully, generated token: {}", token);
            return new LoginResult(token, userDetails);
        }catch (BadCredentialsException e){
            log.warn("Authentication failed for email: {}, reason: {}", email, e.getMessage());
            throw new BadCredentialsException("Credenciais inválidas");
        }
    }

}
