package ru.jf17.pdf;

import org.apache.fop.apps.FopFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Locale;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.xmlgraphics.util.MimeConstants.MIME_PDF;
import static org.thymeleaf.templatemode.TemplateMode.XML;

public class Service {

    void save(byte[] pdf) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(new File("generated.pdf")))) {
            out.write(pdf);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    String pdfToText(byte[] pdf) {
        try (PDDocument document = PDDocument.load(pdf)) {
            return new PDFTextStripper().getText(document);
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    byte[] generatePdf(Map<String, Object> model) {
        try {
            var xslFo = processXslFo(model);

            var out = new ByteArrayOutputStream();


            File configFile = new File("fop.xconf");
            DefaultHandler defaultHandler = FopFactory.newInstance(configFile)
                    .newFop(MIME_PDF, out)
                    .getDefaultHandler();

            Source src = new StreamSource(new StringReader(xslFo));
            Result res = new SAXResult(defaultHandler);

            TransformerFactory.newInstance()
                    .newTransformer()
                    .transform(src, res);

            return out.toByteArray();
        } catch (IOException | SAXException | TransformerException e) {
            throw new RuntimeException();
        }
    }

    private String processXslFo(Map<String, Object> model) {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setCharacterEncoding(UTF_8.name());
        templateResolver.setTemplateMode(XML);
        templateResolver.setPrefix("/");

        TemplateEngine engine = new TemplateEngine();
        engine.setTemplateResolver(templateResolver);

        return engine.process("myfile.fo", new Context(Locale.getDefault(), Map.of("model", model)));
    }
}
