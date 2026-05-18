package matheusfragadev.br.com.lalouise.printerservice.application;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.print.Doc;
import javax.print.DocFlavor;
import javax.print.DocPrintJob;
import javax.print.PrintException;
import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.SimpleDoc;
import java.nio.charset.StandardCharsets;

@Slf4j
@Service
public class ZebraPrinterService {

    @Value("${printer.name}")
    private String printerName;

    public void sendToPrinter(PrintMessageCommand command) throws PrintException {

        PrintService printer = resolvePrinter();
        DocPrintJob job = printer.createPrintJob();

        byte[] zplBytes = command.zpl().getBytes(StandardCharsets.UTF_8);
        Doc doc = new SimpleDoc(zplBytes, DocFlavor.BYTE_ARRAY.AUTOSENSE, null);

        job.print(doc, null);
        log.info("Job {} enviado com sucesso para a impressora {}", command.jobId(), printerName);
    }

    private PrintService resolvePrinter() throws PrintException {
        PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);

        for (PrintService service : services) {
            if (service.getName().equalsIgnoreCase(printerName)) {
                return service;
            }
        }

        // Falha física/operacional: a impressora configurada no Windows não foi encontrada.
        throw new PrintException("Impressora nao encontrada: " + printerName);
    }

}
