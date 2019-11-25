# Referensimplementation SPAR Personsök program-program

Denna källkod är en referensimplementation av SPAR Personsök program-program version _2019.1_.

Referensimplementationen är skriven för _Java 8_ och använder _Maven_ för projekthantering. 
_JAX-WS_ används för att skapa Javakod från WSDL-fil och XML-schemafiler (.xsd). 
Utöver det används _Logback_ och _SLF4J_ för att hantera loggning samt _JUnit 5_ för att hantera enhetstestning av koden.

För mer detaljer om verksamhetsbegrepp inom SPAR, och även andra tjänster inom SPAR se gränssnittsmanualen som är tillgänglig på 
[SPARs hemsida](https://www.statenspersonadressregister.se).

## Användning

Kör `mvn clean install` i samma bibliotek som _pom.xml_ för att hämta hem beroenden och från xsd-filer skapa Java-klasser. 
Med detta kommando körs även testerna, vilket gör att anrop kommer skickas till kundtestmiljön.

_PersonsokExempel_ innehåller en demonstration som gör fyra olika sökningar och loggar utförligt ut resultatet.

_KlientCertifikatInformationTest_ har fyra tester som kör mot kundtestmiljön. Dessa verifierar att inget går fel. 

### Kundtest

Vi rekommenderar att det klientcertifikat som är tänkt att användas i produktion även används vid tester mot kundtestmiljön, 
detta för att i ett tidigt skede verifiera att certifikatet är korrekt.

### Klientcertifikat

För att på ett enkelt sätt använda rätt klientcertifikat vid anslutningen kan _KlientCertifikatInformation_ användas. 
Denna vill ha klientcertifikatet i _PKCS12-format (.p12)_. Det går också att använda Javas _keytool_ för att lägga till 
ett klientcertifikat, men detta alternativ avråder vi från.

### Rootcertifikat

_KlientCertifikatInformation_ kan även hantera rootcertifikat för att verifiera SPAR:s identitet. Denna vill ha 
rootcertifikatet i _PKCS12-format (.p12)_. Vi rekommenderar att verifiering av rootcertifikatet görs även om en annan lösning används.

### Produktion

Om koden används för att integrera mot produktionsmiljön krävs ett giltigt klientcertifikat, det inkluderade 
testcertifikatet fungerar endast i kundtestmiljön. Även indentifieringsinformation behöver vara giltig, 
se _KundNrLeveransMottagare_, _KundNrSlutkund_ och _UppdragsId_. För mer information kontakta SPAR:s kundtjänst.

### Docker

Medföljande _Dockerfile_ kan användas för att bygga en Dockerimage. Denna kan användas för att starta en container som kör 
_PersonsokExempel_, som gör ett antal sökningar mot kundtest.
  
För mer information om Docker, se [https://www.docker.com/](https://www.docker.com/).

### HTTP Request/Response

Om du vill se rå HTTP-data, headers och body, från fråga och svar, använd nedan flagga.

```bash
-Dcom.sun.xml.internal.ws.transport.http.client.HttpTransportPipe.dump=true
```