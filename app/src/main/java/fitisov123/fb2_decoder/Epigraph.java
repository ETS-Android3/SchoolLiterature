package fitisov123.fb2_decoder;

import android.text.SpannableStringBuilder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;

import fitisov123.schoolliterature.Algorithm;

public class Epigraph implements TagElement {
	private ArrayList<TagElement> elements = new ArrayList<>();
	private Integer innerTextStart;
	private SpannableResource resource;

	public Epigraph(NodeList nodeList, SpannableResource resource) {
		this.resource = resource;
		innerTextStart = resource.processedLength;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if(element.getTagName().equals(FB2Decoder.P)) {
					elements.add(TextModificator.decodeP(element.getChildNodes(), resource));
				}
				if(element.getTagName().equals(FB2Decoder.POEM)) {
					elements.add(new Poem(element.getChildNodes(), resource));
				}
				if(element.getTagName().equals(FB2Decoder.CITE)) {
					elements.add(new Cite(element.getChildNodes(), resource));
				}
				if(element.getTagName().equals(FB2Decoder.EMPTYLINE)) {
					elements.add(TextModificator.decodeEmptyLine(resource));
				}
				if(element.getTagName().equals(FB2Decoder.TEXTAUTHOR)) {
					elements.add(TextModificator.decodeTextAuthor(element.getChildNodes(), resource));
				}
			}
		}
	}

	@Override
	public StringBuilder getText() {
		StringBuilder text = new StringBuilder();
		for(int i = 0; i < elements.size(); i++) {
			text.append(elements.get(i).getText());
		}
		int endIndex = innerTextStart + text.length() - 1;
		if (endIndex < innerTextStart) {
			Algorithm.logMessage("Cite was bad");
		}
		resource.addSpan(new Span(SpanType.STYLE, innerTextStart, endIndex));
		return text;
	}
}

