public class PPMImage {
    int width;
    int height;
    int maxColorValue;
    int[][][] pixels; // 3D array to hold RGB values

    public PPMImage(int width, int height, int maxColorValue) {
        this.width = width;
        this.height = height;
        this.maxColorValue = maxColorValue;
        this.pixels = new int[height][width][3]; // Initialize pixel array
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getMaxColorValue() {
        return maxColorValue;
    }

    public void setMaxColorValue(int maxColorValue) {
        this.maxColorValue = maxColorValue;
    }

    public int[][][] getPixels() {
        return pixels;
    }

    public void setPixels(int[][][] pixels) {
        this.pixels = pixels;
    }
}
