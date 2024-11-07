package gutek.utils;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.InputStream;

/**
 * Utility class for creating icons.
 * This class provides methods to generate an ImageView containing an icon and to set its size.
 */
public class ImageUtil {

    /**
     * Private constructor to prevent instantiation of this utility class.
     * This ensures that the class is used in a static context only.
     */
    private ImageUtil(){}

    /**
     * Creates an ImageView with an image based on the given file path.
     * The method uses `createImage` to load the image file from the resources folder using the provided path.
     * If the image is not found, it returns an empty ImageView.
     *
     * @param imagePath the path to the image file, relative to the resources folder.
     * @return an ImageView containing the image, or an empty ImageView if the image is not found.
     */
    public static ImageView createImageView(String imagePath) {
        Image image = createImage(imagePath);

        ImageView imageView = new ImageView();
        if (image != null) {
            imageView.setImage(image);
        }

        return imageView;
    }

    /**
     * Creates an Image based on the given file path.
     * The method attempts to load an image from the resources folder using the provided path.
     * If the image is not found, it returns null.
     *
     * @param imagePath the path to the image file, relative to the resources folder.
     * @return an Image object, or null if the image is not found.
     */
    public static Image createImage(String imagePath) {
        InputStream imageStream = ImageUtil.class.getResourceAsStream(imagePath);
        if (imageStream != null) {
            return new Image(imageStream);
        }
        return null;
    }

    /**
     * Sets the width and height of the given ImageView to scale the image.
     * This method allows scaling the image dynamically based on the provided width and height.
     *
     * @param imageView the ImageView containing the image.
     * @param width the width to set for the image.
     * @param height the height to set for the image.
     */
    public static void setImageViewSize(ImageView imageView, double width, double height) {
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
    }
}
