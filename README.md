# Referensimplementation SPAR Personsök program-program

> **Obeservera:** Referensimplementationer för senare versioner av SPAR Personsök program-program finns på https://www.statenspersonadressregister.se  
> Denna Github-organisation kommer att upphöra per 2024-05-31.

Denna källkod är en referensimplementation av SPAR Personsök program-program version _2021.1_.

Referensimplementationen är skriven för _Java 11_ och använder _Maven_ för projekthantering. 
_JAX-WS_ används för att skapa Javakod från WSDL-fil och XML-schemafiler (.xsd). 
Utöver det används _Logback_ och _SLF4J_ för att hantera loggning samt _JUnit 5_ för att hantera enhetstestning av koden.

För mer information om SPAR på såväl verksamhets- som teknisk nivå se 
[SPARs hemsida](https://www.statenspersonadressregister.se).

## Användning

Kör `mvn clean install` i samma bibliotek som _pom.xml_ för att hämta hem beroenden och från xsd-filer skapa Java-klasser. 
Med detta kommando körs även testerna, vilket gör att anrop kommer skickas till kundtestmiljön.

_PersonsokExempel_ innehåller en demonstration som gör fyra olika sökningar och loggar utförligt ut resultatet.

_OrganisationsertifikatInformationTest_ har fyra tester som kör mot kundtestmiljön. Dessa verifierar att inget går fel. 

### Kundtest

Vi rekommenderar att det organisationscertifikat som är tänkt att användas i produktion även används vid tester mot kundtestmiljön, 
detta för att i ett tidigt skede verifiera att certifikatet är korrekt.

### Organisationscertifikat

För att på ett enkelt sätt använda rätt organisationscertifikat vid anslutningen kan _OrganisationscertifikatInformation_ användas. 
Denna vill ha organisationscertifikat i _PKCS12-format (.p12)_. Det går också att använda Javas _keytool_ för att lägga till 
ett organisationscertifikat, men detta alternativ avråder vi från.

### Rootcertifikat

_OrganisationscertifikatInformation_ kan även hantera rootcertifikat för att verifiera SPAR:s identitet. Denna vill ha 
rootcertifikatet i _PKCS12-format (.p12)_. Vi rekommenderar att verifiering av rootcertifikatet görs även om en annan lösning används.

### Produktion

Om koden används för att integrera mot produktionsmiljön krävs ett giltigt organisationscertifikat, det inkluderade 
testcertifikatet fungerar endast i kundtestmiljön. Även indentifieringsinformation behöver vara giltig, 
se _KundNrLeveransMottagare_, _KundNrSlutkund_ och _UppdragsId_. För mer information kontakta SPAR:s kundtjänst.

### HTTP Request/Response

Om du vill se rå HTTP-data, headers och body, från fråga och svar, använd nedan flagga.

```sh
-Dcom.sun.xml.ws.transport.http.client.HttpTransportPipe.dump=true
```