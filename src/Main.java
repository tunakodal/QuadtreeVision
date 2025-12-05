public class Main {
    static double[] compression_levels = {0.002, 0.004, 0.01, 0.033, 0.077, 0.2, 0.5, 0.65};
    static double[] tolerance_levels = {0.0005, 0.0007, 0.0015, 0.002, 0.01, 0.015, 0.02, 0.03};

    public static void main(String[] args) {

    }

    public static Double findThreshold(PPMImage image,
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

        return null; // Suitable threshold is not found
    }


}
