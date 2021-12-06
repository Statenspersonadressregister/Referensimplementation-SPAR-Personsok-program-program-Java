package se.statenspersonadressregister.referensimplementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.statenspersonadressregister.personsok._2021.PersonsokService;
import se.statenspersonadressregister.personsok._2021.PersonsokService_Service;
import se.statenspersonadressregister.referensimplementation.installningar.OrganisationscertifikatInformation;
import se.statenspersonadressregister.referensimplementation.installningar.PersonsokInstallningar;
import se.statenspersonadressregister.referensimplementation.validering.ValidationHandlerResolver;
import se.statenspersonadressregister.schema.komponent.generellt.typ_1.JaNejTYPE;
import se.statenspersonadressregister.schema.komponent.metadata.identifieringsinformationws_1.IdentifieringsinformationTYPE;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.PersonsokningFragaTYPE;
import se.statenspersonadressregister.schema.personsok._2021_1.personsokningfraga.SPARPersonsokningFraga;
import se.statenspersonadressregister.schema.personsok._2021_1.personsokningsvar.SPARPersonsokningSvar;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Map;

import static com.sun.xml.ws.developer.JAXWSProperties.SSL_SOCKET_FACTORY;
import static java.util.Objects.isNull;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static se.statenspersonadressregister.referensimplementation.loggning.PersonsokExempelLogger.logSPARPersonsokningSvar;
import static se.statenspersonadressregister.referensimplementation.verktyg.OrganisationscertifikatSSLContext.createSSLContextMedOrganisationscertifikat;

/**
 * Referensimplementation till SPAR Personsök program-program, version 2021.1.
 * Använder Java-klasser genererade av JAXB från xsd-filer. För att generera klasser kör <i>mvn install</i>.
 * <p>
 * För information om detaljer och betydelser i fråga och svar, se gränssnittsmanualen på SPAR:s hemsida.
 *
 * @see <a href="https://www.statenspersonadressregister.se/">https://www.statenspersonadressregister.se/</a>
 */
public class PersonsokExempel {
    private static final Logger log = LoggerFactory.getLogger(PersonsokExempel.class);

    private final PersonsokInstallningar personsokInstallningar;

    private final se.statenspersonadressregister.schema.komponent.metadata.identifieringsinformationws_1.ObjectFactory
            identifieringsInformationFactory = new se.statenspersonadressregister.schema.komponent.metadata.identifieringsinformationws_1.ObjectFactory();
    private final se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.ObjectFactory
            personsokningSokParametrarFactory = new se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.ObjectFactory();

