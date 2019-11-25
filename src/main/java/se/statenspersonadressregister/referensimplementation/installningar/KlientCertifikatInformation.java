package se.statenspersonadressregister.referensimplementation.installningar;

import static java.util.Objects.requireNonNull;

public class KlientCertifikatInformation {
    private final String klientCertifikatSokvag;
    private final char[] klientCertifikatLosenord;
    private final String cacertSokvag;
    private final char[] cacertLosenord;

    /**
     * Innehåller information om klientcertifikatet
     *
     * @param klientCertifikatSokvag Sökväg till klientcertifikatet, till exempel /opt/spar/personsok/klientcertifikat.p12
     * @param klientCertifikatLosenord Lösenord till klientcertifikatet
     * @param cacertSokvag Sökväg till cacert
     * @param cacertLosenord Lösenord till cacert
     */
    public KlientCertifikatInformation(String klientCertifikatSokvag, String klientCertifikatLosenord, String cacertSokvag, String cacertLosenord) {
        this.klientCertifikatSokvag = requireNonNull(klientCertifikatSokvag);
        this.klientCertifikatLosenord = requireNonNull(klientCertifikatLosenord.toCharArray());
        this.cacertSokvag = requireNonNull(cacertSokvag);
        this.cacertLosenord = requireNonNull(cacertLosenord.toCharArray());
    }


    /**
     * Innehåller information om klientcertifikatet, använder Javas standard cacert
     *
     * @param klientCertifikatSokvag Se {@link KlientCertifikatInformation#KlientCertifikatInformation(String, String, String, String) KlientCertifikatInformation}
     * @param klientCertifikatLosenord Se {@link KlientCertifikatInformation#KlientCertifikatInformation(String, String, String, String) KlientCertifikatInformation}
     */
    public KlientCertifikatInformation(final String klientCertifikatSokvag, final String klientCertifikatLosenord) {
        this(klientCertifikatSokvag,
             klientCertifikatLosenord,
             System.getProperty("java.home") + "/lib/security/cacerts",
             "changeit");
    }

    public String getKlientCertifikatSokvag() {
        return klientCertifikatSokvag;
    }

    public char[] getKlientCertifikatLosenord() {
        return klientCertifikatLosenord;
    }

    public String getCacertSokvag() {
        return cacertSokvag;
    }

    public char[] getCacertLosenord() {
        return cacertLosenord;
    }
}
