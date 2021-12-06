package se.statenspersonadressregister.referensimplementation.loggning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.statenspersonadressregister.schema.komponent.person.adress.deladeadresselement_1.InternationellAdressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.deladeadresselement_1.SvenskAdressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.folkbokforingsadress_1.FolkbokforingsadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.kontaktadress_1.KontaktadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.sarskildpostadress_1.SarskildPostadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.utlandsadress_1.UtlandsadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.aviseringpost_1.AviseringPostTYPE;
import se.statenspersonadressregister.schema.komponent.person.fastighetstaxering_1.FastighetDelTYPE;
import se.statenspersonadressregister.schema.komponent.person.fastighetstaxering_1.FastighetTYPE;
import se.statenspersonadressregister.schema.komponent.person.folkbokforing_1.FolkbokforingTYPE;
import se.statenspersonadressregister.schema.komponent.person.hanvisning_1.HanvisningTYPE;
import se.statenspersonadressregister.schema.komponent.person.namn.namn_1.NamnTYPE;
import se.statenspersonadressregister.schema.komponent.person.persondetaljer_1.PersondetaljerTYPE;
import se.statenspersonadressregister.schema.komponent.person.relation_1.RelationTYPE;
import se.statenspersonadressregister.schema.personsok._2021_1.personsokningsvar.SPARPersonsokningSvar;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import static java.util.Objects.nonNull;
import static se.statenspersonadressregister.referensimplementation.loggning.PersonsokExempelLogTexter.*;

/**
 * Klass för att logga ett svar från SPAR personsökning pgm pgm 2021.1
 */
@SuppressWarnings("StringConcatenationInsideStringBufferAppend")
public class PersonsokExempelLogger {
    private static final Logger log = LoggerFactory.getLogger(PersonsokExempelLogger.class);

    private PersonsokExempelLogger() {
        // Använd logSPARPersonsokningSvar()
    }

    /**
     * Loggar ett personsökningarsvar
     */
    public static void logSPARPersonsokningSvar(SPARPersonsokningSvar svar) {
        StringBuilder sb = new StringBuilder(SPARPERSONSOKNING_SVAR);
        sb.append(MELLANSLAG + svar.getPersonsokningSvarspost().size() + SOKTRAFFAR);

        maybeAppend(sb, svar.getOverstigerMaxAntalSvarsposter(), overstiger ->
                OVERSTIGER_MAX_ANTAL_TRAFFAR + overstiger.getAntalPoster() +
                        TRAFFAR_AV_MAX + overstiger.getMaxAntalSvarsposter() + RADBRYT);

        maybeAppend(sb, svar.getUndantag(), undantag -> UNDANTAG + KOD + undantag.getKod() + BESKRIVNING + undantag.getBeskrivning() + RADBRYT);
        svar.getPersonsokningSvarspost().forEach(aviseringsPost -> logAviseringsPost(sb, aviseringsPost));
        maybeAppend(sb, svar.getUUID(), uuid -> UUID + uuid + RADBRYT);

        log.debug(sb.toString());
    }

