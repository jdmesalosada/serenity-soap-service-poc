import model.GetCountryRequest;
import model.GetCountryResponse;
import net.serenitybdd.junit.runners.SerenityRunner;
import net.serenitybdd.screenplay.Actor;
import net.serenitybdd.screenplay.rest.abiities.CallAnApi;
import net.serenitybdd.screenplay.rest.interactions.Post;
import net.serenitybdd.screenplay.rest.questions.LastResponse;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SerenityRunner.class)
public class MyFirsTest {

    private static final String URL_BASE = "http://localhost:8080";

    @Test
    public void addTwoNumbers() throws ParserConfigurationException, JAXBException, SOAPException, IOException {
        String resource = "/ws";

        HashMap<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "text/xml;charset=UTF-8");
        headers.put("SOAPAction", "");

        Actor julian = Actor.named("Julian");

        julian.can(CallAnApi.at(URL_BASE));

        GetCountryRequest countryRequest = new GetCountryRequest();
        countryRequest.setName("Spain");

        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        Marshaller marshaller = JAXBContext.newInstance(GetCountryRequest.class).createMarshaller();
        marshaller.marshal(countryRequest, document);
        SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();
        soapMessage.getSOAPBody().addDocument(document);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        soapMessage.writeTo(outputStream);
        String output = new String(outputStream.toByteArray());


        julian.attemptsTo(
                Post.to(resource)
                        .with(
                                req -> req.headers(headers)
                                        .body(output)
                        )
        );

        SOAPMessage message = MessageFactory.newInstance()
                .createMessage(null,
                        new ByteArrayInputStream(LastResponse.received().answeredBy(julian).asString().getBytes()));


        JAXBContext jaxbContext = JAXBContext.newInstance(GetCountryResponse.class);
        Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        GetCountryResponse response = (GetCountryResponse) jaxbUnmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());

        assertThat("Currency", response.getCountry().getCurrency().name(), equalTo("EUR"));
    }
}
