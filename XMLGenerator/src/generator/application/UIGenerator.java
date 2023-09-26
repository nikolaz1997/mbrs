package generator.application;

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
}
