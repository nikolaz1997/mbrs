package generator;

import generator.application.AppGenerator;
import generator.application.UIGenerator;
import generator.application.XmlSchemaReader;
import generator.associations.Association;
import generator.associations.AssociationParser;
import generator.entities.EntityParser;
import generator.entities.EntityProperty;
import org.w3c.dom.Element;

import java.util.*;

public class Reader {

    public static void main(String[] args) {
        try {
            Element root = XmlSchemaReader.getRootElement();

            // Loop over all packaged elements and add id and name of all entities
            // When needing a type for a Class, Enum or Association easily access the map to get the correct type
            HashMap<String, String> entityIdsAndNames = EntityParser.parseInHashMap(root);

            // Loop over all packaged elements and extract associations, so later we can determine the relations
            List<Association> associations = AssociationParser.parse(root, entityIdsAndNames);

            // App generation
            AppGenerator.generateSpringBootApplicationFile();
            AppGenerator.generatePomFile();
            HashMap<String, List<EntityProperty>> entitiesWithProperties = AppGenerator.generateModelsAndRepositories(root, entityIdsAndNames, associations);
            Map<String, Object> dataModel = new HashMap<>();
            List<String> entityNames = new ArrayList<>();

            for (String entityName : entitiesWithProperties.keySet()) {
                entityNames.add(entityName);
            }

            dataModel.put("classes", entityNames);
            AppGenerator.generateControllersAndServices();

            // UI generation
            UIGenerator.generateApp();
            UIGenerator.generateCssFile();
            UIGenerator.generateHomeFile();
            UIGenerator.generateRouterFile(dataModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
