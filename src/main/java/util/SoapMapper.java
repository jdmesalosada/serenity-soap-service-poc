package util;

import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import model.GetCountryRequest;
import model.GetCountryResponse;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SoapMapper implements ObjectMapper {

    public static SoapMapper xml(){
        return new SoapMapper();
    }

    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {

        GetCountryResponse countryResponse = new GetCountryResponse();

        try {
            SOAPMessage message = MessageFactory.newInstance()
                    .createMessage(null,
                            new ByteArrayInputStream(context.getDataToDeserialize().asByteArray()));

            JAXBContext jaxbContext = JAXBContext.newInstance(countryResponse.getClass());

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            countryResponse = (GetCountryResponse) jaxbUnmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        return countryResponse;
    }

    @Override
    public Object serialize(ObjectMapperSerializationContext context) {
        Document document = null;
        try {
            document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Marshaller marshaller = null;
        try {
            marshaller = JAXBContext.newInstance(GetCountryRequest.class).createMarshaller();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        try {
            marshaller.marshal(context.getObjectToSerialize(), document);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        SOAPMessage soapMessage = null;
        try {
            soapMessage = MessageFactory.newInstance().createMessage();
        } catch (SOAPException e) {
            e.printStackTrace();
        }
        try {
            soapMessage.getSOAPBody().addDocument(document);
        } catch (SOAPException e) {
            e.printStackTrace();
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            soapMessage.writeTo(outputStream);
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(outputStream.toByteArray());
    }
}
