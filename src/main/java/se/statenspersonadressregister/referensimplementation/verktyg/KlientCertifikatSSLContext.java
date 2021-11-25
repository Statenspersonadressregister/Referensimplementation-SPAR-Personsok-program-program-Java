package se.statenspersonadressregister.referensimplementation.verktyg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.statenspersonadressregister.referensimplementation.installningar.KlientCertifikatInformation;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Optional;

/**
 * Hjälpklass för att anrop till SPAR personsök pgm pgm 2021.1 ska ske med klientcertifikat
 */
public class KlientCertifikatSSLContext {
    private static final Logger log = LoggerFactory.getLogger(KlientCertifikatSSLContext.class);

    private KlientCertifikatSSLContext() {
        // Använd createSSLContextMedKlientCertfikikat()
    }

    /**
     * Skapar SSLContext som kan använda klientcertifikat.
     */
    public static SSLContext createSSLContextMedKlientCertfikikat(KlientCertifikatInformation certifikatInformation) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        log.info("Skapar SSLContext med klientcertifikat [{}] och cacertifikat [{}]",
                 certifikatInformation.getKlientCertifikatSokvag(),
                 certifikatInformation.getCacertSokvag()
        );

        char[] klientCertifikatLosenord = getKlientCertfikatLosenord(certifikatInformation);

        KeyStore klientCertifikatStore = createKeyStore(certifikatInformation, klientCertifikatLosenord);
        KeyManagerFactory keyManagerFactory = createKeyManagerFactory(klientCertifikatStore, klientCertifikatLosenord);
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

    private static KeyStore createTrustStore(KlientCertifikatInformation certifikatInformation) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream caInputStream = new FileInputStream(certifikatInformation.getCacertSokvag())) {
            KeyStore trustStore = KeyStore.getInstance("JKS");
            trustStore.load(caInputStream, getCACertfikatLosenord(certifikatInformation));
            return trustStore;
        }
    }

    private static KeyManagerFactory createKeyManagerFactory(KeyStore clientStore, char[] klientCertifikatLosenord) throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(clientStore, klientCertifikatLosenord);
        return kmf;
    }

    private static KeyStore createKeyStore(KlientCertifikatInformation certifikatInformation, char[] klientCertifikatLosenord) throws KeyStoreException, IOException, NoSuchAlgorithmException, CertificateException {
        try (InputStream klientCertifikatInputStream = new FileInputStream(certifikatInformation.getKlientCertifikatSokvag())) {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            keyStore.load(klientCertifikatInputStream, klientCertifikatLosenord);
            return keyStore;
        }
    }

    private static char[] getKlientCertfikatLosenord(KlientCertifikatInformation certifikatInformation) {
        return Optional.ofNullable(certifikatInformation.getKlientCertifikatLosenord())
                .filter(l -> l.length > 0)
                .orElse(null);
    }

    private static char[] getCACertfikatLosenord(KlientCertifikatInformation certifikatInformation) {
        return Optional.ofNullable(certifikatInformation.getCacertLosenord())
                .filter(l -> l.length > 0)
                .orElse(null);
    }
}
