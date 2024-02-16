package ru.jf17.pdf;

import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {

        Service testee = new Service();



        var model = Map.of(
                "title", "Hello world!",
                "pages", List.of(1, 2, 3));

        byte[] pdf = testee.generatePdf(model);
        testee.save(pdf);

        var pdfAsText = testee.pdfToText(pdf);
    }




}