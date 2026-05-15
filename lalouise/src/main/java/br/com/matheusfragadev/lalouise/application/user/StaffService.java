package br.com.matheusfragadev.lalouise.application.user;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import br.com.matheusfragadev.lalouise.application.sector.SectorService;
import br.com.matheusfragadev.lalouise.application.user.utils.ChangeUserPasswordCommand;
import br.com.matheusfragadev.lalouise.application.user.utils.CreateStaffCommand;
import br.com.matheusfragadev.lalouise.domain.user.admin.exceptions.UserAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.InactiveResourceException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.NicknameException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.exception.PasswordException;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Email;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Nickname;
import br.com.matheusfragadev.lalouise.domain.user.credentials.vo.Password;
import br.com.matheusfragadev.lalouise.domain.user.staff.entity.Staff;
import br.com.matheusfragadev.lalouise.domain.user.staff.exceptions.ManagerAlreadyExists;
import br.com.matheusfragadev.lalouise.domain.user.staff.repository.StaffRepository;
import br.com.matheusfragadev.lalouise.infra.context.restaurant.RestaurantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class StaffService implements UserService<Staff>{

    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final RestaurantService restaurantService;
    private final SectorService sectorService;


    @Transactional
    public Staff createStaff(CreateStaffCommand command){
        try {
            var restaurantId = RestaurantContext.get();
            log.info("Creating staff for restaurant: {}", restaurantId);
            var restaurant = restaurantService.getRestaurant(restaurantId);
            if (!restaurant.isActive()) {
                throw new InactiveResourceException("Não é possível vincular um colaborador a um restaurante inativo.");
            }
            var sector = sectorService.getSector(command.sectorId());
            if (!sector.isActive()) {
                throw new InactiveResourceException("Não é possível vincular um colaborador a um setor inativo.");
            }
            if (staffRepository.existsByEmail(new Email(command.email()))) {
                throw new UserAlreadyExists("Já existe um colaborador com esse email.");
            }
            inputPasswordMatches(command.password(), command.confirmPassword());
            var staff = new Staff(
                    new Nickname(command.nickname()),
                    new Email(command.email()),
                    Password.of(command.password(), passwordEncoder::encode),
                    restaurant.getId(),
                    sector.getId()
            );
            log.info("Staff created successfully for restaurant: {}", restaurantId);
            return staffRepository.save(staff);
        } catch (Exception e) {
            log.error("Error creating staff: {}", e.getMessage());
            throw e;
        }
    }

    @Override
    public Staff changeUserNickname(UUID targetId, String newNickname) {
        var staff = getUser(targetId);
        if (!staff.isActive()) {
            throw new InactiveResourceException("Não é possível alterar dados de um usuário inativo.");
        }
        if (staff.getNickname().value().equals(newNickname)) {
            throw new NicknameException("O novo nickname deve ser diferente do atual.");
        }
        staff.changeNickname(new Nickname(newNickname));
        return staffRepository.save(staff);
    }

    @Override
    public Staff changeUserPassword(ChangeUserPasswordCommand command) {
        try {
            log.info("Changing password for staff with id: {}", command.targetId());
            var staff = getUser(command.targetId());
            if (!staff.isActive()) {
                throw new InactiveResourceException("Não é possível alterar dados de um usuário inativo.");
            }
            inputPasswordMatches(command.newPassword(), command.confirmNewPassword());
            staff.changePassword(Password.of(command.newPassword(), passwordEncoder::encode));
            log.info("Password changed successfully for staff with id: {}", command.targetId());
            return staffRepository.save(staff);
        } catch (Exception e) {
            log.error("Error changing password for staff with id {}: {}", command.targetId(), e.getMessage());
            throw e;
        }
    }

    @Override
    public Staff deleteUser(UUID targetId) {
        try{
            log.info("Desativando colaborador com id: {}", targetId);
            var staff = getUser(targetId);
            staff.deactivate();
            log.info("Colaborador desativado com sucesso: {}", targetId);
            return staffRepository.save(staff);
        }catch (Exception e){
            log.error("Erro ao desativar colaborador com id: {}. Erro: {}", targetId, e.getMessage());
            throw e;
        }
    }

    @Override
    public Staff reactivate(UUID targetId) {
        try{
            log.info("Reativando colaborador com id: {}", targetId);
            var staff = getUser(targetId);
            staff.reactivate();
            log.info("Colaborador reativado com sucesso: {}", targetId);
            return staffRepository.save(staff);
        }catch (Exception e){
            log.error("Erro ao reativar colaborador com id: {}. Erro: {}", targetId, e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Staff getUser(UUID id) {
        var restaurantId = RestaurantContext.get();
        return staffRepository.findByIdAndRestaurantId(id, restaurantId)
                .orElseThrow(() -> new RuntimeException("Staff não encontrado para o id: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Staff> getAll(String term, Boolean active, Pageable pageable) {
        var restaurantId = RestaurantContext.get();
        return staffRepository.findAllStaffs(term, active, restaurantId, pageable);
    }

    @Transactional(readOnly = true)
    public String getRestaurantName(UUID restaurantId) {
        return restaurantService.getRestaurant(restaurantId).getName().value();
    }

    @Transactional(readOnly = true)
    public String getSectorName(UUID sectorId){
        return sectorService.getSectorById(sectorId).getName().value();
    }

    private void inputPasswordMatches(String password, String confirmPassword) {
        if (!password.equals(confirmPassword)) {
            throw new PasswordException("Senhas não conferem.");
        }
    }
}

