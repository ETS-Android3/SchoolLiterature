package fitisov123.fb2_decoder;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fitisov123.schoolliterature.DataStorage;

public class NotesBody {

	public NotesBody(NodeList nodeList, Notes notes) {
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element element = (Element) node;
				if (element.getTagName().equals(FB2Decoder.SECTION)) {
					if (element.hasAttribute(FB2Decoder.ID)) {
						Note note = new Note();

						String id = element.getAttribute(FB2Decoder.ID);
						note.setId(id);

						TagElement tagElement = new Section(element.getChildNodes(), note);
						StringBuilder noteText = tagElement.getText();
						note.setText(noteText);

						DataStorage.decoder.noteList.addNote(id, note);
					}
				}
			}
		}
	}
}
