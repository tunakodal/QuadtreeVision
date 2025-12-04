public class Main {
    public static void main(String[] args) {
        try {
            // 1. Input dosyasını oku
            PPMImage img = PPMAnalyzer.read_PPM("kira.ppm");

            // 2. Output olarak aynı görüntüyü yaz
            PPMAnalyzer.write_PPM("kira_copy.ppm", img);

            System.out.println("PPM read/write successful!");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
