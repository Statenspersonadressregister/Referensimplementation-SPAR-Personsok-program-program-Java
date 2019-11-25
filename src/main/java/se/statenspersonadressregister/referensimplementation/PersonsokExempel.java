package se.statenspersonadressregister.referensimplementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.statenspersonadressregister.personsok._2019.PersonsokService;
import se.statenspersonadressregister.personsok._2019.PersonsokService_Service;
import se.statenspersonadressregister.referensimplementation.installningar.KlientCertifikatInformation;
import se.statenspersonadressregister.referensimplementation.installningar.PersonsokInstallningar;
import se.statenspersonadressregister.referensimplementation.validering.ValidationHandlerResolver;
import se.statenspersonadressregister.schema.komponent.sok.identifieringsinformation_1.IdentifieringsInformationTYPE;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsfraga_1.SPARPersonsokningFraga;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.PersonsokningFragaTYPE;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsvar_1.SPARPersonsokningSvar;
import se.statenspersonadressregister.schema.komponent.sok.sokargument_1.FonetiskSokningTYPE;
import se.statenspersonadressregister.schema.komponent.sok.sokargument_1.PersonIdTYPE;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.WebServiceException;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static java.util.Objects.isNull;
import static javax.xml.ws.BindingProvider.ENDPOINT_ADDRESS_PROPERTY;
import static se.statenspersonadressregister.referensimplementation.loggning.PersonsokExempelLogger.logSPARPersonsokningSvar;
import static se.statenspersonadressregister.referensimplementation.verktyg.KlientCertifikatSSLContext.createSSLContextMedKlientCertfikikat;

/**
 * Referensimplementation till SPAR Personsök program-program, version 20160213.
 * Använder Java-klasser genererade av JAXB från xsd-filer. För att generera klasser kör <i>mvn install</i>.
 * <p>
 * För information om detaljer och betydelser i fråga och svar, se gränssnittsmanualen på SPAR:s hemsida.
 *
 * @see <a href="https://www.statenspersonadressregister.se/">https://www.statenspersonadressregister.se/</a>
 */
public class PersonsokExempel {
    private static final Logger log = LoggerFactory.getLogger(PersonsokExempel.class);

    private static final String SSL_SOCKET_FACTORY = "com.sun.xml.internal.ws.transport.https.client.SSLSocketFactory";

    private static final SimpleDateFormat TIDSTAMPEL_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");

    private final PersonsokInstallningar personsokInstallningar;
    private final DatatypeFactory datatypeFactory;

    private final se.statenspersonadressregister.schema.komponent.sok.identifieringsinformation_1.ObjectFactory
        identifieringsInformationFactory = new se.statenspersonadressregister.schema.komponent.sok.identifieringsinformation_1.ObjectFactory();
    private final se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.ObjectFactory
        personsokningSokParametrarFactory = new se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.ObjectFactory();

