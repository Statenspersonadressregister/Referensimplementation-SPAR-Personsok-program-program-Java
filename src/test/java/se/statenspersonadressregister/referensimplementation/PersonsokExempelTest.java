package se.statenspersonadressregister.referensimplementation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.statenspersonadressregister.personsok._2019.PersonsokService;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsfraga_1.SPARPersonsokningFraga;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsokparametrar_1.PersonsokningFragaTYPE;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsvar_1.SPARPersonsokningSvar;

import javax.xml.ws.WebServiceException;

import static org.junit.jupiter.api.Assertions.*;

class PersonsokExempelTest {

    private static PersonsokExempel personsokExempel;
    private static PersonsokService personsokKlient;

    @BeforeAll
    static void setup() throws Exception {
        personsokExempel = new PersonsokExempel();
        personsokKlient = personsokExempel.createClient("https://kt-ext-ws.statenspersonadressregister.se/2019.1/", personsokExempel
            .createKlientCertifikatInformation());
    }

    @Test
    void sokningGiltigtPersonId() {
        PersonsokningFragaTYPE personsokningType = personsokExempel.createPersonsokningFragaPersonId("197910312391");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
            personsokExempel.createIdentifieringsInformation(),
            personsokningType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(1, svar.getPersonsokningSvarsPost().size());
        assertEquals("Jerry Felipe", svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().get(0).getFornamn());
        assertEquals("Efternamn3663", svar.getPersonsokningSvarsPost().get(0).getPersondetaljer().get(0).getEfternamn());
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
        assertEquals(0, svar.getUndantag().size());
        assertTrue(svar.getPersonsokningSvarsPost().size() > 1);
    }

    @Test
    void sokningFonetiskNollTraffar() {
        PersonsokningFragaTYPE personsokningFragaType = personsokExempel.createPersonsokningFragaFonetiskNamnSok("dethärnamnetfinnsinteispar");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
            personsokExempel.createIdentifieringsInformation(),
            personsokningFragaType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(0, svar.getPersonsokningSvarsPost().size());
    }

    @Test
    void sokningFonetiskForMangaTraffar() {
        PersonsokningFragaTYPE personsokningFragaType = personsokExempel.createPersonsokningFragaFonetiskNamnSok("an*");

        SPARPersonsokningFraga sparPersonsokningFraga = personsokExempel.createSPARPersonsokningFraga(
            personsokExempel.createIdentifieringsInformation(),
            personsokningFragaType);
        SPARPersonsokningSvar svar = personsokKlient.personSok(sparPersonsokningFraga);

        assertNotNull(svar.getOverstigerMaxAntalSvarsposter());
        assertEquals(0, svar.getUndantag().size());
        assertEquals(0, svar.getPersonsokningSvarsPost().size());
        assertTrue(svar.getOverstigerMaxAntalSvarsposter().getAntalPoster() > 100);
        assertEquals(100, svar.getOverstigerMaxAntalSvarsposter().getMaxAntalSvarsPoster());
    }
}