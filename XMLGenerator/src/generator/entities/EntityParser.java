package generator.entities;

import generator.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

public class EntityParser {
    public static HashMap<String, String> parseEntities(Element root) {
        Node model = root.getChildNodes().item(3);
        HashMap<String, String> entityIdsAndNames = new HashMap<>();

        if (model.getNodeType() == Node.ELEMENT_NODE) {
            Element modelElement = (Element) model;
            NodeList packagedElements = modelElement.getElementsByTagName(Constants.PACKAGED_ELEMENT_TAG);

            for (int i = 0; i < packagedElements.getLength(); i++) {
                Node node = packagedElements.item(i);
                Element element = (Element) node;
                String name = element.getAttribute(Constants.NAME_ATTR);
                String id = element.getAttribute(Constants.XMI_ID);
                entityIdsAndNames.put(id, name);
            }
        }

        return entityIdsAndNames;
    }
}
