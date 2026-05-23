package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.application.print.utils.command.ZplGenerateCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
public class ZplService {

    private static final ZoneId ZONE = ZoneId.of("America/Sao_Paulo");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter
            .ofPattern("dd/MM/yyyy")
            .withZone(ZONE);
    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter
            .ofPattern("HH:mm")
            .withZone(ZONE);

    public String generate(ZplGenerateCommand result, Integer copies) {
        try {
            int safeCopies = (copies == null || copies < 1)
                    ? 1
                    : copies;

//
            log.info("Gerando ZPL para com {} cópia(s)", safeCopies);

            String zpl = buildZplLayout(result, safeCopies);

            log.info("ZPL gerado com sucesso");
            return zpl;

        } catch (Exception e) {
            log.error("Erro ao gerar ZPL para label", e);
            throw new RuntimeException("Erro ao gerar layout da etiqueta: " + e.getMessage(), e);
        }
    }

    private String buildZplLayout(ZplGenerateCommand command, int copies) {
        var restaurant = sanitize(command.restaurantName(), 30); // reduzido para caber na esquerda
        var sector     = sanitize(command.sectorName(), 30);
        var product    = sanitize(command.productName(), 30);
        var printedBy  = sanitize(command.printedByName(), 30);
        var lotCode    = sanitize(command.lot().code(), 20);
        var valDate    = DATE_FMT.format(command.validateDate());
        var fabDate    = DATE_FMT.format(command.createdAt());
        var fabTime    = TIME_FMT.format(command.createdAt());

        return "^XA\n" +
                "^CI28\n" +
                "^MMT\n" +
                "^PW480\n" +
                "^LL360\n" +
                "^LS0\n" +
                String.format("^PQ%d%n%n", copies) +

                // ── Cabeçalho ────────────────────────────────────────────
                String.format("^FO30,20^A0N,28,28^FD%s^FS%n", restaurant) +
                "^FO30,53^GB440,2,2^FS\n" +

                // ── Responsável / Setor ──────────────────────────────────
                String.format("^FO30,65^A0N,20,20^FDResp: %s^FS%n", printedBy) +
                String.format("^FO30,90^A0N,20,20^FDSetor: %s^FS%n", sector) +
                "^FO30,115^GB440,2,2^FS\n" +

                // ── Produto ──────────────────────────────────────────────
                String.format("^FO30,128^A0N,24,24^FD%s^FS%n", product) +

                // ── Lote ─────────────────────────────────────────────────
                String.format("^FO30,160^A0N,21,21^FDLote: %s^FS%n", lotCode) +

                // ── Validade ─────────────────────────────────────────────
                String.format("^FO30,190^A0N,24,24^FDVal: %s^FS%n", valDate) +

                // ── Fabricação ───────────────────────────────────────────
                String.format("^FO30,222^A0N,20,20^FDFab: %s^FS%n", fabDate) +
                String.format("^FO30,247^A0N,20,20^FDHora: %s^FS%n", fabTime) +

                // ── QR Code (direita, centralizado verticalmente) ────────
                buildQrCode(290, 120, command.restaurantId(), command.labelId()) +

                "^XZ";
    }

    private String sanitize(String text, int maxLength) {
        if (text == null || text.isBlank()) return "";
        String cleaned = text
                .replace("^", "")
                .replace("~", "")
                .replace("|", "-")
                .replace("\\", "")
                .trim();
        return cleaned.length() > maxLength ? cleaned.substring(0, maxLength) : cleaned;
    }

    private String buildQrCode(int x, int y, UUID restaurantId, UUID labelId) {
        if (restaurantId == null || labelId == null) {
            log.warn("restaurantId ou labelId nulos, QR Code não será gerado");
            return "";
        }
        String url = String.format(
                "https://lalouiseprinter-upsj.vercel.app/dashboard/restaurants/%s/resources/labels/%s",
                restaurantId, labelId
        );
        return String.format("^FO%d,%d^BQN,2,3^FDLA,%s^FS%n", x, y, url);
    }
}
