package edu.school21.HtmlGeneration;

import com.google.auto.service.AutoService;
import edu.school21.Annotations.HtmlForm;
import edu.school21.Annotations.HtmlInput;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@SupportedAnnotationTypes("edu.school21.Annotations.HtmlForm")
@SupportedSourceVersion(SourceVersion.RELEASE_18)
@AutoService(Processor.class)
public class HtmlProcessor extends AbstractProcessor {

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        Set<? extends Element> annotatedElements = roundEnv.getElementsAnnotatedWith(HtmlForm.class);
        for (Element element : annotatedElements) {
            List<? extends Element> elements = element.getEnclosedElements();
            List<Annotation> fields = getAnnotatedFields(elements);
            createHTML(element.getAnnotation(HtmlForm.class), fields);
        }
        return true;
    }

    private List<Annotation> getAnnotatedFields(List<? extends Element> elements) {
        List<Annotation> fields = new ArrayList<>(elements.size());
        for (Element element : elements) {
            if (element.getAnnotation(HtmlInput.class) != null) {
                Annotation field = element.getAnnotation(HtmlInput.class);
                fields.add(field);
            }
        }
        return fields;
    }

    public void createHTML(HtmlForm form, List<Annotation> fields) {
        try {
            FileObject fileObject = processingEnv.getFiler().createResource(StandardLocation.CLASS_OUTPUT,
                    "", form.fileName());
            try (FileWriter fileWriter = new FileWriter(fileObject.toUri().getPath());
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {

                bufferedWriter.write(String.format("<form action = \"%s\" method = \"%s\">", form.action(), form.method()));
                bufferedWriter.newLine();

                for (Annotation field : fields) {
                    HtmlInput input = (HtmlInput) field;
                    bufferedWriter.write(String.format("<input type = \"%s\" name = \"%s\" placeholder = \"%s\">",
                            input.type(), input.name(), input.placeholder()));
                    bufferedWriter.newLine();
                }

                bufferedWriter.write("<input type = \"submit\" value = \"Send\">");
                bufferedWriter.newLine();
                bufferedWriter.write("</form>");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
