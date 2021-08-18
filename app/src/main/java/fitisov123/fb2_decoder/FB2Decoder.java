package fitisov123.fb2_decoder;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FB2Decoder {

    private DocumentBuilder builder;
    private Document document;
    private File textFile;

    public Spans textSpans;
    public Notes noteList;
    
    public static final String  CITE       = "cite",
    					 		SECTION    = "section",
    					 		BODY       = "body",
    					 		TITLE      = "title",
    					 		SUBTITLE   = "subtitle",
    					 		POEM       = "poem",
    					 		EMPHASIS   = "emphasis",
    					 		STRONG     = "strong",
    					 		STYLE      = "style",
    					 		P          = "p",
    					 		EPIGRAPH   = "epigraph",
    					 		EMPTYLINE  = "empty-line",
    					 		NAME       = "name",
    					 		NOTES      = "notes",
    					 		COMMENTS   = "comments",
    					 		STANZA     = "stanza",
    					 		V          = "v",
    					 		TEXTAUTHOR = "text-author",
    					 		DATE       = "date",
                                A          = "a",
                                TYPE       = "type",
                                HREF       = "l:href",
                                NOTE       = "note",
                                ID         = "id";
    
    public FB2Decoder(File curTextFile) {
        textFile = curTextFile;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        textSpans = new Spans();
        noteList = new Notes();
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        try {
            document = builder.parse(textFile);
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean documentCreated() {
        return document != null;
    }

    public void decodeFile() {
        NodeList bodyList = document.getElementsByTagName(BODY);
        decodeBodies(bodyList);
    }

    private TextBody textBody;
    private NotesBody notesBody;
    
    public void decodeBodies(NodeList bodiesRoot) {
        for (int i = 0; i < bodiesRoot.getLength(); i++) {
            Node node = bodiesRoot.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                if (!element.hasAttribute(NAME)) { //bodyText
                    textBody = new TextBody(element.getChildNodes(), textSpans);
                } else {
                    if (element.getAttribute(NAME).equals(NOTES)) { //bodyNotes
                        notesBody = new NotesBody(element.getChildNodes(), noteList);
                    }
                    if (element.getAttribute(NAME).equals(COMMENTS)) {
                        //TODO: process comments if needed
                    }
                }
            }
        }
    }

    public StringBuilder getText() {
    	return textBody.getText();
    }
}
