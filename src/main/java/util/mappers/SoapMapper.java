package util.mappers;
import io.restassured.internal.mapping.ObjectMapperDeserializationContextImpl;
import io.restassured.internal.mapping.ObjectMapperSerializationContextImpl;
import io.restassured.mapper.ObjectMapper;
import io.restassured.mapper.ObjectMapperDeserializationContext;
import io.restassured.mapper.ObjectMapperSerializationContext;
import org.apache.commons.lang3.SerializationException;
import org.w3c.dom.Document;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SoapMapper implements ObjectMapper {

    public static SoapMapper xml() {
        return new SoapMapper();
    }

    @Override
    public Object deserialize(ObjectMapperDeserializationContext context) {

        Class<?> clazz = ((ObjectMapperDeserializationContextImpl) context).getType();

        try {
            SOAPMessage message = MessageFactory.newInstance()
                    .createMessage(null,
                            new ByteArrayInputStream(context.getDataToDeserialize().asByteArray()));

            JAXBContext jaxbContext = JAXBContext.newInstance(clazz);

            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            return jaxbUnmarshaller.unmarshal(message.getSOAPBody().extractContentAsDocument());


        } catch (IOException e) {
            e.printStackTrace();
        } catch (SOAPException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }

        throw new SerializationException("Unable to deserialize.");

    }

    @Override
    public Object serialize(ObjectMapperSerializationContext context) {

        Class<?> clazz = ((ObjectMapperSerializationContextImpl) context).getObject().getClass();

        try {
            Document document = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder().newDocument();

            Marshaller marshaller = JAXBContext.newInstance(clazz).createMarshaller();
            marshaller.marshal(context.getObjectToSerialize(), document);

            SOAPMessage soapMessage = MessageFactory.newInstance().createMessage();

            soapMessage.getSOAPBody().addDocument(document);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            soapMessage.writeTo(outputStream);

            return new String(outputStream.toByteArray());

        } catch (Exception e) {
            throw new SerializationException("Unable to serialize.");
        }
    }
}