package se.statenspersonadressregister.referensimplementation.installningar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static java.util.Arrays.asList;

/**
 * Behållare för olika inställningar, förifyllda värden är giltiga för att
 * kunna köra mot kundtest utan att behöva ange något explicit.
 */
public class PersonsokInstallningar {
    private static final Logger log = LoggerFactory.getLogger(PersonsokInstallningar.class);

    private String url = "https://kt-ext-ws.statenspersonadressregister.se/2021.1/";

    // Information om organisationscertifikatet
    private String certifikatSokvag = getClass().getResource("/Kommun_A.p12").getFile();
    private String certifikatLosenord = "4611510421732432";

    // Information om ca-certifikatet
    private String caSokvag = System.getProperty("java.home") + "/lib/security/cacerts";
    private String caLosenord = "changeit";

    // Information för att identifiera frågeställare
    private Integer kundNrLeveransMottagare = 500243;
    private Integer kundNrSlutkund = 500243;
    private Long uppdragsId = 637L;
    private String slutAnvandarId = "Anställd X på avdelning B (Referensimplementation 2021.1 - Java)";

    /**
     * Skapar en en instans med default inställningar
     */
    public PersonsokInstallningar() {
    }

    /**
     * Skapar en instans där inställningar sätts med argument, exempel "url=http://localhost certifikatsokvag=/mitt/certifikat.p12".
     * För att inte använda organisationscertifikat, "certifikatsokvag= " (utan något värde).
     */
    public PersonsokInstallningar(String[] args) {
        asList(args).forEach(this::bearbetaArgument);
    }

    private void bearbetaArgument(String argument) {
        log.debug("Bearbetar argument {}", argument);
        String[] split = argument.split("=", 2);

        if (split.length == 2) {
            switch (split[0].toLowerCase()) {
                case "url":
                    url = split[1];
                    break;
                case "certifikatsokvag":
                    certifikatSokvag = split[1];
                    break;
                case "certifikatlosenord":
                    certifikatLosenord = split[1];
                    break;
                case "kundnrleveransmottagare":
                    kundNrLeveransMottagare = Integer.valueOf(split[1]);
                    break;
                case "kundnrslutkund":
                    kundNrSlutkund = Integer.valueOf(split[1]);
                    break;
                case "uppdragsid":
                    uppdragsId = Long.valueOf(split[1]);
                    break;
                case "slutanvandarid":
                    slutAnvandarId = split[1];
                    break;
                case "casokvag":
                    caSokvag = split[1];
                    break;
                case "calosenord":
                    caLosenord = split[1];
                    break;
                default:
                    log.warn("Okänt argument {}", argument);
            }
        } else {
            log.warn("Okänt argument {}", argument);
        }
    }

    public String getUrl() {
        return url;
    }

    public String getCertifikatSokvag() {
        return certifikatSokvag;
    }

    public String getCertifikatLosenord() {
        return certifikatLosenord;
    }

    public Integer getKundNrLeveransMottagare() {
        return kundNrLeveransMottagare;
    }

    public Integer getKundNrSlutkund() {
        return kundNrSlutkund;
    }

    public Long getUppdragsId() {
        return uppdragsId;
    }

    public String getSlutAnvandarId() {
        return slutAnvandarId;
    }

    public String getCaSokvag() {
        return caSokvag;
    }

    public String getCaLosenord() {
        return caLosenord;
    }
}
