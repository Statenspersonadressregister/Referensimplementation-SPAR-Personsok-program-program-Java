package se.statenspersonadressregister.referensimplementation;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import se.statenspersonadressregister.referensimplementation.installningar.KlientCertifikatInformation;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class KlientCertifikatInformationTest {
    private static KlientCertifikatInformation testKlientCertifikatInformation1;

    @BeforeAll
    static void setupTestdata() {
        testKlientCertifikatInformation1 = new KlientCertifikatInformation("/sokvag/till/certifikat", "h3mligt", "/sokvag/till/cacert", "1234");
    }

    @Test
    void getKlientCertifikatSokvag() {
        final String facit = "/sokvag/till/certifikat";
        assertEquals(facit, testKlientCertifikatInformation1.getKlientCertifikatSokvag());
    }

    @Test
    void getKlientCertifikatLosenord() {
        final char[] facit = new char[] {'h', '3', 'm', 'l', 'i', 'g', 't' };
        assertArrayEquals(facit, testKlientCertifikatInformation1.getKlientCertifikatLosenord());
    }

    @Test
    void getCacertSokvag() {
        final String facit = "/sokvag/till/cacert";
        assertEquals(facit, testKlientCertifikatInformation1.getCacertSokvag());
    }

    @Test
    void getCacertLosenord() {
        final char[] facit = new char[] {'1', '2', '3', '4'};
        assertArrayEquals(facit, testKlientCertifikatInformation1.getCacertLosenord());
    }

}