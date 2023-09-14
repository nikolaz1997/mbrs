package generator;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Reader {

    private static final String CLASS = "uml:Class";
    private static final String ENUMERATION = "uml:Enumeration";
    private static final String ASSOCIATION = "uml:Association";
    public static final String PACKAGED_ELEMENT_TAG = "packagedElement";
    public static final String OWNED_ATTRIBUTE_TAG = "ownedAttribute";
    public static final String NAME_ATTR = "name";
    public static final String XMI_TYPE_ATTR = "xmi:type";

    private static List<String> entityNames = new ArrayList<>();
    private static Map<String, String> fieldsAndTypes = new HashMap<>();
    private static Map<String, String> enumsTypes = new HashMap<>();

    public static void main(String[] args) {
        try {
            Element root = getElement();

            generateSpringBootApplicationFile();
            generatePomFile();
            generateModel(root);
            generateCrud();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Generates classes for representing model
    private static void generateModel(final Element root) {
        Node model = root.getChildNodes().item(3);
        if (model.getNodeType() == Node.ELEMENT_NODE) {
            Element modelElement = (Element) model;
            NodeList nodeList = modelElement.getElementsByTagName(PACKAGED_ELEMENT_TAG);
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element element = (Element) node;

                // Name of packagedElement
                String name = element.getAttribute(NAME_ATTR);
//                    System.out.println("Data-Name: " + name);

                // Type of packagedElement
                String type = element.getAttribute(XMI_TYPE_ATTR);
//                    System.out.println("Data-Type: " + type + "\n");

                // This if excludes creation of enums and associations as they can be standalone packagedElements
                // enums are created in else part
                if ((!ENUMERATION.equals(element.getAttribute(XMI_TYPE_ATTR)) && (!ASSOCIATION.equals(element.getAttribute(XMI_TYPE_ATTR))))) {
                    entityNames.add(name);  //use class names for later when creating services and repos

                    // Getting fields for Classes
                    NodeList ownedAttributeNodeList = element.getElementsByTagName(OWNED_ATTRIBUTE_TAG);
                    for (int j = 0; j < ownedAttributeNodeList.getLength(); j++) {
                        Node ownedAttributeNode = ownedAttributeNodeList.item(j);
                        Element ownedAttributeElement = (Element) ownedAttributeNode;

                        // This if excludes generating Associations inside class (Associations have type attribute)
                        // should be updated when we want to add them for fields
                        if (ownedAttributeElement.getAttributes().getNamedItem("type") == null) {
                            Node typeNode = ownedAttributeElement.getElementsByTagName("type").item(0);  //takes type element inside ownedAttribute
                            Element typeElement = (Element) typeNode;

                            Node xmiExtensionNode = typeElement.getElementsByTagName("xmi:Extension").item(0);  //takes xmi:Extension inside type element
                            Element xmiExtensionElement = (Element) xmiExtensionNode;

                            Node referenceExtensionNode = xmiExtensionElement.getElementsByTagName("referenceExtension").item(0); //takes referenceExtension inside xmiExtension
                            Element referenceExtensionElement = (Element) referenceExtensionNode;

                            final var fieldTypes = Arrays.stream(referenceExtensionElement.getAttribute("referentPath").split("::"))
                                    .map(String.class::cast)
                                    .toList();

//                                System.out.println(fieldTypes.get(3));
                            fieldsAndTypes.put(ownedAttributeElement.getAttribute("name"), fieldTypes.get(3));
                        } else {
                            // this section creates enum field inside class
                            // this might be used for creating associations inside classes !?
                            if (!ownedAttributeElement.hasChildNodes()) {   // Enums don't have nodes; Associations have nodes
                                fieldsAndTypes.put(ownedAttributeElement.getAttribute("name"), ownedAttributeElement.getAttribute("name"));
                            } else {
                                Element lowerValue = (Element) element.getElementsByTagName("lowerValue").item(0);
                                System.out.println(lowerValue.getAttribute("value"));
                            }
                        }
                    }
                    createEntityClass(name, fieldsAndTypes);
                } else {
                    if (ENUMERATION.equals(type)) {
                        final var enumFields = getEnumFields(element);
                        createEnum(name, enumFields);
                    } else {
                        // Prints names of Associations
                        System.out.println(element.getAttribute("name"));
                    }
                }
            }
        }
    }

    private static void generateSpringBootApplicationFile() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Provide name for the application:");
        String appName = scanner.nextLine();
        String className = "@SpringBootApplication" + "\npublic class " + appName + "Application {\n";
        String classCode = "\tpublic static void main(String[] args) {\n" +
                "\t\tSpringApplication.run(" + appName + "Application.class, args);\n" +
                "\t}" +
                "\n}";
        saveToFile(className + classCode, appName + "Application");
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

    private static void createEntityClass(final String name, final Map<String, String> fieldsAndTypes) {
        String classAnnotations = "@Entity\n" +
                                  "@Getter\n" +
                                  "@Setter\n" +
                                  "@ToString\n" +
                                  "@AllArgsConstructor\n" +
                                  "@NoArgsConstructor\n" +
                                  "@Builder(toBuilder = true)\n" +
                                  "@Table(name=\"" + name.toLowerCase() + "\")\n";

        String className = String.format("public class %s {%n", name);

        List<String> classFields = fieldsAndTypes.keySet().stream()
                        .map(key -> {
                            final var fieldRow = "\tprivate " + fieldsAndTypes.get(key) + " " + key.toLowerCase();
                            if ("Id".equals(key)) {
                                generateRepository(name, fieldsAndTypes.get(key));
                                return "\t@Id\n" + fieldRow;
                            } else {
                                return "\t@Column(name=\"" + key.toLowerCase() + "\")\n" + fieldRow;
                            }
                        })
                        .map(String.class::cast)
                        .toList();

        String classCode = classAnnotations + className + String.join(";\n", classFields) + ";\n}";

        saveToFile(classCode, name.concat("Entity"));
    }

    private static void createEnum(final String name, final List<String> enumFields) {

        String enumName = String.format("public enum %s {%n", name);
        saveToFile(enumName + "\t" + String.join(", ", enumFields) + "\n}", name);
    }

    private static Element getElement() throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse("./src/schema2.xml");
        document.getDocumentElement().normalize();

        return document.getDocumentElement();
    }

    private static void generatePomFile() {
        try {
            String pomContent = Files.readString(Paths.get("./src/pom_content"));
            FileWriter writer = new FileWriter("pom.xml");
            writer.write(pomContent);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateCrud() {
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

    private static void generateRepository(final String entityName, final String keyType) {
        String classAnnotations = "@Repository\n";

        String className = String.format("public interface %sRepository extends JpaRepository<%sEntity, %s> {%n", entityName, entityName, keyType);

        String comment = "\t// TODO: Add more repository functions that are required by your logic.";

        String classCode = classAnnotations + className + comment + "\n}";

        saveToFile(classCode, entityName.concat("Repository"));
    }

    private static void generateService(final String entityName) {
        String classAnnotations = """
                @Service
                @RequiredArgsConstructor
                """;

        String className = String.format("public class %s" + "Service {%n", entityName);

        String code = String.format("\tprivate final %sRepository %sRepository;%n", entityName, entityName.toLowerCase());

        String comment = "\t// TODO: Add more service functions that are required by your logic.";

        String classCode = classAnnotations + className + code + comment + "\n}";

        saveToFile(classCode, entityName.concat("Service"));
    }

    private static void generateController(final String entityName) {
        String classAnnotations =
                "@RestController\n" +
                "@RequiredArgsConstructor\n" +
                "@RequestMapping(value = \"/api/" + entityName.toLowerCase().concat("s") + "\")\n";

        String className = String.format("public class %s" + "Controller {%n", entityName);

        String code = String.format("\n\tprivate final %sService %sService;%n", entityName, entityName.toLowerCase());

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

        String classCode = classAnnotations + className + comment + code + basicLogicCode + "\n}";

        saveToFile(classCode, entityName.concat("Controller"));
    }

    private static void saveToFile(final String code, final String fileName) {
        try (FileWriter writer = new FileWriter(fileName.concat(".java"))) {
            writer.write(code);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
