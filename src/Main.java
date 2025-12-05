import java.io.IOException;

public class Main {


    public static void main(String[] args) throws IOException {
        boolean compression_flag = false;
        boolean edge_flag = false;
        boolean outline_flag = false;
        String input_file = null;
        String output_file = null;

       double[] compression_levels = {0.002, 0.004, 0.01, 0.033, 0.077, 0.2, 0.5, 0.65};
       double[] tolerance_levels = {0.0005, 0.0007, 0.0015, 0.002, 0.007, 0.015, 0.02, 0.03};

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                compression_flag = true;
            } else if (args[i].equals("-e")) {
                edge_flag = true;
            } else if (args[i].equals("-t")) {
                outline_flag = true;
            }

            if (args[i].equals("-i"))
                input_file = args[i+1];

            if (args[i].equals("-o"))
                output_file = args[i+1];
        }

        if (input_file == null || output_file == null) {
            System.out.println("Input or output file not specified. Invalid arguments.");
            return;
        }

        if (!compression_flag && !edge_flag) {
            System.out.println("Either -c or -e must be specified.");
            return;
        }

        PPMImage image = PPMAnalyzer.read_PPM(input_file);
        int width  = image.getWidth();
        int height = image.getHeight();

        // Only square images are allowed
        if (width != height) {
            System.out.println("Error: Only square images are supported in this assignment.");
            return;
        }

        if (compression_flag){
            for (int i = 0; i < compression_levels.length; i++) {
                double threshold = findThreshold(image,
                                    compression_levels[i],
                                    tolerance_levels[i],
                                    image.getMaxColorValue(),
                          image.getWidth()* image.getHeight());

                if (threshold == -2){
                    System.out.println("Image " + (i + 1) + ":");
                    System.out.println(" -No suitable threshold found for the value: " + compression_levels[i]);
                    continue;
                }

                QuadTree qt = new QuadTree(image, threshold);
                PPMImage outImage = qt.render(outline_flag);

                int leafCount = qt.countLeaves();
                double achievedLevel = (double) leafCount / (double) (width * height);
                String achievedLevelString = String.format("%.5f", achievedLevel);

                System.out.println("Image " + (i + 1) + ":");
                System.out.println(" -Target compression level : " + compression_levels[i]);
                System.out.println(" -Leaf count               : " + leafCount);
                System.out.println(" -Pixel count               : " + width*height);
                System.out.println(" -Achieved compression lvl : " + achievedLevelString);
                System.out.println();

                String output_name = output_file + "-" + (i+1) + ".ppm";

                PPMAnalyzer.write_PPM(output_name, outImage);
            }
        }

        if (edge_flag){
            int defaultThreshold = 30;

            QuadTree qt = new QuadTree(image, defaultThreshold);
            PPMImage outImage = qt.edgeDetect(outline_flag);

            String output_name = output_file + ".ppm";

            PPMAnalyzer.write_PPM(output_name, outImage);
        }
    }

    public static double findThreshold(PPMImage image,
                                       double compressionLevel,
                                       double toleranceLevel,
                                       int MaxColorValue,
                                       int pixelCount) {
        // Lower-bound of threshold --> -1 (<0)
        double lower_bound = -1.0;

        // Upper-bound of threshold --> MaxColorValue^2 *3 --> Max value of the error
        double upper_bound = MaxColorValue * MaxColorValue * 3.0;

        // Binary search for threshold (30 iteration selected --> tunable)
        for (int i = 0; i < 30; i++) {
            double mid = (lower_bound + upper_bound) / 2.0;

            // Tree creation
            QuadTree qt = new QuadTree(image, mid);
            double compressionRatio = (double) qt.countLeaves() / (double) pixelCount;

            if (Math.abs(compressionRatio - compressionLevel) <= toleranceLevel) {
                return mid; // Suitable threshold is founded
            }

            // Binary search adjustment
            if (compressionRatio > compressionLevel) {
                lower_bound = mid; // Increase threshold
            } else {
                upper_bound = mid; // Decrease threshold
            }
        }

        return -2; // Suitable threshold is not found
    }


}
