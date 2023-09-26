package generator.application;

public class UIGenerator {
    public static void generateApp() {
        TemplateGenerator.generateWithFreeMarker("app.ftl", null, "output/frontend", "App.jsx");
    }
}
