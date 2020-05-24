import model.GetCountryRequest;
import model.GetCountryResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.rest.SerenityRest;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abiities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.SOAPException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import tasks.AskForCountry;
import util.SoapMapper;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SerenityRunner.class)
public class MyFirsTest {

    private static final String URL_BASE = "http://localhost:8080";
    Actor julian;

    @Before
    public void setup() {
        SerenityRest.objectMapper(SoapMapper.xml());
        julian = Actor.named("Julian");
        julian.can(CallAnApi.at(URL_BASE));
    }

    @Test
    public void addTwoNumbers() {

        GetCountryRequest countryRequest = new GetCountryRequest();
        countryRequest.setName("Spain");

        julian.attemptsTo(
                AskForCountry.information(countryRequest)
        );

        GetCountryResponse response =
                LastResponse.received().answeredBy(julian)
                        .as(GetCountryResponse.class);

        assertThat("Currency", response.getCountry().getCurrency().name(),
                equalTo("EUR"));
    }
}
