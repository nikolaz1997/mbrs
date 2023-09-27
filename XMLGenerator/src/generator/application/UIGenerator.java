package generator.application;

import generator.entities.EntityProperty;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UIGenerator {
    public static void generateApp() {
        TemplateGenerator.generateWithFreeMarker("app.ftl", null, "output/frontend", "App.jsx");
    }

    public static void generateCssFile() {
        TemplateGenerator.generateWithFreeMarker("css.ftl", null, "output/frontend", "styles.css");
    }

    public static void generateHomeFile() {
        TemplateGenerator.generateWithFreeMarker("home.ftl", null, "output/frontend", "Home.jsx");
    }

    public static void generateRouterFile(final List<String> entityNames) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("classes", entityNames);
        TemplateGenerator.generateWithFreeMarker("router.ftl", dataModel, "output/frontend", "Router.jsx");
    }

    public static void generateHeaderFile(final List<String> entityNames) {
        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("classes", entityNames);
        TemplateGenerator.generateWithFreeMarker("header.ftl", dataModel, "output/frontend", "Header.jsx");
    }

    public static void generateEntitiesListFileAndForms(HashMap<String, List<EntityProperty>> entitiesWithProperties) {
        for (String className : entitiesWithProperties.keySet()) {
            Map<String, Object> dataModel = new HashMap<>();
            dataModel.put("className", className);

            // Retrieve properties for the current class
            List<EntityProperty> properties = entitiesWithProperties.get(className);

            // Add the property list to the data model
            dataModel.put("properties", properties);

            TemplateGenerator.generateWithFreeMarker("listView.ftl", dataModel, "output/frontend", className + "ListView.jsx");
            TemplateGenerator.generateWithFreeMarker("formCreate.ftl", dataModel, "output/frontend", className + "Create.jsx");
            TemplateGenerator.generateWithFreeMarker("formUpdate.ftl", dataModel, "output/frontend", className + "Update.jsx");
        }
    }
}
