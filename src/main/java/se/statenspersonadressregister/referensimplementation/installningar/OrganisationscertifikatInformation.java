package se.statenspersonadressregister.referensimplementation.installningar;

import static java.util.Objects.requireNonNull;

public class OrganisationscertifikatInformation {
    private final String organisationscertifikatSokvag;
    private final char[] organisationscertifikatLosenord;
    private final String cacertSokvag;
    private final char[] cacertLosenord;

    /**
     * Innehåller information om organisationscertifikatet
     *
     * @param organisationscertifikatSokvag   Sökväg till organisationscertifikatet, till exempel /opt/spar/personsok/organisationscertifikat.p12
     * @param organisationscertifikatLosenord Lösenord till organisationscertifikatet
     * @param cacertSokvag                    Sökväg till cacert
     * @param cacertLosenord                  Lösenord till cacert
     */
    public OrganisationscertifikatInformation(String organisationscertifikatSokvag, String organisationscertifikatLosenord, String cacertSokvag, String cacertLosenord) {
        this.organisationscertifikatSokvag = requireNonNull(organisationscertifikatSokvag);
        this.organisationscertifikatLosenord = requireNonNull(organisationscertifikatLosenord.toCharArray());
        this.cacertSokvag = requireNonNull(cacertSokvag);
        this.cacertLosenord = requireNonNull(cacertLosenord.toCharArray());
    }


    /**
     * Innehåller information om organisationscertifikatet, använder Javas standard cacert
     *
     * @param organisationscertifikatSokvag   Se {@link OrganisationscertifikatInformation#OrganisationscertifikatInformation(String, String, String, String) OrganisationsertifikatInformation}
     * @param organisationscertifikatLosenord Se {@link OrganisationscertifikatInformation#OrganisationscertifikatInformation(String, String, String, String) OrganisationsertifikatInformation}
     */
    public OrganisationscertifikatInformation(final String organisationscertifikatSokvag, final String organisationscertifikatLosenord) {
        this(organisationscertifikatSokvag,
                organisationscertifikatLosenord,
                System.getProperty("java.home") + "/lib/security/cacerts",
                "changeit");
    }

    public String getOrganisationscertifikatSokvag() {
        return organisationscertifikatSokvag;
    }

    public char[] getOrganisationscertifikatLosenord() {
        return organisationscertifikatLosenord;
    }

    public String getCacertSokvag() {
        return cacertSokvag;
    }

    public char[] getCacertLosenord() {
        return cacertLosenord;
    }
}
