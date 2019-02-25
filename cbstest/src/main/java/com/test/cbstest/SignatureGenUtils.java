package com.test.cbstest;

import java.io.StringReader;
import java.io.StringWriter;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Collections;

import javax.inject.Inject;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.KeyValue;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class SignatureGenUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(SignatureGenUtils.class);

	@Inject
	private PrivateKey privateKey;

	@Inject
	private PublicKey publicKey;

	public String generateSignature(String xml) throws Exception {
		Document doc = string2Doc(xml);
		XMLSignatureFactory xmlSignFactory = XMLSignatureFactory.getInstance("DOM");
		DOMSignContext domSignCtx = new DOMSignContext(privateKey, doc.getDocumentElement());
		Reference ref = null;
		SignedInfo signedInfo = null;

		ref = xmlSignFactory
				.newReference("", xmlSignFactory.newDigestMethod(DigestMethod.SHA256, null),
						Collections.singletonList(
								xmlSignFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
						null, null);
		signedInfo = xmlSignFactory.newSignedInfo(
				xmlSignFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
						(C14NMethodParameterSpec) null),
				xmlSignFactory.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#rsa-sha256",
						(SignatureMethodParameterSpec) null),
				Collections.singletonList(ref));

		KeyInfo keyInfo = getKeyInfo(xmlSignFactory);
		XMLSignature xmlSignature = xmlSignFactory.newXMLSignature(signedInfo, keyInfo);
		xmlSignature.sign(domSignCtx);
		return doc2String(doc);

	}

	public boolean isDigitaiSignatureValid(String xml) throws Exception {

		boolean validFlag = false;
		Document doc = string2Doc(xml);
		DOMValidateContext valContext;
		try {
			NodeList n1 = doc.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
			valContext = new DOMValidateContext(publicKey, n1.item(0));
		} catch (Exception e) {
			return true;
		}
		XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM");
		XMLSignature signature = fac.unmarshalXMLSignature(valContext);
		validFlag = signature.validate(valContext);
		return validFlag;
	}

	private String doc2String(Document doc) throws Exception {

		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer trans = null;
		trans = factory.newTransformer();
		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		trans.transform(new DOMSource(doc), result);
		String output = writer.getBuffer().toString();
		return output;

	}

	private KeyInfo getKeyInfo(XMLSignatureFactory xmlFactory) throws Exception {

		KeyInfo keyInfo = null;
		KeyValue keyValue = null;
		KeyInfoFactory keyInfoFactory = xmlFactory.getKeyInfoFactory();
		keyValue = keyInfoFactory.newKeyValue(publicKey);
		keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(keyValue));
		return keyInfo;
	}

	public void setPrivateKey(PrivateKey privateKey) {
		this.privateKey = privateKey;
	}

	public void setPublicKey(PublicKey publicKey) {
		this.publicKey = publicKey;
	}

	public Document string2Doc(String xml) throws Exception {

		Document doc = null;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setNamespaceAware(true);
		StringReader reader = new StringReader(xml);
		InputSource source = new InputSource(reader);

		doc = factory.newDocumentBuilder().parse(source);
		return doc;
	}

}
