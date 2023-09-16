package generator.associations;

import generator.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class AssociationGenerator {
    public static List<Association> parseAssociations(Element root) {
        List<Association> associations = new ArrayList<>();

        Node model = root.getChildNodes().item(3);
        if (model.getNodeType() == Node.ELEMENT_NODE) {
            Element modelElement = (Element) model;
            NodeList packagedElements = modelElement.getElementsByTagName(Constants.PACKAGED_ELEMENT_TAG);

            // Loop over all packaged elements and extract Associations
            for (int i = 0; i < packagedElements.getLength(); i++) {
                Node node = packagedElements.item(i);
                Element element = (Element) node;
                String xmiType = element.getAttribute(Constants.XMI_TYPE_ATTR);

                // If it is association packaged element
                if (xmiType.equals(Constants.ASSOCIATION)) {
                    // Extract id
                    String id = element.getAttribute(Constants.XMI_ID);
                    Association newAssociation = new Association(id);

                    NodeList members = element.getElementsByTagName(Constants.MEMBER_END_TAG);
                    // Extract memberOne
                    Node memberOne = members.item(0);
                    Element memberOneElement = (Element) memberOne;
                    String memberOneId = memberOneElement.getAttribute(Constants.XMI_IDREF);

                    // Extract memberTwo
                    Node memberTwo = members.item(1);
                    Element memberTwoElement = (Element) memberTwo;
                    String memberTwoId = memberTwoElement.getAttribute(Constants.XMI_IDREF);

                    newAssociation.AssignMemberOneId(memberOneId);
                    newAssociation.AssignMemberTwoId(memberTwoId);
                    associations.add(newAssociation);
                }
            }
        }

        return associations;
    }
}
