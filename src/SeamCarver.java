import edu.princeton.cs.algs4.Picture;

public class SeamCarver {
    
    private Picture picture;
    
    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null) throw new IllegalArgumentException("constructor argument is null");
        // Initialize a new picture as instance variable that is a deep copy of the argument picture
        this.picture = new Picture(picture);
    }
    
    // current picture
    public Picture picture() {
        return new Picture(this.picture);
    }
    
    // width of current picture
    public int width() {
        return picture.width();
    }
    
    // height of current picture
    public int height() {
        return picture.height();
    }
    
    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validatePixel(x, y);
        if (atBorder(x, y)) 
            return 1000.00;
        return Math.sqrt(squareGradient(x - 1, y, x + 1, y) + squareGradient(x, y - 1, x, y + 1));
    }
    
    // helper functions
    
    private void validatePixel(int x, int y) {
        if (x < 0 || x >= width() || y < 0 || y >= height()) 
            throw new IllegalArgumentException("Pixel is outside its prescribed range");
    }
    
    // check whether the pixel is at the border
    private boolean atBorder(int x, int y) {
        if (x == 0 || x == width() - 1 || y == 0 || y == height() - 1) 
            return true;
        return false;
    }
    
    // calculate square gradient between pixel(x1, y1) and pixel(x2, y2)
    // which is the quare sum of differences in the red, green and blue components
    private int squareGradient(int x1, int y1, int x2, int y2) {
        // pixel1 rgb value
        int[] rgb1 = getRGB(x1, y1);
        // pixel2 rgb value
        int[] rgb2 = getRGB(x2, y2);
        int res = 0;
        for (int i = 0; i < 3; i++) 
            res += centralDiff(rgb1[i], rgb2[i]);
        return res;
    }
    
    // calculate square difference
    private int centralDiff(int c1, int c2) {
        return (c1 - c2) * (c1 - c2);
    }
    
    // get rgb value of a pixel
    private int[] getRGB(int x, int y) {
        int rgb = picture.getRGB(x, y);
        int r = (rgb>>16)&0XFF;
        int g = (rgb>>8)&0XFF;
        int b = (rgb>>0)&0XFF;
        return new int[] {r, g, b};
    }

    public static void main(String[] args) {

    }

}
