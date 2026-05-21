package br.com.matheusfragadev.lalouise.infra.controller.label.utils.mapper;

import br.com.matheusfragadev.lalouise.application.print.utils.command.PrintLabelCommand;
import br.com.matheusfragadev.lalouise.application.print.utils.command.GeneraleLabelForNewLocationCommand;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.PrintLabelRequest;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.ReprintLabelRequest;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.response.LabelInfo;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.dto.response.LabelSummary;
import br.com.matheusfragadev.lalouise.infra.controller.label.utils.resolver.LabelInfoResolverResult;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LabelMapper {

    public static PrintLabelCommand toPrintCommand(PrintLabelRequest request, UUID userId){
        return PrintLabelCommand.builder()
                .userId(userId)
                .productId(request.productId())
                .storage(request.storage())
                .copies(request.copies())
                .build();
    }

    public static GeneraleLabelForNewLocationCommand toReprintLabelCommand(ReprintLabelRequest request, UUID userId, UUID labelId){
        return new GeneraleLabelForNewLocationCommand(labelId, userId, request.storage(), request.copies());
    }

    public static LabelInfo toInfo(LabelInfoResolverResult result){
        return LabelInfo.builder()
                .id(result.label().getId())
                .restaurantName(result.restaurantName())
                .sectorName(result.sectorName())
                .printedBy(result.printedByName())
                .lot(result.label().getLot().code())
                .createdAt(result.label().getCreatedAt())
                .updateAt(result.label().getUpdatedAt())
                .validateDate(result.label().getValidateDate())
                .status(result.label().getStatus())
                .productName(result.productName())
                .build();
    }

    public static LabelSummary toSummary(LabelInfoResolverResult result){
        return LabelSummary.builder()
                .id(result.label().getId())
                .productName(result.productName())
                .sectorName(result.sectorName())
                .lot(result.label().getLot().code())
                .validateDate(result.label().getValidateDate())
                .status(result.label().getStatus())
                .build();
    }


}