    /**
     * Skapar en instans av PersonsokExempel och kör demonstrationen
     */
    public static void main(String[] args) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        PersonsokInstallningar personsokInstallningar = new PersonsokInstallningar(args);
        new PersonsokExempel(personsokInstallningar).demonstration();
    }

    protected PersonsokService createClient(String url, OrganisationscertifikatInformation certifikatInformation) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        PersonsokService_Service service = new PersonsokService_Service();

        // Här sätts ValidationHandlerResolver som gör att anrop valideras före de skickas
        service.setHandlerResolver(new ValidationHandlerResolver());

        PersonsokService serviceSOAP = service.getPersonsokServiceSOAP();
        BindingProvider bindingProvider = (BindingProvider) serviceSOAP;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();

        // Här sätts att anrop ska använda SSLContext med organisationscertifikat
        requestContext.put(SSL_SOCKET_FACTORY, createSSLContextMedOrganisationscertifikat(certifikatInformation).getSocketFactory());

        // Här sätts vilken url som ska anropas
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, url);

        return serviceSOAP;
    }

    protected PersonsokExempel() {
        this(new PersonsokInstallningar());
    }

    public PersonsokExempel(PersonsokInstallningar installningar) {
        this.personsokInstallningar = installningar;
    }

    /**
     * Demonstration av referensimplementationen.
     */
    private void demonstration() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        log.info("Startar demonstration av Personsök program-program, version 2021.1");

        PersonsokService personsokClient = createClient(
                personsokInstallningar.getUrl(),
                createOrganisationscertifikatInformation());

        final String personId = "197910312391";
        log.debug("Sökning på personid " + personId);
        SPARPersonsokningSvar svarPersonIdSokning = personsokClient.personSok(
                createSPARPersonsokningFraga(
                        createIdentifieringsInformation(),
                        createPersonsokningFragaPersonId(personId)));

        logSPARPersonsokningSvar(svarPersonIdSokning);

        log.debug("Sökning på felaktigt personid");
        try {
            personsokClient.personSok(
                    createSPARPersonsokningFraga(
                            createIdentifieringsInformation(),
                            createPersonsokningFragaPersonId("000000000000")));
        } catch (WebServiceException e) {
            log.debug("Felaktigt personid ger WebServiceException", e);
        }

        log.debug("Fonetisk sökning, mikael");
        SPARPersonsokningSvar svarFonetiskt = personsokClient.personSok(
                createSPARPersonsokningFraga(
                        createIdentifieringsInformation(),
                        createPersonsokningFragaFonetiskNamnSok("mikael efter*")));
        logSPARPersonsokningSvar(svarFonetiskt);

        log.debug("Fonetisk sökning, inga träffar");
        SPARPersonsokningSvar svarFonetiskNollTraffar = personsokClient.personSok(
                createSPARPersonsokningFraga(
                        createIdentifieringsInformation(),
                        createPersonsokningFragaFonetiskNamnSok("dethärnamnetfinnsinteispar")));
        logSPARPersonsokningSvar(svarFonetiskNollTraffar);

        log.debug("Fonetisk sökning, överskridning av max antal träffar");
        SPARPersonsokningSvar svarFonetiskForManga = personsokClient.personSok(
                createSPARPersonsokningFraga(
                        createIdentifieringsInformation(),
                        createPersonsokningFragaFonetiskNamnSok("an*")));
        logSPARPersonsokningSvar(svarFonetiskForManga);
    }

    protected OrganisationscertifikatInformation createOrganisationscertifikatInformation() {
        String certifikatSokvag = personsokInstallningar.getCertifikatSokvag();
        if (isNull(certifikatSokvag) || certifikatSokvag.isEmpty()) {
            return null;
        } else {
            String certifikatLosenord = personsokInstallningar.getCertifikatLosenord();
            String caSokvag = personsokInstallningar.getCaSokvag();

            if (isNull(caSokvag) || caSokvag.isEmpty()) {
                return new OrganisationscertifikatInformation(
                        certifikatSokvag,
                        certifikatLosenord);
            } else {
                return new OrganisationscertifikatInformation(
                        certifikatSokvag,
                        certifikatLosenord,
                        caSokvag,
                        personsokInstallningar.getCaLosenord());
            }
        }
    }

    /**
     * Skapar och returnerar en SPARPersonsokningFraga, huvudelementet i en fråga till personsök i SPAR
     */
    protected SPARPersonsokningFraga createSPARPersonsokningFraga(IdentifieringsinformationTYPE identifieringsInformation,
                                                                  PersonsokningFragaTYPE personsokningFraga) {
        SPARPersonsokningFraga sparPersonsokningFraga = new SPARPersonsokningFraga();
        sparPersonsokningFraga.setIdentifieringsinformation(identifieringsInformation);
        sparPersonsokningFraga.setPersonsokningFraga(personsokningFraga);
        return sparPersonsokningFraga;
    }

    /**
     * Skapar och returnerar IdentifieringsInformation
     */
    protected IdentifieringsinformationTYPE createIdentifieringsInformation() {
        IdentifieringsinformationTYPE identifieringsInformationTYPE = identifieringsInformationFactory.createIdentifieringsinformationTYPE();
        identifieringsInformationTYPE.setKundNrLeveransMottagare(personsokInstallningar.getKundNrLeveransMottagare());
        identifieringsInformationTYPE.setKundNrSlutkund(personsokInstallningar.getKundNrSlutkund());
        identifieringsInformationTYPE.setUppdragId(personsokInstallningar.getUppdragsId());
        identifieringsInformationTYPE.setSlutAnvandarId(personsokInstallningar.getSlutAnvandarId());

        return identifieringsInformationTYPE;
    }

    /**
     * Skapar och returnerar en fråga på person- eller samordningsnummer
     */
    protected PersonsokningFragaTYPE createPersonsokningFragaPersonId(String personId) {
        PersonsokningFragaTYPE personsokningFraga = personsokningSokParametrarFactory.createPersonsokningFragaTYPE();
        personsokningFraga.setIdNummer(personId);
        return personsokningFraga;
    }

    /**
     * Skapar och returnerar en fråga på fonetiskt namn
     */
    protected PersonsokningFragaTYPE createPersonsokningFragaFonetiskNamnSok(String fonetisktNamn) {
        PersonsokningFragaTYPE personsokningFraga = personsokningSokParametrarFactory.createPersonsokningFragaTYPE();
        personsokningFraga.setFonetiskSokning(JaNejTYPE.JA);
        personsokningFraga.setNamnSokArgument(fonetisktNamn);
        return personsokningFraga;
    }
}
