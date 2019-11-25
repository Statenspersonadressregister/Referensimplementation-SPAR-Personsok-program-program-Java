package se.statenspersonadressregister.referensimplementation.loggning;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import se.statenspersonadressregister.schema.komponent.person.adress.folkbokforingsadress_1.FolkbokforingsadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.sarskildpostadress_1.SarskildPostadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.adress.utlandsadress_1.UtlandsadressTYPE;
import se.statenspersonadressregister.schema.komponent.person.aviseringspost_1.AviseringsPostTYPE;
import se.statenspersonadressregister.schema.komponent.person.fastighetstaxering_1.FastighetDelTYPE;
import se.statenspersonadressregister.schema.komponent.person.fastighetstaxering_1.FastighetTYPE;
import se.statenspersonadressregister.schema.komponent.person.persondetaljer_1.PersondetaljerTYPE;
import se.statenspersonadressregister.schema.komponent.person.relation_1.RelationTYPE;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsvar_1.SPARPersonsokningSvar;
import se.statenspersonadressregister.schema.komponent.sok.personsokningsvar_1.UndantagTYPE;

import java.util.Optional;
import java.util.function.Function;

import static se.statenspersonadressregister.referensimplementation.loggning.PersonsokExempelLogTexter.*;

/**
 * Klass för att logga ett svar från SPAR personsökning pgm pgm 2019.1
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
        sb.append(MELLANSLAG + svar.getPersonsokningSvarsPost().size() + SOKTRAFFAR +
                      svar.getUndantag().size() + ANTAL_UNDANTAG + RADBRYT);

        maybeAppend(sb, svar.getOverstigerMaxAntalSvarsposter(), overstiger ->
            OVERSTIGER_MAX_ANTAL_TRAFFAR + overstiger.getAntalPoster() +
                TRAFFAR_AV_MAX + overstiger.getMaxAntalSvarsPoster() + RADBRYT);

        svar.getUndantag().forEach(undantag -> logUndantag(sb, undantag));
        svar.getPersonsokningSvarsPost().forEach(aviseringsPost -> logAviseringsPost(sb, aviseringsPost));

        log.debug(sb.toString());
    }

    /**
     * Lägger till loggning om undantag till StringBuilder
     */
    protected static void logUndantag(StringBuilder sb, UndantagTYPE undantag) {
        sb.append(UNDANTAG + RADBRYT);
        maybeAppend(sb, undantag.getKod(), kod -> KOD + kod + RADBRYT);
        maybeAppend(sb, undantag.getBeskrivning(), beskrivning -> BESKRIVNING + beskrivning + RADBRYT);
    }

    /**
     * Lägger till loggningstext om aviseringsport till StringBuilder
     */
    protected static void logAviseringsPost(StringBuilder sb, AviseringsPostTYPE aviseringsPost) {
        sb.append(AVISERINGSPOST + RADBRYT);
        maybeAppend(sb, aviseringsPost.getPersonId(), id -> PERSON_ID + id.getFysiskPersonId() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSenasteAndringSPAR(), senast -> SENASTE_ANDRING_SPAR + senast + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessmarkering(), sek -> SEKRETESSMARKERING1 + sek.getValue().value() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessmarkering().getSattAvSPAR(), sattAvSpar -> SEKRETESS_SATT_AV_SPAR1 + sattAvSpar.value() + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSekretessAndringsdatum(), datum -> SEKRETESS_ANDRINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSkyddadFolkbokforing(), sfb -> SKYDDAD_FOLKBOKFORING1 + sfb + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSkyddadFolkbokforingAndringsdatum(), datum -> SKYDDAD_FOLKBOKFORING_ANDRINGSDATUM + datum + RADBRYT);
        maybeAppend(sb, aviseringsPost.getInkomstAr(), ar -> INKOMST_AR + ar + RADBRYT);
        maybeAppend(sb, aviseringsPost.getSummeradInkomst(), inkomst -> SUMMERAD_INKOMST + inkomst + RADBRYT);
        aviseringsPost.getFastighet().forEach(fastighet -> logFastighet(sb, fastighet));
        aviseringsPost.getPersondetaljer().forEach(persondetalj -> logPersonDetalj(sb, persondetalj));
        aviseringsPost.getRelation().forEach(relation -> logRelation(sb, relation));
        aviseringsPost.getFolkbokforingsadress().forEach(fba -> logFolkbokforingsAdress(sb, fba));
        aviseringsPost.getSarskildPostadress().forEach(spa -> logSarskildPostadress(sb, spa));
        aviseringsPost.getUtlandsadress().forEach(ua -> logUtlandsadress(sb, ua));
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
     * Lägger till loggningstext om utlandsadresser till StringBuilder
     */
    protected static void logUtlandsadress(StringBuilder sb, UtlandsadressTYPE ua) {
        sb.append(UTLANDSADRESS + RADBRYT);
        maybeAppend(sb, ua.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, ua.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, ua.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, ua.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, ua.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, ua.getUtdelningsadress3(), ua3 -> UTDELNINGSADRESS_3 + ua3 + RADBRYT);
        maybeAppend(sb, ua.getLand(), land -> LAND + land + RADBRYT);
    }

    /**
     * Lägger till loggningstext om särskild postadress till StringBuilder
     */
    protected static void logSarskildPostadress(StringBuilder sb, SarskildPostadressTYPE spa) {
        sb.append(SARSKILD_POSTADRESS + RADBRYT);
        maybeAppend(sb, spa.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, spa.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, spa.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, spa.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, spa.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, spa.getPostNr(), postnr -> POSTNUMMER + postnr + RADBRYT);
        maybeAppend(sb, spa.getPostort(), postort -> POSTORT + postort + RADBRYT);

    }

    /**
     * Lägger till loggningstext om folkbokföringsadress till StringBuilder
     */
    protected static void logFolkbokforingsAdress(StringBuilder sb, FolkbokforingsadressTYPE fba) {
        sb.append(FOLKBOKFORINGSADRESS + RADBRYT);
        maybeAppend(sb, fba.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, fba.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);

        maybeAppend(sb, fba.getCareOf(), co -> CARE_OF + co + RADBRYT);
        maybeAppend(sb, fba.getUtdelningsadress1(), ua1 -> UTDELNINGSADRESS_1 + ua1 + RADBRYT);
        maybeAppend(sb, fba.getUtdelningsadress2(), ua2 -> UTDELNINGSADRESS_2 + ua2 + RADBRYT);
        maybeAppend(sb, fba.getPostNr(), postnr -> POSTNUMMER + postnr + RADBRYT);
        maybeAppend(sb, fba.getPostort(), postort -> POSTORT + postort + RADBRYT);

        maybeAppend(sb, fba.getFolkbokforingsdatum(), datum -> "    Folkbokföringsdatum = " + datum + RADBRYT);
        maybeAppend(sb, fba.getFolkbokfordForsamlingKod(), kod -> FOLKBOKFORDSFORSAMLINGKOD + kod + RADBRYT);
        maybeAppend(sb, fba.getDistriktKod(), kod -> DISTRIKTKOD + kod + RADBRYT);
        maybeAppend(sb, fba.getFolkbokfordKommunKod(), kod -> KOMMUNKOD + kod + RADBRYT);
        maybeAppend(sb, fba.getFolkbokfordLanKod(), kod -> LANKOD + kod + RADBRYT);
    }

    /**
     * Lägger till loggningstext om relation till StringBuilder
     */
    protected static void logRelation(StringBuilder sb, RelationTYPE relation) {
        sb.append(RELATION + RADBRYT);
        maybeAppend(sb, relation.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, relation.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);
        maybeAppend(sb, relation.getRelationstyp(), typ -> RELATIONSTYP + typ + RADBRYT);

        maybeAppend(sb, relation.getPersonId(), personid -> PERSON_ID + personid + RADBRYT);
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
    protected static void logPersonDetalj(StringBuilder sb, PersondetaljerTYPE persondetalj) {
        sb.append(PERSONDETALJ + RADBRYT);
        maybeAppend(sb, persondetalj.getDatumFrom(), datum -> DATUM_FROM + datum + RADBRYT);
        maybeAppend(sb, persondetalj.getDatumTill(), datum -> DATUM_TILL + datum + RADBRYT);
        maybeAppend(sb, persondetalj.getAvlidendatum(), datum -> AVLIDENDATUM + datum + RADBRYT);
        maybeAppend(sb, persondetalj.getAvregistreringsorsakKod(), kod -> AVREGISTRERINGSORSAK_KOD + kod + RADBRYT);
        maybeAppend(sb, persondetalj.getAntraffadDodDatum(), datum -> ANTRAFFAD_DOD + datum + RADBRYT);

        maybeAppend(sb, persondetalj.getFornamn(), namn -> FORNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getMellannamn(), namn -> MELLANNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getEfternamn(), namn -> EFTERNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getAviseringsnamn(), namn -> AVISERINGSNAMN + namn + RADBRYT);
        maybeAppend(sb, persondetalj.getTilltalsnamn(), tilltal -> TILLTALSNAMN + tilltal + RADBRYT);

        maybeAppend(sb, persondetalj.getFodelselanKod(), kod -> FODELSELAN_KOD + kod + RADBRYT);
        maybeAppend(sb, persondetalj.getFodelseforsamling(), forsamling -> FODELSEFORSAMLING + forsamling + RADBRYT);
        maybeAppend(sb, persondetalj.getFodelsetid(), tid -> FODELSETID + tid + RADBRYT);

        maybeAppend(sb, persondetalj.getHanvisningsPersonNrByttFran(), hanvisning -> HANVISNINGS_PERSON_NR_BYTT_FRAN + hanvisning + RADBRYT);
        maybeAppend(sb, persondetalj.getHanvisningsPersonNrByttTill(), hanvisning -> HANVISNINGS_PERSON_NR_BYTT_TILL + hanvisning + RADBRYT);
        maybeAppend(sb, persondetalj.getSekretessmarkering(), sekretess -> SEKRETESSMARKERING2 + sekretess.getValue().value() + RADBRYT);
        maybeAppend(sb, persondetalj.getSekretessmarkering().getSattAvSPAR(), sattAvSpar -> SEKRETESS_SATT_AV_SPAR2 + sattAvSpar.value() + RADBRYT);
        maybeAppend(sb, persondetalj.getSkyddadFolkbokforing(), skyddad -> SKYDDAD_FOLKBOKFORING2 + skyddad + RADBRYT);
        maybeAppend(sb, persondetalj.getKon(), kon -> KON + kon + RADBRYT);
        maybeAppend(sb, persondetalj.getSvenskMedborgare(), medborgare -> SVENSK_MEDBORGARE + medborgare + RADBRYT);
    }

    /**
     * Hjälpfunktion för att göra koden mer lättläst, om objektet objekt inte är null så mappas objektet om till en sträng som appendas på StringBuilder.
     *
     * @param sb Stringbuilder där det eventuellt läggs till text
     * @param objekt Om det här värden inte är null mappas det om
     * @param funktion Används för att mappa om objektet om det inte är null
     */
    private static <T> void maybeAppend(StringBuilder sb, T objekt, Function<? super T, ? extends String> funktion) {
        Optional.ofNullable(objekt)
                .map(funktion)
                .ifPresent(sb::append);
    }
}
