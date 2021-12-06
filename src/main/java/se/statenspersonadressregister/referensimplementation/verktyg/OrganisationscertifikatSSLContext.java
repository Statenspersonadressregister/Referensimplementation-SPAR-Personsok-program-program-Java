package se.statenspersonadressregister.referensimplementation.verktyg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.statenspersonadressregister.referensimplementation.installningar.OrganisationscertifikatInformation;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.*;
import java.security.cert.CertificateException;

import static java.util.Optional.ofNullable;

/**
 * Hjälpklass för att anrop till SPAR personsök pgm pgm 2021.1 ska ske med organisationscertifikat
 */
public class OrganisationscertifikatSSLContext {
    private static final Logger log = LoggerFactory.getLogger(OrganisationscertifikatSSLContext.class);

    private OrganisationscertifikatSSLContext() {
        // Använd createSSLContextMedKlientCertfikikat()
    }

    /**
     * Skapar SSLContext som kan använda organisationscertifikat.
     */
    public static SSLContext createSSLContextMedOrganisationscertifikat(OrganisationscertifikatInformation certifikatInformation) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        log.info("Skapar SSLContext med organisationscertifikat [{}] och cacertifikat [{}]",
                certifikatInformation.getOrganisationscertifikatSokvag(),
                certifikatInformation.getCacertSokvag()
        );

        char[] organisationscertifikatLosenord = getOrganisationscertifikatLosenord(certifikatInformation);

        KeyStore organisationscertifikatStore = createKeyStore(certifikatInformation, organisationscertifikatLosenord);
        KeyManagerFactory keyManagerFactory = createKeyManagerFactory(organisationscertifikatStore, organisationscertifikatLosenord);
        KeyStore trustStore = createTrustStore(certifikatInformation);
        TrustManagerFactory trustManagerFactory = createTrustManagerFactory(trustStore);

        SSLContext sslContext = createSSLContext(keyManagerFactory, trustManagerFactory);
        return sslContext;
    }

    private static SSLContext createSSLContext(KeyManagerFactory kmf, TrustManagerFactory tmf) throws NoSuchAlgorithmException, KeyManagementException {
        SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private static TrustManagerFactory createTrustManagerFactory(KeyStore trustStore) throws NoSuchAlgorithmException, KeyStoreException {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);
        return tmf;
    }

    private static KeyStore createTrustStore(OrganisationscertifikatInformation certifikatInformation) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream caInputStream = new FileInputStream(certifikatInformation.getCacertSokvag())) {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(caInputStream, getCACertfikatLosenord(certifikatInformation));
            return trustStore;
        }
    }

    private static KeyManagerFactory createKeyManagerFactory(KeyStore clientStore, char[] organisationscertifikatLosenord) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, organisationscertifikatLosenord);
        return kmf;
    }

    private static KeyStore createKeyStore(OrganisationscertifikatInformation certifikatInformation, char[] organisationscertifikatLosenord) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream organisationscertifikatInputStream = new FileInputStream(certifikatInformation.getOrganisationscertifikatSokvag())) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(organisationscertifikatInputStream, organisationscertifikatLosenord);
            return keyStore;
        }
    }

    private static char[] getOrganisationscertifikatLosenord(OrganisationscertifikatInformation certifikatInformation) {
        return ofNullable(certifikatInformation.getOrganisationscertifikatLosenord())
                .filter(l -> l.length > 0)
                .orElse(null);
    }

    private static char[] getCACertfikatLosenord(OrganisationscertifikatInformation certifikatInformation) {
        return ofNullable(certifikatInformation.getCacertLosenord())
                .filter(l -> l.length > 0)
                .orElse(null);
    }
}
