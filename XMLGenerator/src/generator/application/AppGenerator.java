package generator.application;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import generator.Constants;
import generator.FileSaver;
import generator.Reader;
import generator.associations.Association;
import generator.associations.AssociationType;
import generator.entities.EntityProperty;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class AppGenerator {
    public static void generateSpringBootApplicationFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Provide name for the application:");
        String appName = scanner.nextLine();

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("appName", appName);

        generateWithFreeMarker("spring_app.ftl", dataModel, "output", appName + "Application.java");
    }

    public static void generatePomFile() {
        Map<String, Object> dataModel = new HashMap<>();

        generateWithFreeMarker("pom_content.ftl", dataModel, "output", "pom.xml");
    }

    public static void generateModelsAndRepositories(final Element root, HashMap<String, String> entityIdsAndNames, List<Association> associations) {
        List<EntityProperty> currentEntityProperties = new ArrayList<>();

        Node model = root.getChildNodes().item(3);
        if (model.getNodeType() == Node.ELEMENT_NODE) {
            Element modelElement = (Element) model;
            NodeList nodeList = modelElement.getElementsByTagName(Constants.PACKAGED_ELEMENT_TAG);

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                // Name of packagedElement
                String name = element.getAttribute(Constants.NAME_ATTR);

                // Type of packagedElement
                String type = element.getAttribute(Constants.XMI_TYPE_ATTR);

                // This if excludes creation of enums and associations as they can be standalone packagedElements
                // enums are created in else part
                if (Constants.CLASS.equals(type)) {

                    // Getting fields for Classes
                    NodeList ownedAttributeNodeList = element.getElementsByTagName(Constants.OWNED_ATTRIBUTE_TAG);
                    for (int j = 0; j < ownedAttributeNodeList.getLength(); j++) {
                        Node ownedAttributeNode = ownedAttributeNodeList.item(j);
                        Element ownedAttributeElement = (Element) ownedAttributeNode;

                        // This if excludes generating Associations inside class (Associations have type attribute)
                        if (ownedAttributeElement.getAttributes().getNamedItem(Constants.TYPE_ATTR) == null) {
                            // Takes type element inside ownedAttribute
                            Node typeNode = ownedAttributeElement.getElementsByTagName(Constants.TYPE_ATTR).item(0);
                            Element typeElement = (Element) typeNode;

                            // Takes xmi:Extension inside type element
                            Node xmiExtensionNode = typeElement.getElementsByTagName(Constants.XMI_EXTENSION).item(0);
                            Element xmiExtensionElement = (Element) xmiExtensionNode;

                            // Takes referenceExtension inside xmiExtension
                            Node referenceExtensionNode = xmiExtensionElement.getElementsByTagName(Constants.REFERENCE_EXTENSION_TAG).item(0);
                            Element referenceExtensionElement = (Element) referenceExtensionNode;

                            final var fieldTypes = Arrays.stream(referenceExtensionElement.getAttribute(Constants.REFERENT_PATH_ATTR).split("::"))
                                    .map(String.class::cast)
                                    .toList();

                            currentEntityProperties.add(
                                    new EntityProperty(
                                            fieldTypes.get(3),
                                            ownedAttributeElement.getAttribute(Constants.NAME_ATTR),
                                            null,
                                            null
                                    )
                            );
                        } else {
                            // this section creates enum field inside class
                            if (!ownedAttributeElement.hasChildNodes()) {   // Enums don't have nodes; Associations have nodes
                                String enumerationTypeId = ownedAttributeElement.getAttribute(Constants.TYPE_ATTR);
                                String enumerationType = entityIdsAndNames.get(enumerationTypeId);
                                currentEntityProperties.add(
                                        new EntityProperty(
                                                enumerationType,
                                                ownedAttributeElement.getAttribute(Constants.NAME_ATTR),
                                                null,
                                                null
                                        )
                                );
                            } else {
                                // this section is used for creating associations inside classes: based on values add to currentEntityProperties
                                String associationId = ownedAttributeElement.getAttribute(Constants.ASSOCIATION_ATTR);
                                String associationMemberId = ownedAttributeElement.getAttribute(Constants.XMI_ID);

                                Association foundAssociation = associations
                                        .stream()
                                        .filter(a -> associationId.equals(a.id))
                                        .findFirst()
                                        .orElseThrow();

                                String typeOfAssociationProperty;
                                String nameOfAssociationProperty;
                                String association;
                                String decorator = "";

                                // If the current owned attribute is referred to first member of the association
                                if (foundAssociation.memberOne.id.equals(associationMemberId)) {
                                    typeOfAssociationProperty = foundAssociation.memberOne.associationType == AssociationType.One
                                            ? foundAssociation.memberOne.dataType
                                            : "List<" + foundAssociation.memberOne.dataType + ">";

                                    nameOfAssociationProperty = foundAssociation.memberOne.associationType == AssociationType.One
                                            ? foundAssociation.memberOne.dataType
                                            : foundAssociation.memberOne.dataType + "s";

                                    association = calculateAssociation(foundAssociation.memberOne.associationType, foundAssociation.memberTwo.associationType);

                                    // Logic for extracting decorator on model based on association with another member
                                    if (association.equals("OneToOne")) {
                                        decorator = "\t@OneToOne" + "\n" + "\t@JoinColumn(name=\"" + nameOfAssociationProperty.toLowerCase() + "_id" + "\")";
                                    }

                                    if (association.equals("ManyToOne")) {
                                        decorator = "\t@ManyToOne" + "\n" + "\t@JoinColumn(name=\"" + nameOfAssociationProperty.toLowerCase() + "_id" + "\")";
                                    }

                                    if (association.equals("OneToMany")) {
                                        decorator = "\t@OneToMany(mappedBy=\"" + name.toLowerCase() + "\")";
                                    }

                                    if (association.equals("ManyToMany")) {
                                        decorator = "\t@ManyToMany(CascadeType.ALL)" + "\n" +
                                                "\t@JoinTable(name=\"" + foundAssociation.memberOne.dataType.toLowerCase() + "_" + name.toLowerCase() + "\", " + "\n\t" +
                                                "\t\tjoinColumns = @JoinColumn(name=\"" + foundAssociation.memberOne.dataType.toLowerCase() + "_id\", referencedColumnName=\"id\"), " + "\n\t" +
                                                "\t\tinverseJoinColumns = @JoinColumn(name=\"" + name.toLowerCase() + "_id\", referencedColumnName=\"id\"))";
                                    }

                                } else {
                                    // If the current owned attribute is referred to second member of the association
                                    typeOfAssociationProperty = foundAssociation.memberTwo.associationType == AssociationType.One
                                            ? foundAssociation.memberTwo.dataType
                                            : "List<" + foundAssociation.memberTwo.dataType + ">";

                                    nameOfAssociationProperty = foundAssociation.memberTwo.associationType == AssociationType.One
                                            ? foundAssociation.memberTwo.dataType
                                            : foundAssociation.memberTwo.dataType + "s";

                                    association = calculateAssociation(foundAssociation.memberTwo.associationType, foundAssociation.memberOne.associationType);

                                    // Logic for extracting decorator on model based on association with another member
                                    if (association.equals("OneToOne")) {
                                        decorator = "\t@OneToOne(mappedBy=\"" + name.toLowerCase() + "\")";
                                    }

                                    if (association.equals("ManyToOne")) {
                                        decorator = "\t@ManyToOne" + "\n" + "\t@JoinColumn(name=\"" + nameOfAssociationProperty.toLowerCase() + "_id" + "\")";
                                    }

                                    if (association.equals("OneToMany")) {
                                        decorator = "\t@OneToMany(mappedBy=\"" + name.toLowerCase() + "\")";
                                    }

                                    if (association.equals("ManyToMany")) {
                                        decorator = "\t@ManyToMany(mappedBy=\"" + name.toLowerCase() + "s" + "\")";
                                    }

                                }
                                currentEntityProperties.add(new EntityProperty(typeOfAssociationProperty, nameOfAssociationProperty, association, decorator));
                            }
                        }
                    }
                    createEntityClass(name, currentEntityProperties);
                    createDtoClass(name, currentEntityProperties);
                    currentEntityProperties.clear();
                } else if (Constants.ENUMERATION.equals(type)) {
                    final var enumFields = getEnumFields(element);
                    createEnum(name, enumFields);
                }
                // Associations were handled before model generation as to have access to them
            }
        }
    }

    public static void generateControllersAndServices() {
        String baseDirectory = "./output/model";
        String partialClassName = "Entity";
        String javaExtension = ".java";

        try {
            File directory = new File(baseDirectory);
            File[] files = directory.listFiles();

            // Iterate through the files and check if they are java class files and contain the partial name
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(javaExtension)) {
                    String className = file.getName().replace(javaExtension, "");
                    if (className.contains(partialClassName)) {
                        String name = className.replace("Entity", "");
                        generateService(name);
                        generateController(name);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Class not found: " + e.getMessage());
        }
    }


    private static void generateService(final String entityName) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("className", entityName);

        generateWithFreeMarker("service_class.ftl", dataModel, "output/service", entityName + "Service.java");
    }

    private static void generateController(final String entityName) {

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("className", entityName);

        generateWithFreeMarker("controller_class.ftl", dataModel, "output/controller", entityName + "Controller.java");
    }

    private static String calculateAssociation(AssociationType associationTypeOne, AssociationType associationTypeTwo) {
        if (associationTypeOne == AssociationType.One && associationTypeTwo == AssociationType.One)
            return "OneToOne";

        if (associationTypeOne == AssociationType.One && associationTypeTwo == AssociationType.Many)
            return "ManyToOne";

        if (associationTypeOne == AssociationType.Many && associationTypeTwo == AssociationType.One)
            return "OneToMany";

        if (associationTypeOne == AssociationType.Many && associationTypeTwo == AssociationType.Many)
            return "ManyToMany";

        return "Error";
    }

    private static void createEntityClass(final String name, final List<EntityProperty> properties) {

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableName", name.toLowerCase());
        dataModel.put("className", name);

        List<String> classFields = properties.stream()
                .map(property -> {
                    final var fieldRow = "\tprivate " + property.type + " " + property.name.toLowerCase();
                    if ("Id".equals(property.name) && property.association == null) {
                        generateRepository(name, property.type);
                        return "@Id\n" + fieldRow;
                    } else if (property.association == null) {
                        return "@Column(name=\"" + property.name.toLowerCase() + "\")\n" + fieldRow;
                    } else {
                        return property.decorator + "\n" + fieldRow;
                    }
                })
                .map(String.class::cast)
                .toList();

        dataModel.put("classFields", classFields);

        generateWithFreeMarker("entity_class.ftl", dataModel, "output/model",name + "Entity.java");
    }

    private static void createDtoClass(final String name, final List<EntityProperty> properties) {

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("tableName", name.toLowerCase());
        dataModel.put("className", name);

        List<String> classFields = properties.stream()
                .map(property -> {
                    final var fieldRow = "\tprivate " + property.type + " " + property.name.toLowerCase();
                    return fieldRow;
                })
                .map(String.class::cast)
                .toList();

        dataModel.put("classFields", classFields);

        generateWithFreeMarker("dto_class.ftl", dataModel, "output/dto",name + "DTO.java");
    }

    private static void createEnum(final String name, final List<String> enumFields) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("enumName", name);
        dataModel.put("enumFields", enumFields);

        generateWithFreeMarker("enum.ftl", dataModel, "output/model", name + "Enum.java");
    }

    private static void generateRepository(final String entityName, final String keyType) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("repositoryName", entityName);
        dataModel.put("className", entityName);
        dataModel.put("keyType", keyType);

        generateWithFreeMarker("repository_class.ftl", dataModel, "output/repository", entityName + "Repository.java");
    }

    private static List<String> getEnumFields(final Element element) {
        List<String> enumFields = new ArrayList<>();
        NodeList enumOwnedLiteral = element.getElementsByTagName("ownedLiteral");
        // Get enum values
        for (int i = 0; i < enumOwnedLiteral.getLength(); i++) {
            Node enumNode = enumOwnedLiteral.item(i);
            Element enumElement = (Element) enumNode;
            enumFields.add(enumElement.getAttribute("name").toUpperCase());
        }
        return enumFields;
    }

    private static void generateWithFreeMarker(final String templateName,
                                               final Map<String, Object> dataModel,
                                               final String outputDirectory,
                                               final String fileName) {
        try {
            Configuration cfg = new Configuration(Configuration.VERSION_2_3_32);

            cfg.setClassForTemplateLoading(Reader.class, "/templates");

            Template template = cfg.getTemplate(templateName);

            File outputDir = new File(outputDirectory);
            outputDir.mkdirs();

            // Process the template with the data model
            FileWriter writer = new FileWriter(new File(outputDir, fileName));

            template.process(dataModel, writer);
            writer.close();
        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }
    }
}
