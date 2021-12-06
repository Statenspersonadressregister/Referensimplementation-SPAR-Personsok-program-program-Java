package se.statenspersonadressregister.referensimplementation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.statenspersonadressregister.personsok._2021.PersonsokService;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.PersonsokningFragaTYPE;
import se.statenspersonadressregister.schema.personsok._2021_1.personsokningfraga.SPARPersonsokningFraga;
import se.statenspersonadressregister.schema.personsok._2021_1.personsokningsvar.SPARPersonsokningSvar;

import javax.xml.ws.WebServiceException;

import static org.junit.jupiter.api.Assertions.*;

class PersonsokExempelTest {

    private static PersonsokExempel personsokExempel;
    private static PersonsokService personsokKlient;

    @BeforeAll
    static void setup() throws Exception {
        personsokExempel = new PersonsokExempel();
        personsokKlient = personsokExempel.createClient(
                "https://kt-ext-ws.statenspersonadressregister.se/2021.1/",
                personsokExempel.createOrganisationscertifikatInformation());
    }

    @Test
    void sokningGiltigtPersonId() {
        PersonsokningFragaTYPE personsokningType = personsokExempel.createPersonsokningFragaPersonId("197910312391");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
                personsokExempel.createIdentifieringsInformation(),
                personsokningType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertNull(svar.getUndantag());
        assertEquals(1, svar.getPersonsokningSvarspost().size());
        assertEquals("Jerry Felipe", svar.getPersonsokningSvarspost().get(0).getNamn().get(0).getFornamn());
        assertEquals("Efternamn3663", svar.getPersonsokningSvarspost().get(0).getNamn().get(0).getEfternamn());
    }

    @Test
    void sokningFelaktigtPersonId() {
        assertThrows(WebServiceException.class, () -> {
            PersonsokningFragaTYPE personsokningType = personsokExempel.createPersonsokningFragaPersonId("000000000000");

            SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
                    personsokExempel.createIdentifieringsInformation(),
                    personsokningType);
            SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);
        });
    }

    @Test
    void sokningFonetisk() {
        PersonsokningFragaTYPE personsokningFragaType = personsokExempel.createPersonsokningFragaFonetiskNamnSok("mikael");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
                personsokExempel.createIdentifieringsInformation(),
                personsokningFragaType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertNull(svar.getUndantag());
        assertTrue(svar.getPersonsokningSvarspost().size() > 1);
    }

    @Test
    void sokningFonetiskNollTraffar() {
        PersonsokningFragaTYPE personsokningFragaType = personsokExempel.createPersonsokningFragaFonetiskNamnSok("dethärnamnetfinnsinteispar");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
                personsokExempel.createIdentifieringsInformation(),
                personsokningFragaType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertNull(svar.getUndantag());
        assertEquals(0, svar.getPersonsokningSvarspost().size());
    }

    @Test
    void sokningFonetiskForMangaTraffar() {
        PersonsokningFragaTYPE personsokningFragaType = personsokExempel.createPersonsokningFragaFonetiskNamnSok("an*");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
                personsokExempel.createIdentifieringsInformation(),
                personsokningFragaType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNotNull(svar.getOverstigerMaxAntalSvarsposter());
        assertNull(svar.getUndantag());
        assertEquals(0, svar.getPersonsokningSvarspost().size());
        assertTrue(svar.getOverstigerMaxAntalSvarsposter().getAntalPoster() > 100);
        assertEquals(100, svar.getOverstigerMaxAntalSvarsposter().getMaxAntalSvarsposter());
    }
}