package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.domain.label.entity.Label;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PrintMapper {

    public static ZplGenerateCommand toZplGenerateCommand(
            Label label,
            String restaurantName,
            String sectorName,
            String productName,
            String userNickname
    )
    {
        return new ZplGenerateCommand(
                label.getLot(),
                label.getValidateDate(),
                label.getCreatedAt(),
                restaurantName,
                sectorName,
                productName,
                userNickname
        );
    }

}