    /**
     * Lägger till loggningstext om aviseringsport till StringBuilder
     */
    protected static void logAviseringsPost(StringBuilder sb, AviseringPostTYPE aviseringsPost) {
        sb.append(AVISERINGSPOST + RADBRYT);
        maybeAppend(sb, aviseringsPost.getPersonId(), id -> ID_NUMMER + id.getIdNummer() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getPersonId(), id -> ID_TYP + id.getTyp() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSenasteAndringSPAR(), senast -> SENASTE_ANDRING_SPAR + senast + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessmarkering(), sek -> SEKRETESSMARKERING1 + sek.getValue().value() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessmarkering().getSattAvSPAR(), sattAvSpar -> SEKRETESS_SATT_AV_SPAR1 + sattAvSpar.value() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessDatum(), datum -> SEKRETESS_ANDRINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSkyddadFolkbokforing(), sfb -> SKYDDAD_FOLKBOKFORING1 + sfb + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSkyddadFolkbokforingDatum(), datum -> SKYDDAD_FOLKBOKFORING_ANDRINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, aviseringsPost.getInkomstAr(), ar -> INKOMST_AR + ar + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSummeradInkomst(), inkomst -> SUMMERAD_INKOMST + inkomst + RADBRYT);

        aviseringsPost.getNamn().forEach(namn -> logNamn(sb, namn));
        aviseringsPost.getPersondetaljer().forEach(persondetalj -> logPersonDetalj(sb, persondetalj));
        aviseringsPost.getFolkbokforing().forEach(fb -> logFolkbokforing(sb, fb));
        logFolkbokforingsadresser(sb, aviseringsPost.getFolkbokforingsadress());
        logSarskildPostadress(sb, aviseringsPost.getSarskildPostadress());
        logUtlandsadress(sb, aviseringsPost.getUtlandsadress());
        logKontaktadress(sb, aviseringsPost.getKontaktadress());
        aviseringsPost.getRelation().forEach(relation -> logRelation(sb, relation));
        aviseringsPost.getFastighet().forEach(fastighet -> logFastighet(sb, fastighet));
    }

    private static void logFolkbokforingsadresser(StringBuilder sb, List<FolkbokforingsadressTYPE> folkbokforingsadresser) {
        // Folkbokföringsadress är alltid svensk, dvs på formatet svensk adress
        folkbokforingsadresser.forEach(adr -> {
                    sb.append(FOLKBOKFORINGSADRESS + RADBRYT);
                    logSvenskadress(sb, adr.getSvenskAdress());
                }
        );
    }

    private static void logSarskildPostadress(StringBuilder sb, List<SarskildPostadressTYPE> sarskildaPostadresser) {
        sarskildaPostadresser.forEach(adr -> {
            if (nonNull(adr.getSvenskAdress())) {
                sb.append(SARSKILD_POSTADRESS + RADBRYT);
                logSvenskadress(sb, adr.getSvenskAdress());
            } else {
                sb.append(SARSKILD_POSTADRESS_INTERNATIONELL + RADBRYT);
                logInternationellAdress(sb, adr.getInternationellAdress());
            }
        });
    }

    private static void logUtlandsadress(StringBuilder sb, List<UtlandsadressTYPE> utlandsadresser) {
        // Utlandsadress är alltid på formatet internationell, men denna kan innehålla adress till såväl Sverige som utlandet
        utlandsadresser.forEach(adr -> {
            sb.append(UTLANDSADRESS + RADBRYT);
            logInternationellAdress(sb, adr.getInternationellAdress());
        });
    }

    private static void logKontaktadress(StringBuilder sb, List<KontaktadressTYPE> kontaktadresser) {
        kontaktadresser.forEach(adr -> {
            if (nonNull(adr.getSvenskAdress())) {
                sb.append(KONTAKTADRESS + RADBRYT);
                logSvenskadress(sb, adr.getSvenskAdress());
            } else {
                sb.append(KONTAKTADRESS_INTERNATIONELL + RADBRYT);
                logInternationellAdress(sb, adr.getInternationellAdress());
            }
        });
    }

    /**
     * Lägger till loggningstext om fastighet till StringBuilder
     */
    protected static void logFastighet(StringBuilder sb, FastighetTYPE fastighet) {
        sb.append(FASTIGHET + RADBRYT);
        maybeAppend(sb, fastighet.getFastighetKod(), kod -> FASTIGHETSKOD + kod + RADBRYT);
        maybeAppend(sb, fastighet.getTaxeringsenhetsnummer(), nummer -> TAXERINGSENHETSNUMMER + nummer + RADBRYT);
        maybeAppend(sb, fastighet.getKommunKod(), kommun -> KOMMUN + kommun + RADBRYT);
        maybeAppend(sb, fastighet.getLanKod(), lan -> LAN + lan + RADBRYT);
        maybeAppend(sb, fastighet.getTaxeringsar(), ar -> TAXERINGSAR + ar + RADBRYT);
        maybeAppend(sb, fastighet.getTaxeringsvarde(), ar -> TAXERINGSVARDE + ar + RADBRYT);

        fastighet.getFastighetDel().forEach(del -> logFastighetDel(sb, del));
    }

