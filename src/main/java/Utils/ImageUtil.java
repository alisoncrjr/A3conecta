package Utils;

import javafx.scene.image.Image;

import java.io.InputStream;

public class ImageUtil {

    private static final String FALLBACK = "/icons/default_profile.png";


    public static Image loadProfileImage(String resourcePath) {
        try {
            String path = resourcePath;
            if (path == null || path.trim().isEmpty()) path = FALLBACK;

            InputStream is = ImageUtil.class.getResourceAsStream(path);
            if (is != null) {
                try {
                    Image img = new Image(is);
                    if (img != null && !img.isError()) return img;
                } catch (Exception ignored) {

                }
            }

            InputStream fallbackStream = ImageUtil.class.getResourceAsStream(FALLBACK);
            if (fallbackStream != null) return new Image(fallbackStream);


            return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII=");
        } catch (Exception e) {

            return new Image("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR4nGNgYAAAAAMAASsJTYQAAAAASUVORK5CYII=");
        }
    }
}
