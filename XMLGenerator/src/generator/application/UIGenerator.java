package generator.application;

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

    public static void generateRouterFile(Map<String, Object> dataModel) {
        TemplateGenerator.generateWithFreeMarker("router.ftl", dataModel, "output/frontend", "Router.jsx");
    }

    public static void generateHeaderFile(Map<String, Object> dataModel) {
        TemplateGenerator.generateWithFreeMarker("header.ftl", dataModel, "output/frontend", "Header.jsx");
    }
}
