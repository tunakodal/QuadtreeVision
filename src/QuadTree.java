public class QuadTree {

    private final QuadNode root;
    private final PPMImage image;

    //======Nested Node Class======
    public static class QuadNode{
        boolean isLeaf;
        int x, y, size; // Assuming square, thus height = width = size
        int r, g, b;
        QuadNode NW, NE, SW, SE;

        public QuadNode(int x, int y, int size, int r, int g, int b, boolean isLeaf) {
            this.x = x;
            this.y = y;
            this.size = size;
            this.r = r;
            this.g = g;
            this.b = b;
            this.isLeaf = isLeaf;
        }
    }
    //============

    //======Nested Stats Class======
    public static class Stats{
        int mean_r, mean_g, mean_b;
        double error;
    }

    private Stats computeStats(int[][][] pixels, int x, int y, int size){
        long sum_r = 0, sum_g = 0, sum_b = 0;
        int count = size*size; // total pixels

        //RGB summations
        for (int j = y; j < y + size; j++) {
            for (int i = x; i < x + size; i++) {
                sum_r += pixels[j][i][0];
                sum_g += pixels[j][i][1];
                sum_b += pixels[j][i][2];
            }
        }

        int mean_r = (int) (sum_r / count);
        int mean_g = (int) (sum_g / count);
        int mean_b = (int) (sum_b / count);

        //Error calculation
        double total_error = 0;
        for (int j = y; j < y + size; j++) {
            for (int i = x; i < x + size; i++) {
                int dr = pixels[y][x][0] - mean_r;
                int dg = pixels[y][x][1] - mean_g;
                int db = pixels[y][x][2] - mean_b;

                total_error += (dr*dr + dg*dg + db*db); // Normally (/count), post-division due to the casting problems,
            }
        }

        total_error = total_error / count;

        Stats stats = new Stats();
        stats.mean_r = mean_r;
        stats.mean_g = mean_g;
        stats.mean_b = mean_b;
        stats.error =total_error;

        return stats;

    }
    //============


    //--Constructor--
    public QuadTree(PPMImage image, double threshold){
       this.root = buildQuadTree(image.getPixels(), 0,0, image.getWidth(), threshold);
       this.image = image;
    }
    //============

    //--Methods--
    public QuadNode buildQuadTree(int[][][] pixels, int x, int y, int size, double threshold){
        Stats stats = computeStats(pixels, x, y, size);

        // If it is a single pixel or the error does not exceed the threshold (split) -- base cases
        if (size == 1 || stats.error <= threshold){
            return new QuadNode(x, y, size, stats.mean_r, stats.mean_g, stats.mean_b, true);
        }

        int half = size/2;
        QuadNode quadNode = new QuadNode(x, y, size, stats.mean_r, stats.mean_g, stats.mean_b, false);

        // Create the children nodes
        quadNode.NW = buildQuadTree(pixels, x, y, half,threshold);
        quadNode.SW = buildQuadTree(pixels, x, y + half, half,threshold);
        quadNode.NE = buildQuadTree(pixels, x + half, y, half,threshold);
        quadNode.SE = buildQuadTree(pixels, x + half, y + half, half,threshold);

        return quadNode;
    }

    public int countLeaves(){
       return countLeavesAux(root);
    }

    private int countLeavesAux(QuadNode quadNode){
        if (quadNode.isLeaf){
            return 1;
        }

        return countLeavesAux(quadNode.NW) + countLeavesAux(quadNode.SW) + countLeavesAux(quadNode.NE) + countLeavesAux(quadNode.SE);
    }

    public PPMImage render(boolean drawOutline){
        int width = image.getWidth();
        int height = image.getHeight();
        int maxColorValue = image.getMaxColorValue();

        PPMImage ppmImage = new PPMImage(width, height, maxColorValue);
        ppmImage.setPixels(compress(ppmImage.getPixels(),drawOutline));

        return ppmImage;
    }

    private int[][][] compress(int[][][] pixels, boolean drawOutline){
        return compressAux(root, pixels, drawOutline);
    }

    private int[][][] compressAux(QuadNode quadNode, int[][][] pixels, boolean drawOutline){
        if (quadNode.isLeaf){
            for (int j = quadNode.y; j < quadNode.y + quadNode.size; j++) {
                for (int i = quadNode.x; i < quadNode.x + quadNode.size; i++) {
                    pixels[j][i][0] = quadNode.r;
                    pixels[j][i][1] = quadNode.g;
                    pixels[j][i][2] = quadNode.b;
                }
            }
            if (drawOutline && quadNode.size > 1){
                drawBorder(quadNode, pixels);
            }
        }
        else {
            compressAux(quadNode.NW, pixels, drawOutline);
            compressAux(quadNode.NE, pixels, drawOutline);
            compressAux(quadNode.SW, pixels, drawOutline);
            compressAux(quadNode.SE, pixels, drawOutline);
        }
        return pixels;
    }

    private void drawBorder(QuadNode node, int[][][] pixels) {
        int x0 = node.x;
        int y0 = node.y;
        int x1 = node.x + node.size - 1;
        int y1 = node.y + node.size - 1;

        for (int x = x0; x <= x1; x++) {
            setPixel(pixels, x, y0, 0, 0, 0);
            setPixel(pixels, x, y1, 0, 0, 0);
        }
        for (int y = y0; y <= y1; y++) {
            setPixel(pixels, x0, y, 0, 0, 0);
            setPixel(pixels, x1, y, 0, 0, 0);
        }
    }

    private void setPixel(int[][][] pixels, int x, int y, int r, int g, int b) {
        pixels[y][x][0] = r;
        pixels[y][x][1] = g;
        pixels[y][x][2] = b;
    }
}
