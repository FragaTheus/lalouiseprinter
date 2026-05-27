package br.com.matheusfragadev.lalouise.application.auth;

import br.com.matheusfragadev.lalouise.application.mail.EmailService;
import br.com.matheusfragadev.lalouise.application.mail.MailMessageBuilder;
import br.com.matheusfragadev.lalouise.application.user.profile.registry.UserServiceRegistry;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.BaseStaff;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.infra.security.details.UserDetailsImpl;
import br.com.matheusfragadev.lalouise.infra.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserServiceRegistry userServiceRegistry;
    private final EmailService emailService;

    public LoginResult authenticate(String email, String password){
        try{
            log.info("Authenticating user with email: {}", email);
            var auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );

            var userDetails = (UserDetailsImpl) auth.getPrincipal();
            var id = userDetails.getId().toString();
            var role = userDetails.getRole();

            String restaurantId = null;
            String sectorId = null;

            switch (role) {
                case STAFF -> {
                    Staff user = userServiceRegistry.getUser(UUID.fromString(id));
                    restaurantId = user.getRestaurantId().toString();
                    sectorId = user.getSectorId().toString();
                }
                case MANAGER -> {
                    BaseStaff user = userServiceRegistry.getUser(UUID.fromString(id));
                    restaurantId = user.getRestaurantId().toString();
                }
                case ADMIN -> {}
                default -> throw new IllegalArgumentException("Role desconhecida: " + role);
            }

            var token = jwtService.generateToken(id, role.name(), restaurantId, sectorId);

            var mailCommand = MailMessageBuilder.buildLoginMail(userDetails.getUsername());
            emailService.sendSimpleEmail(mailCommand);

            log.info("User authenticated successfully, generated token");
            return new LoginResult(token, userDetails);
        }catch (BadCredentialsException e){
            log.warn("Authentication failed for email: {}, reason: {}", email, e.getMessage());
            throw new BadCredentialsException("Credenciais inválidas");
        }
    }

}