    /**
     * Skapar en instans av PersonsokExempel och kör demonstrationen
     */
    public static void main(String[] args) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        PersonsokInstallningar personsokInstallningar = new PersonsokInstallningar(args);
        new PersonsokExempel(personsokInstallningar).demonstration();
    }

    protected PersonsokService createClient(String url, KlientCertifikatInformation certifikatInformation) throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        PersonsokService_Service service = new PersonsokService_Service();

        // Här sätts ValidationHandlerResolver som gör att anrop valideras före de skickas
        service.setHandlerResolver(new ValidationHandlerResolver());

        PersonsokService serviceSOAP = service.getPersonsokServiceSOAP();
        BindingProvider bindingProvider = (BindingProvider) serviceSOAP;
        Map<String, Object> requestContext = bindingProvider.getRequestContext();

        // Här sätts att anrop ska använda SSLContext med klientcertifikatet
        requestContext.put(SSL_SOCKET_FACTORY, createSSLContextMedKlientCertfikikat(certifikatInformation).getSocketFactory());

        // Här sätts vilken url som ska anropas
        requestContext.put(ENDPOINT_ADDRESS_PROPERTY, url);

        return serviceSOAP;
    }

    protected PersonsokExempel() {
        this(new PersonsokInstallningar());
    }

    public PersonsokExempel(PersonsokInstallningar installningar) {
        this.personsokInstallningar = installningar;

        try {
            datatypeFactory = DatatypeFactory.newInstance();
        } catch (DatatypeConfigurationException e) {
            log.error("Kunde inte skapa DatatypeFactory", e);
            throw new IllegalStateException("Kunde inte skapa DatatypeFactory", e);
        }
    }

    /**
     * Demonstration av referensimplementationen.
     */
    private void demonstration() throws CertificateException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException, IOException {
        log.info("Startar demonstration av Personsök program-program, version 2019.1");

        PersonsokService personsokClient = createClient("https://kt-ext-ws.statenspersonadressregister.se/2019.1/", createKlientCertifikatInformation());

        log.debug("Sökning på personid 197910312391");
        SPARPersonsokningSvar svarPersonIdSokning = personsokClient.personSok(
            createSPARPersonsokningFraga(createIdentifieringsInformation(), createPersonsokningFragaPersonId("197910312391")));
        logSPARPersonsokningSvar(svarPersonIdSokning);

        log.debug("Sökning på felaktigt personid");
        try {
            personsokClient.personSok(
                createSPARPersonsokningFraga(createIdentifieringsInformation(), createPersonsokningFragaPersonId("000000000000")));
        } catch (WebServiceException e) {
            log.debug("Felaktigt personid ger WebServiceException", e);
        }

        log.debug("Fonetisk sökning, mikael");
        SPARPersonsokningSvar svarFonetiskt = personsokClient.personSok(
            createSPARPersonsokningFraga(createIdentifieringsInformation(), createPersonsokningFragaFonetiskNamnSok("mikael efter*")));
        logSPARPersonsokningSvar(svarFonetiskt);

        log.debug("Fonetisk sökning, inga träffar");
        SPARPersonsokningSvar svarFonetiskNollTraffar = personsokClient.personSok(
            createSPARPersonsokningFraga(createIdentifieringsInformation(), createPersonsokningFragaFonetiskNamnSok("dethärnamnetfinnsinteispar")));
        logSPARPersonsokningSvar(svarFonetiskNollTraffar);

        log.debug("Fonetisk sökning, överskridning av max antal träffar");
        SPARPersonsokningSvar svarFonetiskForManga = personsokClient.personSok(
            createSPARPersonsokningFraga(createIdentifieringsInformation(), createPersonsokningFragaFonetiskNamnSok("an*")));
        logSPARPersonsokningSvar(svarFonetiskForManga);
    }

    protected KlientCertifikatInformation createKlientCertifikatInformation() {
        String certifikatSokvag = personsokInstallningar.getCertifikatSokvag();
        if (isNull(certifikatSokvag) || certifikatSokvag.isEmpty()) {
            return null;
        } else {
            String certifikatLosenord = personsokInstallningar.getCertifikatLosenord();
            String caSokvag = personsokInstallningar.getCaSokvag();

            if (isNull(caSokvag) || caSokvag.isEmpty()) {
                return new KlientCertifikatInformation(
                    certifikatSokvag,
                    certifikatLosenord);
            } else {
                return new KlientCertifikatInformation(
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
    protected SPARPersonsokningFraga createSPARPersonsokningFraga(IdentifieringsInformationTYPE identifieringsInformation,
                                                                  PersonsokningFragaTYPE personsokningFraga) {
        SPARPersonsokningFraga sparPersonsokningFraga = new SPARPersonsokningFraga();
        sparPersonsokningFraga.setIdentifieringsInformation(identifieringsInformation);
        sparPersonsokningFraga.setPersonsokningFraga(personsokningFraga);
        return sparPersonsokningFraga;
    }

    /**
     * Skapar och returnerar IdentifieringsInformation
     */
    protected IdentifieringsInformationTYPE createIdentifieringsInformation() {
        IdentifieringsInformationTYPE identifieringsInformationTYPE = identifieringsInformationFactory.createIdentifieringsInformationTYPE();
        identifieringsInformationTYPE.setKundNrLeveransMottagare(personsokInstallningar.getKundNrLeveransMottagare());
        identifieringsInformationTYPE.setKundNrSlutkund(personsokInstallningar.getKundNrSlutkund());
        identifieringsInformationTYPE.setUppdragsId(personsokInstallningar.getUppdragsId());
        identifieringsInformationTYPE.setOrgNrSlutkund(personsokInstallningar.getOrgNrSlutkund());
        identifieringsInformationTYPE.setSlutAnvandarId(personsokInstallningar.getSlutAnvandarId());
        XMLGregorianCalendar tidsstampel = toXMLGregorianCalendarDate(new Date());
        identifieringsInformationTYPE.setTidsstampel(tidsstampel);

        log.debug("Skapat IdentifieringsInformation med tidstämpel [{}]", tidsstampel);
        return identifieringsInformationTYPE;
    }

    /**
     * Skapar en XMLGregorianCalendar som ger innehåll i format "yyyy-MM-dd'T'HH:mm:ss.SSS"
     */
    public XMLGregorianCalendar toXMLGregorianCalendarDate(Date date) {
        return Optional.ofNullable(date)
                .map(d -> TIDSTAMPEL_DATE_FORMAT.format(d))
                .map(str -> datatypeFactory.newXMLGregorianCalendar(str))
                .orElse(null);
    }

    /**
     * Skapar och returnerar en fråga på person- eller samordningsnummer
     */
    protected PersonsokningFragaTYPE createPersonsokningFragaPersonId(String personId) {
        PersonsokningFragaTYPE personsokningFraga = personsokningSokParametrarFactory.createPersonsokningFragaTYPE();
        PersonIdTYPE personIdTYPE = new PersonIdTYPE();
        personIdTYPE.setFysiskPersonId(personId);
        personsokningFraga.setPersonId(personIdTYPE);
        return personsokningFraga;
    }

    /**
     * Skapar och returnerar en fråga på fonetiskt namn
     */
    protected PersonsokningFragaTYPE createPersonsokningFragaFonetiskNamnSok(String fonetisktNamn) {
        PersonsokningFragaTYPE personsokningFraga = personsokningSokParametrarFactory.createPersonsokningFragaTYPE();
        personsokningFraga.setFonetiskSokning(FonetiskSokningTYPE.J);
        personsokningFraga.setNamnSokArgument(fonetisktNamn);
        return personsokningFraga;
    }
}