    private static void logFastighetDel(StringBuilder sb, FastighetDelTYPE fastighetDel) {
        sb.append(FASTIGHETDEL + RADBRYT);
        maybeAppend(sb, fastighetDel.getTaxeringsidentitet(), id -> TAXERINGSIDENTITET + id + RADBRYT);
        maybeAppend(sb, fastighetDel.getFastighetBeteckning(), beteckning -> FASTIGHET_BETECKNING + beteckning + RADBRYT);
        maybeAppend(sb, fastighetDel.getAndelstalNamnare(), namnare -> ANDELSTAL_NAMNARE + namnare + RADBRYT);
        maybeAppend(sb, fastighetDel.getAndelstalTaljare(), taljare -> ANDELSTAL_TALJARE + taljare + RADBRYT);
    }

    /**
     * Lägger till loggningstext om svensk adress till StringBuilder
     */
    protected static void logSvenskadress(StringBuilder sb, SvenskAdressTYPE spa) {
        maybeAppend(sb, spa.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, spa.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, spa.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, spa.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, spa.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, spa.getPostNr(), postnr -> POSTNUMMER + postnr + RADBRYT);
        maybeAppend(sb, spa.getPostort(), postort -> POSTORT + postort + RADBRYT);
    }

    /**
     * Lägger till loggningstext om internationell adress till StringBuilder
     */
    protected static void logInternationellAdress(StringBuilder sb, InternationellAdressTYPE adr) {
        maybeAppend(sb, adr.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, adr.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, adr.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, adr.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, adr.getUtdelningsadress3(), ua3 -> UTDELNINGSADRESS_3 + ua3 + RADBRYT);
        maybeAppend(sb, adr.getLand(), land -> LAND + land + RADBRYT);
    }

    /**
     * Lägger till loggningstext om folkbokföring till StringBuilder
     */
    protected static void logFolkbokforing(StringBuilder sb, FolkbokforingTYPE fb) {
        sb.append(FOLKBOKFORING + RADBRYT);
        maybeAppend(sb, fb.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, fb.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, fb.getFolkbokfordLanKod(), kod -> LANKOD + kod + RADBRYT);
        maybeAppend(sb, fb.getFolkbokfordKommunKod(), kod -> KOMMUNKOD + kod + RADBRYT);
        maybeAppend(sb, fb.getHemvist(), kod -> HEMVIST + kod + RADBRYT);
        maybeAppend(sb, fb.getFolkbokforingsdatum(), datum -> "    Folkbokföringsdatum = " + datum + RADBRYT);
        maybeAppend(sb, fb.getDistriktKod(), kod -> DISTRIKTKOD + kod + RADBRYT);
    }

    /**
     * Lägger till loggningstext om relation till StringBuilder
     */
    protected static void logRelation(StringBuilder sb, RelationTYPE relation) {
        sb.append(RELATION + RADBRYT);
        maybeAppend(sb, relation.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, relation.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);
        maybeAppend(sb, relation.getRelationstyp(), typ -> RELATIONSTYP + typ + RADBRYT);

        maybeAppend(sb, relation.getIdNummer(), personid -> ID_NUMMER + personid + RADBRYT);
        maybeAppend(sb, relation.getFodelsetid(), fodelsetid -> FODELSETID + fodelsetid + RADBRYT);

        maybeAppend(sb, relation.getAvlidendatum(), datum -> AVLIDENDATUM + datum + RADBRYT);
        maybeAppend(sb, relation.getAvregistreringsorsakKod(), datum -> AVREGISTRERINGSORSAK_KOD + datum + RADBRYT);

        maybeAppend(sb, relation.getFornamn(), namn -> FORNAMN + namn + RADBRYT);
        maybeAppend(sb, relation.getMellannamn(), namn -> MELLANNAMN + namn + RADBRYT);
        maybeAppend(sb, relation.getEfternamn(), namn -> EFTERNAMN + namn + RADBRYT);
    }

