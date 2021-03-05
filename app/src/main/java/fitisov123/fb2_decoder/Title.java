package fitisov123.fb2_decoder;

import android.text.SpannableStringBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class Title implements TagElement {
	private List<TagElement> elements;
	
	public Title() {
		elements = new ArrayList<>();
	}
	
	public Title(NodeList nodeList, SpannableResource resource) {
		this();
		resource.processedLength += System.lineSeparator().length();
		for (int i = 0; i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.TEXT_NODE) {
    			elements.add(TextModificator.decodeP(new StringBuilder(node.getTextContent()), resource));
    		}
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element element = (Element) node;
    			if (element.getTagName().equals(FB2Decoder.P)) {
    				elements.add(TextModificator.decodeP(element.getChildNodes(), resource));
    			}
    			if (element.getTagName().equals(FB2Decoder.EMPTYLINE)) {
    				elements.add(TextModificator.decodeEmptyLine(resource));
    			}
    		}
    	}
		resource.processedLength += System.lineSeparator().length();
	}
	
	@Override
	public StringBuilder getText() {
		StringBuilder title = new StringBuilder();
		title.append(System.lineSeparator());
		for (int i = 0; i < elements.size(); i++) {
			title.append(elements.get(i).getText());
		}
		title.append(System.lineSeparator());
		return title;
	}
}
