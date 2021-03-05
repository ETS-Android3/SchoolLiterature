package fitisov123.fb2_decoder;

import android.text.SpannableStringBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

public class Poem implements TagElement{
	ArrayList<TagElement> elements = new ArrayList<>();
	
	public Poem(NodeList poem, SpannableResource resource) {
		for (int i = 0; i < poem.getLength(); i++) {
			Node node = poem.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(FB2Decoder.TITLE)) {
					elements.add(new Title(element.getChildNodes(), resource));
				}
				if (element.getTagName().equals(FB2Decoder.STANZA)) {
					elements.add(new Stanza(element.getChildNodes(), resource));
				}
				if (element.getTagName().equals(FB2Decoder.TEXTAUTHOR)) {
					elements.add(TextModificator.decodeTextAuthor(element.getChildNodes(), resource));
				}
				if (element.getTagName().equals(FB2Decoder.DATE)) {
					elements.add(TextModificator.decodeDate(element.getChildNodes(), resource));
				}
			}
		}
	}
	
	public class Stanza implements TagElement{
		ArrayList<TagElement> elements = new ArrayList<>();
		
		public Stanza(NodeList stanza, SpannableResource resource) {
        	for(int i = 0; i < stanza.getLength(); i++) {
        		Node node = stanza.item(i);
        		if(node.getNodeType() == Node.ELEMENT_NODE) {
        			Element element = (Element) node;
        			if(element.getTagName().equals(FB2Decoder.TITLE)) {
        				elements.add(new Title(element.getChildNodes(), resource));
        			}
        			if(element.getTagName().equals(FB2Decoder.SUBTITLE)) {
        				elements.add(TextModificator.decodeSubtitle(element.getChildNodes(), resource));
        			}
        			if(element.getTagName().equals(FB2Decoder.V)) {
        				elements.add(TextModificator.decodeV(element.getChildNodes(), resource));
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

	@Override
	public StringBuilder getText() {
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < elements.size(); i++)
			text.append(elements.get(i).getText());
		return text;
	}
}
