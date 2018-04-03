import java.lang.reflect.Array;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.StdOut;

public class SeamCarver {

    private Integer[][] rgbMatrix;
    private Double[][] energyMatrix;
    private boolean isVertical;

    // create a seam carver object based on the given picture
    public SeamCarver(Picture picture) {
        if (picture == null)
            throw new IllegalArgumentException("constructor argument is null");
        int width = picture.width();
        int height = picture.height();
        // initialize rgb matrix first
        rgbMatrix = new Integer[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                rgbMatrix[col][row] = picture.getRGB(col, row);
            }
        }
        // then initialize energy matrix
        energyMatrix = new Double[width][height];
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                energyMatrix[col][row] = energy(col, row);
            }
        }
        isVertical = true;
    }

    // current picture
    public Picture picture() {
        Picture picture = new Picture(width(), height());
        for (int col = 0; col < width(); col++) {
            for (int row = 0; row < height(); row++) {
                picture.setRGB(col, row, rgbMatrix[col][row]);
            }
        }
        return picture;
    }

    // width of current picture
    public int width() {
        return energyMatrix.length;
    }

    // height of current picture
    public int height() {
        return energyMatrix[0].length;
    }

    // energy of pixel at column x and row y
    public double energy(int x, int y) {
        validatePixel(x, y);
        if (atBorder(x, y))
            return 1000.00;
        return Math.sqrt(squareGradient(x - 1, y, x + 1, y) + squareGradient(x, y - 1, x, y + 1));
    }

    // sequence of indices for horizontal seam
    public int[] findHorizontalSeam() {
        if (isVertical) {
            this.energyMatrix = toHorizontal(this.energyMatrix);
            this.rgbMatrix = toHorizontal(this.rgbMatrix);
            this.isVertical = false;
        }

        int[] res = findSeam();

        // transpose it back
        this.energyMatrix = toVertical(this.energyMatrix);
        this.rgbMatrix = toVertical(this.rgbMatrix);
        this.isVertical = true;

        return res;
    }

    // sequence of indices for vertical seam
    public int[] findVerticalSeam() {
        if (!isVertical) {
            this.energyMatrix = (Double[][]) toVertical(this.energyMatrix);
            this.rgbMatrix = (Integer[][]) toVertical(this.rgbMatrix);
            this.isVertical = true;
        }

        return findSeam();
    }

    // remove horizontal seam from current picture
    public void removeHorizontalSeam(int[] seam) {
        validateSeam(seam, false);
        this.rgbMatrix = removeHSeamHelper(seam, rgbMatrix);
        this.energyMatrix = removeHSeamHelper(seam, energyMatrix);
        updateEnergy(seam);
    }

    // remove vertical seam from current picture
    public void removeVerticalSeam(int[] seam) {
        validateSeam(seam, true);

        // transpose the picture to horizontal
        // then the seam will be horizontal
        // use removeHorizontalSeam and transpose it back
        this.energyMatrix = toHorizontal(this.energyMatrix);
        this.rgbMatrix = toHorizontal(this.rgbMatrix);
        this.isVertical = false;

        removeHorizontalSeam(seam);

        // transpose it back
        this.energyMatrix = toVertical(this.energyMatrix);
        this.rgbMatrix = toVertical(this.rgbMatrix);
        this.isVertical = true;
    }

    // helper functions

    // update Energy matrix alone the seam (treat the seam as horizontal seam)
    private void updateEnergy(int[] seam) {
        for (int col = 0; col < width(); col++) {
            int seamPos = seam[col];
            energyMatrix[col][seamPos] = energy(col, seamPos);
            if (seamPos - 1 >= 0) {
                energyMatrix[col][seamPos - 1] = energy(col, seamPos - 1);
            }
        }
    }

    // remove (default) horizontal seam helper
    private <T> T[][] removeHSeamHelper(int[] seam, T[][] matrix) {
        int width = matrix.length;
        int height = matrix[0].length;
        T[][] res = (T[][]) Array.newInstance(matrix[0][0].getClass(), width, height - 1);
        for (int col = 0; col < width; col++) {
            // copy first part before seam
            System.arraycopy(matrix[col], 0, res[col], 0, seam[col]);
            // copy second part after seam
            System.arraycopy(matrix[col], seam[col] + 1, res[col], seam[col], height - seam[col] - 1);
        }
        return res;
    }

    private void validateSeam(int[] seam, boolean isVaticalSeam) {
        if (seam == null)
            throw new IllegalArgumentException("argument is null");
        if (isVaticalSeam) {
            // vatical seam validate
            if (seam.length != height())
                throw new IllegalArgumentException("removeVerticalSeam() is called with an array of the wrong length");
            if (width() <= 1)
                throw new IllegalArgumentException(
                        "removeVerticalSeam() is called when the width of the picture is less than or equal to 1");
        } else {
            // horizontal seam validate
            if (seam.length != width())
                throw new IllegalArgumentException(
                        "removeHorizontalSeam() is called with an array of the wrong length");
            if (height() <= 1)
                throw new IllegalArgumentException(
                        "removeHorizontalSeam() is called when the height of the picture is less than or equal to 1");
        }
    }

    // find vertical seam regardless of picture orientation
    private int[] findSeam() {
        int width = width();
        int height = height();
        double[][] distTo = new double[width][height];
        int[][] edgeTo = new int[width][height];
        // initialize above matrixes
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {

                if (row == 0) {
                    // first line, sources
                    distTo[col][row] = energyMatrix[col][row];
                } else {
                    distTo[col][row] = Double.POSITIVE_INFINITY;
                }

                edgeTo[col][row] = -1;
            }
        }

        // topological order
        for (int y = 0; y < height - 1; y++) {
            for (int x = 0; x < width; x++) {
                // relax each edge (x, y)
                for (int i = -1; i <= 1; i++) {
                    if (x + i < 0 || x + i >= width)
                        continue;
                    else if (distTo[x + i][y + 1] > distTo[x][y] + energyMatrix[x + i][y + 1]) {
                        // update
                        distTo[x + i][y + 1] = distTo[x][y] + energyMatrix[x + i][y + 1];
                        edgeTo[x + i][y + 1] = x;
                    }
                }
            }
        }

        // find seam endpoint
        int end = 0;
        for (int x = 1; x < width; x++) {
            if (distTo[x][height - 1] < distTo[end][height - 1]) {
                end = x;
            }
        }

        // build return array
        int[] res = new int[height];
        res[height - 1] = end;
        for (int i = height - 2; i >= 0; i--) {
            res[i] = edgeTo[end][i + 1];
            end = res[i];
        }

        return res;
    }

    // horizontal transpose
    private <T> T[][] toHorizontal(T[][] arr) {
        // phase 2: diagonal exchange
        T[][] diagonal = diagonal(arr);

        // phase 1: up and down exchange
        T[][] upDown = upDown(diagonal);

        return upDown;
    }

    // vertical transpose
    private <T> T[][] toVertical(T[][] arr) {
        // phase 1: diagonal exchange
        T[][] diagonal = diagonal(arr);

        // phase 1: left and right exchange
        T[][] leftRight = leftRight(diagonal);

        return leftRight;
    }

    private <T> T[][] upDown(T[][] arr) {
        int width = arr.length;
        int height = arr[0].length;

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height / 2; row++) {
                // exchange [col, row] with [col, height - row - 1]
                T temp = arr[col][row];
                arr[col][row] = arr[col][height - row - 1];
                arr[col][height - row - 1] = temp;
            }
        }

        return arr;
    }

    private <T> T[][] leftRight(T[][] arr) {
        int width = arr.length;

        for (int col = 0; col < width / 2; col++) {
            // exchange left and right
            T[] temp = arr[col];
            arr[col] = arr[width - col - 1];
            arr[width - col - 1] = temp;
        }

        return arr;
    }

    private <T> T[][] diagonal(T[][] arr) {
        int width = arr.length;
        int height = arr[0].length;

        // create a generate array that is T type (not Object type)
        T[][] res = (T[][]) Array.newInstance(arr[0][0].getClass(), height, width);

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                // exchange diagnonal
                res[row][col] = arr[col][row];
            }
        }
        return res;
    }

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
        int rgb = rgbMatrix[x][y];
        int r = (rgb >> 16) & 0XFF;
        int g = (rgb >> 8) & 0XFF;
        int b = (rgb >> 0) & 0XFF;
        return new int[] { r, g, b };
    }

    // test method
    // print energy matrix
    private void printEnergyMatrix() {
        for (int row = 0; row < height(); row++) {
            for (int col = 0; col < width(); col++) {
                double energy = this.energyMatrix[col][row];
                StdOut.printf("%7.2f ", energy);
            }
            StdOut.println();
        }
        StdOut.println();
        StdOut.println();
    }

    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        SeamCarver carver = new SeamCarver(picture);

        // int[] verticalSeam = carver.findVerticalSeam();

        carver.printEnergyMatrix();

        System.out.println();
        System.out.println();

        int[] horizontalSeam = carver.findHorizontalSeam();

        carver.printEnergyMatrix();

        for (int x = 0; x < horizontalSeam.length; x++) {
            System.out.print(horizontalSeam[x] + " ");
        }
        System.out.println();

        carver.removeHorizontalSeam(horizontalSeam);

        System.out.println();
        System.out.println();

        carver.printEnergyMatrix();
    }

}
