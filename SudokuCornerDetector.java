import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.highgui.HighGui;
import org.opencv.core.Point;
import org.opencv.core.Mat;
import java.util.ArrayList;
import java.util.List;

public class SudokuCornerDetector {


    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Load the image
        String filePath = "\\Users\\811755\\OpenCV-JavaTest\\Sudoku3.jpg";
        Mat image = Imgcodecs.imread(filePath);

        if (image.empty()) {
            System.out.println("Could not open or find the image!");
            return;
        }

        // Convert to grayscale
        Mat gray = new Mat();
        Imgproc.cvtColor(image, gray, Imgproc.COLOR_BGR2GRAY);

        // Apply Gaussian Blur
        Imgproc.GaussianBlur(gray, gray, new Size(5, 5), 0);

        // Detect edges using Canny
        Mat edges = new Mat();
        Imgproc.Canny(gray, edges, 50, 150);

        // Find contours
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(edges, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        // Find the largest quadrilateral
        double maxArea = 0;
        MatOfPoint2f maxQuad = new MatOfPoint2f();
        for (MatOfPoint contour : contours) {
            MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
            double peri = Imgproc.arcLength(contour2f, true);
            MatOfPoint2f approx = new MatOfPoint2f();
            Imgproc.approxPolyDP(contour2f, approx, 0.02 * peri, true);

            if (approx.total() == 4) {
                double area = Imgproc.contourArea(approx);
                if (area > maxArea) {
                    maxArea = area;
                    maxQuad = approx;
                }
            }
        }

        if (maxQuad.total() != 4) {
            System.out.println("Could not find a quadrilateral in the image!");
            return;
        }

        // Convert MatOfPoint2f to List<Point>
        List<Point> corners = new ArrayList<>();
        for (int i = 0; i < maxQuad.total(); i++) {
            corners.add(new Point(maxQuad.get(i, 0)));
        }

        // Draw red dots on the corners
        for (Point corner : corners) {
            Imgproc.circle(image, corner, 10, new Scalar(0, 0, 255), -1);
        }

        // Display the image
        HighGui.imshow("Sudoku Corners", image);
        HighGui.waitKey();
        System.exit(0);
    }
}


