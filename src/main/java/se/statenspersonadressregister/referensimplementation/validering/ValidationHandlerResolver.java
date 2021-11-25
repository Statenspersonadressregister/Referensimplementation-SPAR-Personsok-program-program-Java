package se.statenspersonadressregister.referensimplementation.validering;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import java.util.List;

import static java.util.Collections.singletonList;
import static se.statenspersonadressregister.referensimplementation.validering.SOAPValidationHandler.ValidationDirection.SOAP_REQUEST;

/**
 * En handlerchain som innehåller SOAPValidationHandler för att validera att utgående frågor är korrekta
 */
public class ValidationHandlerResolver implements HandlerResolver {
    private static final Logger log = LoggerFactory.getLogger(ValidationHandlerResolver.class);

    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        try {
            return singletonList(new SOAPValidationHandler("PersonSok",
                                                           "http://xmls.statenspersonadressregister.se/se/spar/granssnitt/personsok/2021.1/PersonsokningFraga.xsd",
                                                           SOAP_REQUEST));
        } catch (SAXException | TransformerConfigurationException e) {
            log.error("Kunde inte skapa SOAPValidationHandler", e);
            throw new IllegalStateException("Kunde inte skapa SOAPValidationHandler", e);
        }
    }
}
