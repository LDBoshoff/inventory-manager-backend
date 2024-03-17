package main.java.com.ldb;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class WebsiteGenerator {
    // public static void generateWebsite(String storeName, List<String> products, String outputPath)

    public static void generateWebsite() {
        // Template with placeholders
        String template = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>%s - Home</title>
            </head>
            <body>
                <h1>Welcome to %s!</h1>
                <h2>Our Products</h2>
                <ul>
                    %s
                </ul>
            </body>
            </html>
            """;

        // Generate the product list HTML
        // StringBuilder productListBuilder = new StringBuilder();
        // for (String product : products) {
        //     productListBuilder.append("<li>").append(product).append("</li>\n");
        // }

        // Fill the template with actual data
        // String filledTemplate = String.format(template, storeName, storeName, productListBuilder.toString());

        // Write the filled template to an HTML file
        // String outputPath = "c:\Users\luhah\Desktop\Portfolio Projects\Inventory Manager\frontend\stores";
        String outputPath = "C:\\Users\\luhah\\Desktop\\Portfolio Projects\\Inventory Manager\\frontend\\stores\\example.html";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            // writer.write(filledTemplate);
            writer.write(template);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
