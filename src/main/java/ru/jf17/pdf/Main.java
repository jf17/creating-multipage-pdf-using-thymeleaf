package ru.jf17.pdf;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Service service = new Service();


        var model = Map.of(
                "title", "Hello world!",
                "pages", List.of(1, 2, 3));

        byte[] pdf = service.generatePdf(model);
        service.save(pdf);

        var pdfAsText = service.pdfToText(pdf);
    }




}