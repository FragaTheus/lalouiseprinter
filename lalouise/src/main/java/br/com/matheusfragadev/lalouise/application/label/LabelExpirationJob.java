package br.com.matheusfragadev.lalouise.application.label;

import br.com.matheusfragadev.lalouise.application.restaurant.RestaurantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LabelExpirationJob {

    private final RestaurantService restaurantService;
    private final CheckUpLabels checkUpLabels;

    @Scheduled(cron = "0 0 2 * * *")
    public void checkAllRestaurantLabels(){
        log.info("============== Iniciando verificacao de etiquetas ==============");

        try {
            var restaurants = restaurantService.getActiveRestaurants();

            if (restaurants.isEmpty()){
                log.warn("Nenhum restaurante encontrado");
                return;
            }

            log.info("Iniciando verificacao para restaurantes {}", restaurants.size());

            restaurants.forEach(restaurant -> {
                try {
                    var restaurantId = restaurant.getId();
                    log.debug("Processando restaurante {}", restaurantId);
                    checkUpLabels.checkAndUpdateExpiringLabels(restaurantId);
                }catch (Exception e){
                    log.error("Erro ao processar etiquetas do restaurante {} com erro {}", restaurant.getId(), e.getMessage());
                }
            });

            log.info("Job de etiquetas concluido com sucesso!");
        }catch (Exception e){
            log.error("Erro critico ao executar varredura de etiquetas com erro {}", e.getMessage());
        }
    }

}
