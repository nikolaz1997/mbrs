package generator.associations;

import generator.Constants;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;
import java.util.stream.Stream;

public class AssociationParser {
    public static List<Association> parseAssociations(Element root) {
        List<Association> associations = new ArrayList<>();

        Node model = root.getChildNodes().item(3);
        if (model.getNodeType() == Node.ELEMENT_NODE) {
            Element modelElement = (Element) model;
            NodeList packagedElements = modelElement.getElementsByTagName(Constants.PACKAGED_ELEMENT_TAG);

            // Parse all entities in XML File with its ids
            var entityIdsAndNames = parseEntitiesWithIds(packagedElements);

            // Loop over all packaged elements and extract Associations
            extractAssociationsWithoutFullMembers(associations, packagedElements);

            // Loop again over all packaged elements and its owned attributes to connect members to associations
            enrichAssociationsWithFullMembers(associations, packagedElements, entityIdsAndNames);
        }

        return associations;
    }

    private static void enrichAssociationsWithFullMembers(List<Association> associations, NodeList packagedElements, HashMap<String, String> entityIdsAndNames) {
        for (int i = 0; i < packagedElements.getLength(); i++) {
            Node node = packagedElements.item(i);
            Element element = (Element) node;
            String xmiType = element.getAttribute(Constants.XMI_TYPE_ATTR);

            // If it is class packaged element
            if (xmiType.equals(Constants.CLASS)) {

                // Take ownedAttributesElements
                NodeList ownedAttributeNodeList = element.getElementsByTagName(Constants.OWNED_ATTRIBUTE_TAG);
                for (int j = 0; j < ownedAttributeNodeList.getLength(); j++) {
                    Node ownedAttributeNode = ownedAttributeNodeList.item(j);
                    Element ownedAttributeElement = (Element) ownedAttributeNode;

                    // If it has type attribute and child  it is association property
                    if (ownedAttributeElement.getAttributes().getNamedItem(Constants.TYPE_ATTR) != null && ownedAttributeElement.hasChildNodes()) {
                        String associationId = ownedAttributeElement.getAttribute(Constants.ASSOCIATION_ATTR);
                        String entityTypeId = ownedAttributeElement.getAttribute(Constants.TYPE_ATTR);
                        String memberId = ownedAttributeElement.getAttribute(Constants.XMI_ID);

//                        System.out.println(associationId);
//                        System.out.println(entityTypeId);

                        Element upperValueElement = (Element) element.getElementsByTagName(Constants.UPPER_VALUE_TAG).item(0);
                        String upperValue = upperValueElement.getAttribute(Constants.VALUE_ATTR);

                        String entityName = entityIdsAndNames.get(entityTypeId);

                        Association associationToEnrich = associations
                                .stream()
                                .filter(a -> associationId.equals(a.id))
                                .findFirst()
                                .orElseThrow();

                        if (Objects.equals(associationToEnrich.memberOne.id, memberId)) {
                            associationToEnrich.memberOne.dataType = entityName;
                            associationToEnrich.memberOne.associationType = upperValue.equals("*")
                                    ? AssociationType.Many
                                    : AssociationType.One;
                        } else {
                            associationToEnrich.memberTwo.dataType = entityName;
                            associationToEnrich.memberTwo.associationType = upperValue.equals("*")
                                    ? AssociationType.Many
                                    : AssociationType.One;
                        }

                    }
                }
            }
        }
    }

    private static void extractAssociationsWithoutFullMembers(List<Association> associations, NodeList packagedElements) {
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

    private static HashMap<String, String> parseEntitiesWithIds(NodeList packagedElements) {
        HashMap<String, String> entityIdsAndNames = new HashMap<>();
        for (int i = 0; i < packagedElements.getLength(); i++) {
            Node node = packagedElements.item(i);
            Element element = (Element) node;
            String name = element.getAttribute(Constants.NAME_ATTR);
            String id = element.getAttribute(Constants.XMI_ID);
            entityIdsAndNames.put(id, name);  //use class names for later when creating services and repos
        }
        return entityIdsAndNames;
    }
}