    /**
     * Lägger till loggning om persondetalj till StringBuilder
     */
    protected static void logNamn(StringBuilder sb, NamnTYPE namn) {
        sb.append(NAMN + RADBRYT);
        maybeAppend(sb, namn.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, namn.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, namn.getFornamn(), n -> FORNAMN + n + RADBRYT);
        maybeAppend(sb, namn.getMellannamn(), n -> MELLANNAMN + n + RADBRYT);
        maybeAppend(sb, namn.getEfternamn(), n -> EFTERNAMN + n + RADBRYT);
        maybeAppend(sb, namn.getAviseringsnamn(), n -> AVISERINGSNAMN + n + RADBRYT);
        maybeAppend(sb, namn.getTilltalsnamn(), n -> TILLTALSNAMN + n + RADBRYT);
    }

    /**
     * Lägger till loggning om persondetalj till StringBuilder
     */
    protected static void logPersonDetalj(StringBuilder sb, PersondetaljerTYPE pdt) {
        sb.append(PERSONDETALJ + RADBRYT);
        maybeAppend(sb, pdt.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, pdt.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, pdt.getSekretessmarkering(), sekretess -> SEKRETESSMARKERING2 + sekretess.getValue().value() + RADBRYT);
        maybeAppend(sb, pdt.getSekretessmarkering().getSattAvSPAR(), sattAvSpar -> SEKRETESS_SATT_AV_SPAR2 + sattAvSpar.value() + RADBRYT);
        maybeAppend(sb, pdt.getSkyddadFolkbokforing(), skyddad -> SKYDDAD_FOLKBOKFORING2 + skyddad + RADBRYT);

        maybeAppend(sb, pdt.getAvregistreringsorsakKod(), kod -> AVREGISTRERINGSORSAK_KOD + kod + RADBRYT);
        maybeAppend(sb, pdt.getAvregistreringsdatum(), datum -> AVREGISTRERINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, pdt.getAvlidendatum(), datum -> AVLIDENDATUM + datum + RADBRYT);
        maybeAppend(sb, pdt.getAntraffadDodDatum(), datum -> ANTRAFFAD_DOD + datum + RADBRYT);

        maybeAppend(sb, pdt.getFodelsedatum(), d -> FODELSEDATUM + d + RADBRYT);
        maybeAppend(sb, pdt.getFodelselanKod(), kod -> FODELSELAN_KOD + kod + RADBRYT);
        maybeAppend(sb, pdt.getFodelseforsamling(), forsamling -> FODELSEFORSAMLING + forsamling + RADBRYT);

        maybeAppend(sb, pdt.getKon(), kon -> KON + kon + RADBRYT);
        maybeAppend(sb, pdt.getSvenskMedborgare(), medborgare -> SVENSK_MEDBORGARE + medborgare + RADBRYT);

        pdt.getHanvisning().forEach(hnv -> logHanvisning(sb, hnv));
    }

    private static void logHanvisning(StringBuilder sb, HanvisningTYPE hnv) {
        sb.append(HANVISNING + RADBRYT);
        final String indent = "    ";
        maybeAppend(sb, hnv.getIdNummer(), id -> HANVISNING_ID_NUMMER + id + RADBRYT);
        maybeAppend(sb, hnv.getTyp(), typ -> HANVISNING_TYP + typ + RADBRYT);
    }

    /**
     * Hjälpfunktion för att göra koden mer lättläst, om objektet objekt inte är null så mappas objektet om till en sträng som appendas på StringBuilder.
     *
     * @param sb       Stringbuilder där det eventuellt läggs till text
     * @param objekt   Om det här värden inte är null mappas det om
     * @param funktion Används för att mappa om objektet om det inte är null
     */
    private static <T> void maybeAppend(StringBuilder sb, T objekt, Function<? super T, ? extends String> funktion) {
        Optional.ofNullable(objekt)
                .map(funktion)
                .ifPresent(sb::append);
    }
}
