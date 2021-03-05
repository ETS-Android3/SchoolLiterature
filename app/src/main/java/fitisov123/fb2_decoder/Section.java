package fitisov123.fb2_decoder;

import android.text.SpannableStringBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Section implements TagElement{
	private ArrayList<TagElement> elements = new ArrayList<>();
	
	public Section(NodeList nodeList, SpannableResource resource) {
		for (int i = 0; i < nodeList.getLength(); i++) {
    		Node node = nodeList.item(i);
    		if (node.getNodeType() == Node.ELEMENT_NODE) {
    			Element element = (Element) node;
    			if (element.getTagName().equals(FB2Decoder.SECTION)) {
    				elements.add(new Section(element.getChildNodes(), resource));
    			}	
    			if (element.getTagName().equals(FB2Decoder.TITLE)) {
    				elements.add(new Title(element.getChildNodes(), resource));
    			}
    			if (element.getTagName().equals(FB2Decoder.P)) {
    				elements.add(TextModificator.decodeP(element.getChildNodes(), resource));
    			}
    			if (element.getTagName().equals(FB2Decoder.POEM)) {
    				elements.add(new Poem(element.getChildNodes(), resource));
    			}
    			if (element.getTagName().equals(FB2Decoder.CITE)) {
    				elements.add(new Cite(element.getChildNodes(), resource));
    			}
				if (element.getTagName().equals(FB2Decoder.EPIGRAPH)) {
					elements.add(new Epigraph(element.getChildNodes(), resource));
				}
    			if (element.getTagName().equals(FB2Decoder.EMPTYLINE)) {
    				elements.add(TextModificator.decodeEmptyLine(resource));
    			}
    		}
    	}
	}

	@Override
	public StringBuilder getText() {
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < elements.size(); i++)
			text.append(elements.get(i).getText());
		return text;
	}
}
