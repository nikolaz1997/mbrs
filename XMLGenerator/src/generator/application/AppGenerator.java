package generator.application;

import generator.Constants;
import generator.FileSaver;
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
        String className = "@SpringBootApplication" + "\npublic class " + appName + "Application {\n";
        String classCode = "\tpublic static void main(String[] args) {\n" +
                "\t\tSpringApplication.run(" + appName + "Application.class, args);\n" +
                "\t}" +
                "\n}";
        FileSaver.save(className + classCode, appName + "Application");
    }

    public static void generatePomFile() {
        try {
            String pomContent = Files.readString(Paths.get("./src/pom_content"));
            FileWriter writer = new FileWriter("pom.xml");
            writer.write(pomContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Generates classes for representing model
    public static void generateModel(final Element root, HashMap<String, String> entityIdsAndNames, List<Association> associations) {
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
//                    System.out.println("Data-Name: " + name);

                // Type of packagedElement
                String type = element.getAttribute(Constants.XMI_TYPE_ATTR);
//                    System.out.println("Data-Type: " + type + "\n");

                // This if excludes creation of enums and associations as they can be standalone packagedElements
                // enums are created in else part
                if ((!Constants.ENUMERATION.equals(type) && (!Constants.ASSOCIATION.equals(type)))) {

                    // Getting fields for Classes
                    NodeList ownedAttributeNodeList = element.getElementsByTagName(Constants.OWNED_ATTRIBUTE_TAG);
                    for (int j = 0; j < ownedAttributeNodeList.getLength(); j++) {
                        Node ownedAttributeNode = ownedAttributeNodeList.item(j);
                        Element ownedAttributeElement = (Element) ownedAttributeNode;

                        // This if excludes generating Associations inside class (Associations have type attribute)
                        // should be updated when we want to add them for fields
                        if (ownedAttributeElement.getAttributes().getNamedItem(Constants.TYPE_ATTR) == null) {
                            Node typeNode = ownedAttributeElement.getElementsByTagName(Constants.TYPE_ATTR).item(0);  //takes type element inside ownedAttribute
                            Element typeElement = (Element) typeNode;

                            Node xmiExtensionNode = typeElement.getElementsByTagName(Constants.XMI_EXTENSION).item(0);  //takes xmi:Extension inside type element
                            Element xmiExtensionElement = (Element) xmiExtensionNode;

                            Node referenceExtensionNode = xmiExtensionElement.getElementsByTagName(Constants.REFERENCE_EXTENSION_TAG).item(0); //takes referenceExtension inside xmiExtension
                            Element referenceExtensionElement = (Element) referenceExtensionNode;

                            final var fieldTypes = Arrays.stream(referenceExtensionElement.getAttribute(Constants.REFERENT_PATH_ATTR).split("::"))
                                    .map(String.class::cast)
                                    .toList();

//                                System.out.println(fieldTypes.get(3));
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
                                // this section is used for creating associations inside classes: based on values add to fieldAndTypes
                                String associationId = ownedAttributeElement.getAttribute(Constants.ASSOCIATION_ATTR);
                                String associationMemberId = ownedAttributeElement.getAttribute(Constants.XMI_ID);

                                Association foundAssociation = associations
                                        .stream()
                                        .filter(a -> associationId.equals(a.id))
                                        .findFirst()
                                        .orElseThrow();

                                if (foundAssociation.memberOne.id.equals(associationMemberId)) {
                                    String typeOfAssociationProperty = foundAssociation.memberOne.associationType == AssociationType.One
                                            ? foundAssociation.memberOne.dataType
                                            : "List<" + foundAssociation.memberOne.dataType + ">";

                                    String nameOfAssociationProperty = foundAssociation.memberOne.associationType == AssociationType.One
                                            ? foundAssociation.memberOne.dataType
                                            : foundAssociation.memberOne.dataType + "s";

                                    String association = calculateAssociation(foundAssociation.memberOne.associationType, foundAssociation.memberTwo.associationType);

                                    String decorator = "";

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

                                    currentEntityProperties.add(new EntityProperty(typeOfAssociationProperty, nameOfAssociationProperty, association, decorator));
                                } else {
                                    String typeOfAssociationProperty = foundAssociation.memberTwo.associationType == AssociationType.One
                                            ? foundAssociation.memberTwo.dataType
                                            : "List<" + foundAssociation.memberTwo.dataType + ">";

                                    String nameOfAssociationProperty = foundAssociation.memberTwo.associationType == AssociationType.One
                                            ? foundAssociation.memberTwo.dataType
                                            : foundAssociation.memberTwo.dataType + "s";

                                    String association = calculateAssociation(foundAssociation.memberTwo.associationType, foundAssociation.memberOne.associationType);

                                    String decorator = "";

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

                                    currentEntityProperties.add(new EntityProperty(typeOfAssociationProperty, nameOfAssociationProperty, association, decorator));
                                }
                            }
                        }
                    }
//                    System.out.println("-------------");
//                    System.out.println(name);
//                    for (EntityProperty property: currentEntityProperties) {
//                        System.out.println(property);
//                    }
//                    System.out.println("-------------");
                    createEntityClass(name, currentEntityProperties);
                    currentEntityProperties.clear();
                } else {
                    if (Constants.ENUMERATION.equals(type)) {
                        final var enumFields = getEnumFields(element);
                        createEnum(name, enumFields);
                    } else {
                        // Prints names of Associations
//                        System.out.println(element.getAttribute("name"));
                    }
                }
            }
        }
    }

    public static void generateCrud() {
        String baseDirectory = "./";
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
        String classAnnotations = """
                @Service
                @RequiredArgsConstructor
                """;

        String className = String.format("public class %s" + "Service {%n", entityName);

        String repositoryType = String.format("%sRepository", entityName);
        String repositoryName = String.format("%sRepository", entityName.toLowerCase());

        String constructor = String.format("""
                                
                \tpublic %sService(%s %s) {
                \t     this.%s = %s;
                \t}
                """, entityName, repositoryType, repositoryName, repositoryName, repositoryName);

        String code = String.format("\tprivate final %sRepository %sRepository;%n", entityName, entityName.toLowerCase());

        String comment = "\t// TODO: Add more service functions that are required by your logic.";

        String classCode = classAnnotations + className + code + constructor + comment + "\n}";

        FileSaver.save(classCode, entityName.concat("Service"));
    }

    private static void generateController(final String entityName) {
        String classAnnotations =
                "@RestController\n" +
                        "@RequiredArgsConstructor\n" +
                        "@RequestMapping(value = \"/api/" + entityName.toLowerCase().concat("s") + "\")\n";

        String className = String.format("public class %s" + "Controller {%n", entityName);

        String serviceType = String.format("%sService", entityName);
        String serviceName = String.format("%sService", entityName.toLowerCase());

        String code = String.format("\n\tprivate final %s %s;%n", serviceType, serviceName);

        String constructor = String.format("""
                                
                \tpublic %sController(%s %s) {
                \t     this.%s = %s;
                \t}
                """, entityName, serviceType, serviceName, serviceName, serviceName);

        String codeComment = "\n\t\t// TODO: Implement logic that is convenient for your controller method\n";

        String basicLogicCode =
                "\n\t@GetMapping()\n" +
                        "\t@ResponseStatus(HttpStatus.OK)\n" +
                        "\tpublic List<" + entityName + "Entity> get" + entityName + "s() { " + codeComment + "\n\t\treturn null;\n\t}\n" +
                        "\n\t@PostMapping()\n" +
                        "\t@ResponseStatus(HttpStatus.CREATED)\n" +
                        "\tpublic " + entityName + "Entity create" + entityName + "() { " + codeComment + "\n\t\treturn null;\n\t}\n" +
                        "\n\t@PutMapping()\n" +
                        "\t@ResponseStatus(HttpStatus.NO_CONTENT)\n" +
                        "\tpublic " + entityName + "Entity update" + entityName + "() { " + codeComment + "\n\t\treturn null;\n\t}\n" +
                        "\n\t@DeleteMapping()\n" +
                        "\t@ResponseStatus(HttpStatus.NO_CONTENT)\n" +
                        "\tpublic void delete" + entityName + "() {}";

        String comment = "\t// TODO: Adjust method annotations and add more controller functions that are required by your logic.";

        String classCode = classAnnotations + className + comment + code + constructor + basicLogicCode + "\n}";

        FileSaver.save(classCode, entityName.concat("Controller"));
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
        String classAnnotations = "@Entity\n" +
                "@Getter\n" +
                "@Setter\n" +
                "@ToString\n" +
                "@AllArgsConstructor\n" +
                "@NoArgsConstructor\n" +
                "@Builder(toBuilder = true)\n" +
                "@Table(name=\"" + name.toLowerCase() + "\")\n";

        String className = String.format("public class %s {%n", name);

        List<String> classFields = properties.stream()
                .map(property -> {
                    final var fieldRow = "\tprivate " + property.type + " " + property.name.toLowerCase();
                    if ("Id".equals(property.name) && property.association == null) {
                        generateRepository(name, property.type);
                        return "\t@Id\n" + fieldRow;
                    } else if (property.association == null) {
                        return "\t@Column(name=\"" + property.name.toLowerCase() + "\")\n" + fieldRow;
                    } else {
                        return property.decorator + "\n" + fieldRow;
                    }
                })
                .map(String.class::cast)
                .toList();

        String classCode = classAnnotations + className + String.join(";\n\n", classFields) + ";\n}";

        FileSaver.save(classCode, name.concat("Entity"));
    }

    private static void createEnum(final String name, final List<String> enumFields) {

        String enumName = String.format("public enum %s {%n", name);
        FileSaver.save(enumName + "\t" + String.join(", ", enumFields) + "\n}", name);
    }

    private static void generateRepository(final String entityName, final String keyType) {
        String classAnnotations = "@Repository\n";

        String className = String.format("public interface %sRepository extends JpaRepository<%sEntity, %s> {%n", entityName, entityName, keyType);

        String comment = "\t// TODO: Add more repository functions that are required by your logic.";

        String classCode = classAnnotations + className + comment + "\n}";

        FileSaver.save(classCode, entityName.concat("Repository"));
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
}
