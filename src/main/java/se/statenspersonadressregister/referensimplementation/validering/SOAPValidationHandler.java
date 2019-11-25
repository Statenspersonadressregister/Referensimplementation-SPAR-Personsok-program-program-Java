package se.statenspersonadressregister.referensimplementation.validering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.ls.LSInput;
import org.xml.sax.SAXException;
import se.statenspersonadressregister.referensimplementation.verktyg.SPARKomponenterClasspathXSDResolver;
import se.statenspersonadressregister.referensimplementation.verktyg.SPARKomponenterClasspathXSDResolver.ClasspathLSInput;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.ws.LogicalMessage;
import javax.xml.ws.WebServiceException;
import javax.xml.ws.handler.LogicalHandler;
import javax.xml.ws.handler.LogicalMessageContext;
import javax.xml.ws.handler.MessageContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Objects;
import java.util.Optional;

import static java.util.Objects.requireNonNull;
import static javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI;
import static javax.xml.ws.handler.MessageContext.MESSAGE_OUTBOUND_PROPERTY;
import static se.statenspersonadressregister.referensimplementation.validering.SOAPValidationHandler.ValidationDirection.SOAP_REQUEST;
import static se.statenspersonadressregister.referensimplementation.validering.SOAPValidationHandler.ValidationDirection.SOAP_RESPONSE;

/**
 * SOAPValidationHandler används för att validera utgående anrop är korrekt innan anropet görs.
 */
public class SOAPValidationHandler implements LogicalHandler<LogicalMessageContext> {
    private static final Logger log = LoggerFactory.getLogger(SOAPValidationHandler.class);

    public enum ValidationDirection {
        SOAP_REQUEST,
        SOAP_RESPONSE
    }

    private final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    private final Validator schemaValidator;

    private final String schemaURL;
    private final String qNameLocalPart;
    private final ValidationDirection validationDirection;
    private final Transformer trans;

    public SOAPValidationHandler(String qNameLocalPart,
                                 String schemaURL,
                                 ValidationDirection direction) throws SAXException, TransformerConfigurationException {
        this.qNameLocalPart = requireNonNull(qNameLocalPart);
        this.schemaURL = requireNonNull(schemaURL);
        this.validationDirection = requireNonNull(direction);

        documentBuilderFactory.setNamespaceAware(true);
        schemaValidator = getValidator();
        trans = TransformerFactory.newInstance().newTransformer();
    }

    @Override
    public boolean handleMessage(LogicalMessageContext logicalMessageContext) {
        if (!(shouldValidateMessage(logicalMessageContext))) {
            return true;
        }

        try {
            validera(logicalMessageContext);
        } catch (TransformerException | TransformerFactoryConfigurationError | IOException | ParserConfigurationException e) {
            log.error("Fel vid validering av {}", validationDirection, e);
            throw new WebServiceException(e.getMessage(), e);
        } catch (SAXException e) {
             log.error("Fel vid validering av {}, felaktig xml", validationDirection, e);
             throw new WebServiceException(e.getMessage(), e);
        }

        log.info("Validering av {} {} {} ", qNameLocalPart, schemaURL, validationDirection);
        return true;
    }

    private boolean shouldValidateMessage(LogicalMessageContext logicalMessageContext) {
        return isCorrectDirection(logicalMessageContext)
            && isCorrectQNameLocalPart(logicalMessageContext);
    }

    private boolean isCorrectQNameLocalPart(LogicalMessageContext logicalMessageContext) {
        String localPart = getQNameLocalPart(logicalMessageContext);
        return Objects.equals(this.qNameLocalPart, localPart);
    }

    /**
     * Validera bara om riktning på meddelande och validering är samma
     */
    private boolean isCorrectDirection(LogicalMessageContext logicalMessageContext) {
        final boolean messageOutgoing = (boolean) logicalMessageContext.get(MESSAGE_OUTBOUND_PROPERTY);
        final boolean messageIncoming = !(messageOutgoing);

        if (messageOutgoing && validationDirection == SOAP_REQUEST) {
            return true;
        }

        if (messageIncoming && validationDirection == SOAP_RESPONSE) {
            return true;
        }

        return false;
    }

    private String getQNameLocalPart(LogicalMessageContext logicalMessageContext) {
        return Optional.ofNullable((QName) logicalMessageContext.get(MessageContext.WSDL_OPERATION))
                .map(qName -> qName.getLocalPart())
                .orElse(null);
    }

    private void validera(LogicalMessageContext logicalMessageContext) throws TransformerException, ParserConfigurationException, IOException, SAXException {
        LogicalMessage lm = logicalMessageContext.getMessage();
        Source payload = lm.getPayload();

        StreamResult res = new StreamResult(new StringWriter());
        trans.transform(payload, res);

        String message = res.getWriter().toString();
        validera(message);
    }

    private void validera(String xml) throws ParserConfigurationException, IOException, SAXException {
        schemaValidator.validate(new DOMSource(skapaDocument(xml)));
    }

    private Document skapaDocument(final String xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilder parser = documentBuilderFactory.newDocumentBuilder();
        return parser.parse(new ByteArrayInputStream(xml.getBytes()));
    }

    private Validator getValidator() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(W3C_XML_SCHEMA_NS_URI);

        SPARKomponenterClasspathXSDResolver classpathResolver = new SPARKomponenterClasspathXSDResolver();
        factory.setResourceResolver(classpathResolver);

        LSInput lsInput = classpathResolver.resolveResource(null, null, null, schemaURL, null);

        Schema schema = factory.newSchema(((ClasspathLSInput) lsInput).getClasspathURL());
        Validator val = schema.newValidator();
        val.setResourceResolver(classpathResolver);
        return val;
    }

    @Override
    public boolean handleFault(LogicalMessageContext logicalMessageContext) {
        // true = kör vidare i kedjan
        return true;
    }

    @Override
    public void close(MessageContext messageContext) {
        // Måste finnas pga interfacet
    }
}
