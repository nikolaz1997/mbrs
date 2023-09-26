package generator.application;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import generator.Reader;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class TemplateGenerator {
    public static void generateWithFreeMarker(
            final String templateName,
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
