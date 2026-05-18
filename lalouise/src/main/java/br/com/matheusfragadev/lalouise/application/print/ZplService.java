package br.com.matheusfragadev.lalouise.application.print;

import br.com.matheusfragadev.lalouise.infra.controller.label.utils.resolver.LabelInfoResolverResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Serviço responsável por gerar o código ZPL (Zebra Programming Language)
 * a partir dos dados de uma etiqueta, para envio direto a impressoras Zebra.
 *
 * Layout padrão: ~60mm x 45mm (480 x 360 dots @ 203 DPI)
 */
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

    /**
     * Gera o ZPL completo para a etiqueta informada.
     *
     * @param result  resultado resolvido com nome do restaurante, setor, produto e responsável
     * @param copies  número de cópias a imprimir (normalizado entre 1 e 99)
     * @return String com o código ZPL pronto para envio à impressora
     */
    public String generate(ZplGenerateCommand result, Integer copies) {
        try {
            int safeCopies = (copies == null || copies < 1)
                    ? 1
                    : copies;

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
        var restaurant = sanitize(command.restaurantName(), 40);
        var sector     = sanitize(command.sectorName(), 40);
        var product    = sanitize(command.productName(), 45);
        var printedBy  = sanitize(command.printedByName(), 40);
        var lotCode    = sanitize(command.lot().code(), 30);
        var valDate    = DATE_FMT.format(command.validateDate());
        var fabDate    = DATE_FMT.format(command.createdAt());
        var fabTime    = TIME_FMT.format(command.createdAt());


        // ── Cabeçalho ─────────────────────────────────────────────────

        return "^XA\n" +
                "^CI28\n" +
                "^MMT\n" +
                "^PW480\n" +
                "^LL360\n" +
                "^LS0\n" +
                String.format("^PQ%d%n%n", copies) +
                String.format("^FO30,20^A0N,30,30^FD%s^FS%n", restaurant) +
                "^FO30,55^GB420,2,2^FS\n" +

                // ── Responsável ───────────────────────────────────────────────
                String.format("^FO30,70^A0N,20,20^FDResp: %s^FS%n", printedBy) +

                // ── Setor ─────────────────────────────────────────────────────
                String.format("^FO30,95^A0N,20,20^FDSetor: %s^FS%n", sector) +
                "^FO30,120^GB420,2,2^FS\n" +

                // ── Produto ───────────────────────────────────────────────────
                String.format("^FO30,135^A0N,25,25^FD%s^FS%n", product) +

                // ── Lote ──────────────────────────────────────────────────────
                String.format("^FO30,170^A0N,22,22^FDLote: %s^FS%n", lotCode) +

                // ── Validade ──────────────────────────────────────────────────
                String.format("^FO30,200^A0N,25,25^FDVal: %s^FS%n", valDate) +

                // ── Fabricação ────────────────────────────────────────────────
                String.format("^FO30,230^A0N,20,20^FDFab: %s^FS%n", fabDate) +
                String.format("^FO30,252^A0N,20,20^FDHora: %s^FS%n", fabTime) +
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
}
