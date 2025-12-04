import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

public class PPMAnalyzer {

    public static PPMImage read_PPM(String filename) throws IOException {
        Scanner scan = new Scanner(new java.io.File(filename));

        // Read the PPM header (check for "P3")
        String line = scan.next();
        if (!line.trim().equals("P3")) {
            scan.close();
            throw new IOException("Invalid PPM file: Missing P3 header");
        }

        // ===Assume no comments for simplicity===

        // Read width and height
        int width = scan.nextInt();
        int height = scan.nextInt();

        // Check if the image is square (if not throw an exception)
        if (width != height) {
            scan.close();
            throw new IllegalArgumentException("Image is not square: width " + width + " != height " + height);
        }

        // Read max color value
        int maxColorValue = scan.nextInt();

        // Initialize PPMImage object
        PPMImage image = new PPMImage(width, height, maxColorValue);

        // Read pixel data
        int[][][] pixels = new int[height][width][3];
        int r, g, b;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                r = scan.nextInt();
                g = scan.nextInt();
                b = scan.nextInt();
                pixels[i][j][0] = r;
                pixels[i][j][1] = g;
                pixels[i][j][2] = b;
            }
        }
        image.setPixels(pixels);
        scan.close();
        return image;
    }

    public static void write_PPM(String filename, PPMImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int maxColorValue = image.getMaxColorValue();
        int[][][] pixels = image.getPixels(); // height x width x 3(for RGB)

        PrintWriter writer = null;
        try {
            writer = new PrintWriter(new java.io.File(filename));
            // Write PPM header (our type: P3)
            writer.println("P3");
            writer.println(width + " " + height);
            writer.println(maxColorValue);

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    writer.println(pixels[i][j][0] + " " + pixels[i][j][1] + " " + pixels[i][j][2]);
                    if (j < width - 1) {
                        writer.print(" ");
                    }
                }
                writer.println();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}

