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
        
    }
    
    // helper functions
    
    // calculate central difference between pixel(x1, y1) and pixel(x2, y2)
    // which is the quare sum of differences in the red, green and blue components
    private int gradient(int x1, int y1, int x2, int y2) {
        
    }

    public static void main(String[] args) {

    }

}
