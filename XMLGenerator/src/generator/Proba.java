package generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class Proba {

    public static void main(String[] args) {


        try {
            // Load the XML file
            File file = new File("./src/schema2.xml");
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(file);

            // Find the desired tag section by tag name
            String tagName = "uml:Model";
            Element model = (Element) document.getElementsByTagName(tagName).item(0);
            NodeList packages = model.getElementsByTagName("packagedElement");

            for (int j = 0; j < packages.getLength(); j++) {
                Element packagedElement = (Element) packages.item(j);
                System.out.println(packagedElement.getAttribute("xmi:type"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
