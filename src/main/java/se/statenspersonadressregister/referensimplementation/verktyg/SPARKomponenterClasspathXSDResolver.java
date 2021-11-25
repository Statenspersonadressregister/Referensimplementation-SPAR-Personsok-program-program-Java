package se.statenspersonadressregister.referensimplementation.verktyg;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.nonNull;

/**
 * Används för att hämta XSD-filer från classpath istället för från internet
 */
public class SPARKomponenterClasspathXSDResolver implements LSResourceResolver {
    private static final Logger log = LoggerFactory.getLogger(SPARKomponenterClasspathXSDResolver.class);

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        if (isRelativeSPARPath(systemId, namespaceURI)) {
            return null;
        }

        if(isPart(systemId, "granssnitt")) {
            return classpathLSIput(publicId, systemId, baseURI, "granssnitt", "/se/statenspersonadressregister/granssnitt");
        }

        if(isPart(systemId, "komponent")) {
            return classpathLSIput(publicId, systemId, baseURI,"komponent", "/se/statenspersonadressregister/komponent");
        }

        log.warn("Kunde inte hitta, uppfyllde ingen defenierad path [{}] [{}] [{}] [{}] [{}]",
                 type,
                 namespaceURI,
                 publicId,
                 systemId,
                 baseURI);
        return null;
    }

    private LSInput classpathLSIput(String publicId, String systemId, String baseUri, String part, String localPath) {
        String[] xsdDelar = systemId.split(part);
        try {
            URL classpathUrl = this.getClass().getResource(localPath + xsdDelar[1]);
            return new ClasspathLSInput(publicId, systemId, baseUri, classpathUrl, UTF_8.name());
        } catch (IOException e) {
            log.warn("Kunde inte hitta [{}] [{}]", publicId, systemId, e);
            throw new IllegalArgumentException("Hittade inte " + systemId, e);
        }
    }

    private boolean isPart(String systemId, String partRegEx) {
        return systemId != null && systemId.split(partRegEx).length == 2;
    }

    private boolean isRelativeSPARPath(String systemId, String namespaceURI) {
        return nonNull(namespaceURI)
            && nonNull(systemId)
            && namespaceURI.startsWith("http://statenspersonadressregister.se/")
            && !systemId.startsWith("http");
    }

    public static class ClasspathLSInput implements LSInput {
        private final String publicId;
        private final String systemId;
        private final String baseUri;
        private final String encoding;
        private final URL classPathURL;
        private final InputStream byteStream;

        ClasspathLSInput(final String publicId,
                         final String systemId,
                         final String baseUri,
                         final URL classPathURL,
                         final String encoding) throws IOException {
            this.publicId = publicId;
            this.systemId = systemId;
            this.baseUri = baseUri;
            this.classPathURL = classPathURL;
            this.byteStream = classPathURL.openStream();
            this.encoding = encoding;
        }

        /**
         * När du vill ha classpath till schemafil, exempelvis till schema-validering i runtime.
         */
        public URL getClasspathURL() {
            return classPathURL;
        }

        public String getPublicId() {
            return publicId;
        }

        public String getSystemId() {
            return systemId;
        }

        public String getEncoding() {
            return encoding;
        }

        public String getBaseURI() {
            return baseUri;
        }

        public InputStream getByteStream() {
            return byteStream;
        }

        //
        // Funktioner nedan används inte
        //

        public boolean getCertifiedText() {
            // Krävs av LSInput-interfacet, därför tom implementation
            return false;
        }

        public Reader getCharacterStream() {
            // Krävs av LSInput-interfacet, därför tom implementation
            return null;
        }

        public String getStringData() {
            // Krävs av LSInput-interfacet, därför tom implementation
            return null;
        }

        public void setByteStream(InputStream byteStream) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setCharacterStream(Reader characterStream) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setStringData(String stringData) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setEncoding(String encoding) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setPublicId(String publicId) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setSystemId(String systemId) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setBaseURI(String baseURI) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }

        public void setCertifiedText(boolean certifiedText) {
            // Krävs av LSInput-interfacet, därför tom implementation
        }
    }
}
