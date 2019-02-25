package com.test.cbstest;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Scanner;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.ClassUtils;

public class JaxbConverter<T> extends AbstractHttpMessageConverter<T> {

	@Inject
	private HttpServletRequest request;

	@Inject
	private SignatureGenUtils genUtils;

	private static final Logger LOGGER = LoggerFactory.getLogger(JaxbConverter.class);

	public JaxbConverter(SignatureGenUtils genUtils) {
		super(new MediaType("application", "xml"),
				new MediaType(MediaType.APPLICATION_XML, Charset.forName("UTF-8")));
		this.genUtils = genUtils;
	}

	@Override
	protected boolean supports(Class<?> clazz) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage)
			throws IOException, HttpMessageNotReadableException {
		// TODO Auto-generated method stub

		try {
			String reqBody = getRequest(inputMessage);
			LOGGER.info("ReqBody [{}]", reqBody);

			boolean res = genUtils.isDigitaiSignatureValid(reqBody);
			LOGGER.info("Is sign verify [{}]", res);
			if (!res) {
				throw new DataException("04", "XML Sign Verify Failed");

			}

			StringReader reader = new StringReader(reqBody);
			JAXBContext jaxbContext = JAXBContext.newInstance(org.springframework.util.ClassUtils.getUserClass(clazz));
			Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
			Source source = new StreamSource(reader);
			T t = (T) jaxbUnmarshaller.unmarshal(source);
			return t;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IOException();
			// return null;
		}

	}

	@Override
	protected void writeInternal(T t, HttpOutputMessage outputMessage)
			throws IOException, HttpMessageNotWritableException {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ClassUtils.getUserClass(t));
			if (AnnotationUtils.findAnnotation(t.getClass(), XmlRootElement.class) != null) {
				Marshaller jaxbMarsheller = jaxbContext.createMarshaller();
				StringWriter writer = new StringWriter();
				jaxbMarsheller.marshal(t, writer);

				String plainXml = writer.getBuffer().toString();
				outputMessage.getBody().write(plainXml.getBytes());

			}
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
		}
	}

	private String getRequest(HttpInputMessage inputMessage) throws IOException {
		StringBuilder stringBuilder = new StringBuilder();
		Scanner scanner = new Scanner(inputMessage.getBody(), "UTF-8");

		while (scanner.hasNextLine()) {
			stringBuilder.append(scanner.nextLine());

		}
		scanner.close();
		String body = stringBuilder.toString();
		return body;

	}

}
