package fitisov123.fb2_decoder;

import android.text.SpannableStringBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class TextModificator{
	/*
	 * Every element processed here is already wrapped with appropriate 
	 * sub symbols or html tags 
	 */
	
	private static class CustomElement implements TagElement {
		StringBuilder text = new StringBuilder();
		
		public CustomElement(StringBuilder resultText) {
			text = resultText;
		}
		
		@Override
		public StringBuilder getText() {
			return text;
		}
		
	}

	private static void addLineBreak(StringBuilder result, SpannableResource resource) {
		result.append(new SpannableStringBuilder(System.lineSeparator()));
		resource.processedLength += System.lineSeparator().length();
	}

	private static StringBuilder decodeSpannedElement(Element element, SpannableResource resource) {
		if (element.getTagName().equals(FB2Decoder.EMPHASIS)) {
			return decodeStyleModification(StyleModification.EMPHASIS, element.getChildNodes(), resource).getText();
		}
    	if (element.getTagName().equals(FB2Decoder.STRONG)) {
    		return decodeStyleModification(StyleModification.STRONG, element.getChildNodes(), resource).getText();
		}
		if (element.getTagName().equals(FB2Decoder.STYLE)) {
			return decodeStyleModification(StyleModification.STYLE, element.getChildNodes(), resource).getText();
		}
		if (element.getTagName().equals(FB2Decoder.A)) {
			if (element.hasAttribute(FB2Decoder.TYPE) && element.getAttribute(FB2Decoder.TYPE).equals(FB2Decoder.NOTE)) {
				String noteId = element.getAttribute(FB2Decoder.HREF).substring(1);
				return decodeA(element.getChildNodes(), noteId, resource).getText();
			}
		}
		return new StringBuilder();
    }

	private enum StyleModification{
		EMPHASIS,
		STRONG,
		STYLE,
	}

	public static TagElement decodeStyleModification(StyleModification styleModification, NodeList nodeList, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				switch (styleModification) {
					case EMPHASIS : resource.addSpan(new Span(SpanType.EMPHASIS, startIndex, endIndex));
						break;
					case STRONG   : resource.addSpan(new Span(SpanType.STRONG, startIndex, endIndex));
						break;
					case STYLE    : resource.addSpan(new Span(SpanType.STYLE, startIndex, endIndex));
						break;
				}
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
			}
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				result.append(decodeSpannedElement(element, resource));
			}
		}
		return new CustomElement(result);
	}

    public static TagElement decodeA(NodeList nodeList, String noteId, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				resource.addSpan(new Span(SpanType.REF, startIndex, endIndex, noteId));
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
			}
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				result.append(decodeSpannedElement(element, resource));
			}
		}
		return new CustomElement(result);
	}
    
    public static TagElement decodeDate(NodeList nodeList, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
    		}
    	}
		addLineBreak(result, resource);
    	return new CustomElement(result);
	}
    
    public static TagElement decodeEmptyLine(SpannableResource resource) {
		StringBuilder result = new StringBuilder();
		addLineBreak(result, resource);
		addLineBreak(result, resource);
    	return new CustomElement(result);
    }
    
    public static TagElement decodeSubtitle(NodeList nodeList, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
    		}
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element element = (Element) node;
				result.append(decodeSpannedElement(element, resource));
    		}
    	}
    	addLineBreak(result, resource);
    	return new CustomElement(result);
    }
   
    public static TagElement decodeV(NodeList nodeList, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
    		}
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element element = (Element) node;
				result.append(decodeSpannedElement(element, resource));
    		}
    	}
		addLineBreak(result, resource);
    	return new CustomElement(result);
	}
    
    public static TagElement decodeTextAuthor(NodeList nodeList, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
    	for (int i = 0; i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
    		}
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element element = (Element) node;
				result.append(decodeSpannedElement(element, resource));
    		}
    	}
		addLineBreak(result, resource);
    	return new CustomElement(result);
    }
    
    public static TagElement decodeP(NodeList nodeList, SpannableResource resource) {
		StringBuilder result = new StringBuilder();
    	for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.TEXT_NODE) {
				String textContent = node.getTextContent();
				int startIndex = resource.processedLength;
				int endIndex = startIndex + textContent.length() - 1;
				resource.processedLength = endIndex + 1;
				result.append(new SpannableStringBuilder(textContent));
			}
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				result.append(decodeSpannedElement(element, resource));
			}
		}
		addLineBreak(result, resource);
    	return new CustomElement(result);
    }
    
    public static TagElement decodeP(StringBuilder text, SpannableResource resource) {
		int startIndex = resource.processedLength;
		int endIndex = startIndex + text.length() - 1;
		resource.processedLength = endIndex + 1;
		StringBuilder result = text;
		addLineBreak(result, resource);
    	return new CustomElement(result);
    }
}
