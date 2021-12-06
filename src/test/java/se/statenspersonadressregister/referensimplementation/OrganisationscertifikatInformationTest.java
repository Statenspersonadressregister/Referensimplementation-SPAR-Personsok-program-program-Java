package se.statenspersonadressregister.referensimplementation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.statenspersonadressregister.referensimplementation.installningar.OrganisationscertifikatInformation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class OrganisationscertifikatInformationTest {
    private static OrganisationscertifikatInformation testOrganisationscertifikatInformation;

    @BeforeAll
    static void setupTestdata() {
        testOrganisationscertifikatInformation = new OrganisationscertifikatInformation("/sokvag/till/certifikat", "h3mligt", "/sokvag/till/cacert", "1234");
    }

    @Test
    void getOrganisationscertifikatSokvag() {
        final String facit = "/sokvag/till/certifikat";
        assertEquals(facit, testOrganisationscertifikatInformation.getOrganisationscertifikatSokvag());
    }

    @Test
    void getOrganisationscertifikatLosenord() {
        final char[] facit = new char[]{'h', '3', 'm', 'l', 'i', 'g', 't'};
        assertArrayEquals(facit, testOrganisationscertifikatInformation.getOrganisationscertifikatLosenord());
    }

    @Test
    void getCacertSokvag() {
        final String facit = "/sokvag/till/cacert";
        assertEquals(facit, testOrganisationscertifikatInformation.getCacertSokvag());
    }

    @Test
    void getCacertLosenord() {
        final char[] facit = new char[]{'1', '2', '3', '4'};
        assertArrayEquals(facit, testOrganisationscertifikatInformation.getCacertLosenord());
    }

}