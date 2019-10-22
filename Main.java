import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.image.WritableImage;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.*;
import javax.imageio.ImageIO;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.MouseEvent;
import java.awt.Color;
import javafx.scene.image.PixelWriter;
import javafx.scene.control.Button;
import javafx.scene.input.MouseButton;
import java.util.*;

public class Main extends Application {

    static int w;
    static int h;
    static String name;
    static String extension;
    static boolean scaleWithColor = false;
    static int amountOfPixelsMoved = 10;
    static int tolerance = 30;
    static boolean first = true;

    public void start(Stage stage) throws Exception { // jpg/jpeg files are not recommended due to compression!
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("What is the file name?(with extension)");
            String tempstr = scan.nextLine();
            name = tempstr.substring(0, tempstr.indexOf("."));
            extension = tempstr.substring(tempstr.indexOf("."));
            System.out.println("Scale pixel sort with color?(y/n)");
            if (scan.nextLine().equals("y"))
                scaleWithColor = true;
            System.out.println("How many pixels moved per sort?");
            amountOfPixelsMoved = scan.nextInt();
            System.out.println("What tolerance do you want?");
            tolerance = scan.nextInt();

            // load the image
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Photos\\" + name + extension));
            w = imgBuf.getWidth();
            h = imgBuf.getHeight();
            copyImage(name);
            Image image = new Image("\\Edited-Photos\\" + "new-" + name + extension);
            ImageView iv1 = new ImageView();
            iv1.setImage(image);

            // ImageView iv2 = new ImageView();
            // iv2.setImage(image);
            // iv2.setFitWidth(100);
            // iv2.setPreserveRatio(true);
            // iv2.setSmooth(true);
            // iv2.setCache(true);

            // ImageView iv3 = new ImageView();
            // iv3.setImage(image);
            // Rectangle2D viewportRect = new Rectangle2D(40, 35, 110, 110);
            // iv3.setViewport(viewportRect);
            // iv3.setRotate(90);

            iv1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        System.out.println(mouseEvent.getSceneX() + "  " + mouseEvent.getY());
                    }
                }
            });

            Button brighten = new Button("Bright");
            brighten.setOnAction((event) -> {
                iv1.setImage(brightenImage("new-" + name));
            });

            Button darken = new Button("Dark");
            darken.setOnAction((event) -> {
                iv1.setImage(darkenImage("new-" + name));
            });

            Button pixelSort = new Button("Sort");
            pixelSort.setOnAction((event) -> {
                iv1.setImage(pixelSort("new-" + name));
            });

            Button richDark = new Button("Enhance Blacks");
            richDark.setOnAction((event) -> {
                iv1.setImage(RicherDarkColors("new-" + name));
            });

            Button richWhite = new Button("Enhance Whites");
            richWhite.setOnAction((event) -> {
                iv1.setImage(enhanceWhites("new-" + name));
            });

            Group root = new Group();
            Scene scene = new Scene(root);
            // scene.setFill();
            VBox vbox = new VBox();
            HBox box = new HBox();
            vbox.getChildren().add(box);
            vbox.getChildren().add(iv1);
            box.getChildren().add(brighten);
            box.getChildren().add(darken);
            box.getChildren().add(pixelSort);
            box.getChildren().add(richDark);
            box.getChildren().add(richWhite);
            // box.getChildren().add(iv2);
            // box.getChildren().add(iv3);
            root.getChildren().add(vbox);

            stage.setTitle("ImageView");
            stage.setWidth(415);
            stage.setHeight(200);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Application.launch(args);

    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }

    public static Image pixelSort(String imgName) {
        try {
            ArrayList<int[]> marked = new ArrayList<int[]>();
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    if ((int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() < tolerance)
                        marked.add(new int[] { i, x });
                    Color bright = new Color((int) temp.getRed(), (int) temp.getGreen(), (int) temp.getBlue());
                    img[i][x] = bright.getRGB();
                    c++;
                }
            }
            int amount = 0;
            for (int[] arr : marked) {
                Color temp;
                if (arr[0] != 0 && arr[1] != 0)
                    temp = new Color(img[arr[0] - 1][arr[1] - 1]);
                else
                    temp = new Color(img[arr[0]][arr[1]]);
                if (scaleWithColor)
                    amount = (temp.getRed() + temp.getGreen() + temp.getBlue()) / 6;
                else
                    amount = amountOfPixelsMoved;
                for (int i = 0; i < amount; i++) {
                    if (arr[1] < w && arr[0] + i < h) {
                        if (arr[0] != 0 && arr[1] != 0)
                            img[arr[0] + i - 1][arr[1] - 1] = temp.getRGB();
                    }
                }
            }
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image enhanceWhites(String imgName) {
        try {
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    if (temp.getRed() + temp.getGreen() + temp.getRed() > 500) {
                        Color bright;
                        if (temp.getRed() > 245 && temp.getGreen() < 245 && temp.getBlue() < 245)
                            bright = new Color(255, 255, 255);
                        else
                            bright = new Color(
                                    (int) temp.getRed() + 10 <= 255 ? (int) temp.getRed() + 10 : (int) temp.getRed(),
                                    (int) temp.getGreen() + 10 <= 255 ? (int) temp.getGreen() + 10: (int) temp.getGreen(),
                                    (int) temp.getBlue() + 10 <= 255 ? (int) temp.getBlue() + 10 : (int) temp.getBlue());

                        img[i][x] = bright.getRGB();
                    } else {
                        img[i][x] = RGBarray[c];
                    }
                    c++;
                }
            }
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image RicherDarkColors(String imgName) {
        try {
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    if (temp.getRed() + temp.getGreen() + temp.getRed() < 60) {
                        Color bright;
                        if (temp.getRed() < 10 && temp.getGreen() < 10 && temp.getBlue() < 10)
                            bright = new Color(0, 0, 0);
                        else
                            bright = new Color(
                                    (int) temp.getRed() - 10 >= 0 ? (int) temp.getRed() - 10 : (int) temp.getRed(),
                                    (int) temp.getGreen() - 10 >= 0 ? (int) temp.getGreen() - 10: (int) temp.getGreen(),
                                    (int) temp.getBlue() - 10 >= 0 ? (int) temp.getBlue() - 10 : (int) temp.getBlue());

                        img[i][x] = bright.getRGB();
                    } else {
                        img[i][x] = RGBarray[c];
                    }
                    c++;
                }
            }
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image brightenImage(String imgName) {
        try {
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright = new Color(
                            (int) temp.getRed() + 10 <= 255 ? (int) temp.getRed() + 10 : (int) temp.getRed(),
                            (int) temp.getGreen() + 10 <= 255 ? (int) temp.getGreen() + 10 : (int) temp.getGreen(),
                            (int) temp.getBlue() + 10 <= 255 ? (int) temp.getBlue() + 10 : (int) temp.getBlue());
                    img[i][x] = bright.getRGB();
                    c++;
                }
            }
            System.out.println(new Color(img[0][0]));
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image darkenImage(String imgName) {
        try {
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright = new Color(
                            (int) temp.getRed() - 10 >= 1 ? (int) temp.getRed() - 10 : (int) temp.getRed(),
                            (int) temp.getGreen() - 10 >= 1 ? (int) temp.getGreen() - 10 : (int) temp.getGreen(),
                            (int) temp.getBlue() - 10 >= 1 ? (int) temp.getBlue() - 10 : (int) temp.getBlue());
                    img[i][x] = bright.getRGB();
                    c++;
                }
            }
            System.out.println(new Color(img[0][0]));
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage copyImage(String oldFile) {
        try {
            BufferedImage imgBuf;
            if(first){
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Photos\\" + oldFile + extension));
                first = false;
            }
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + oldFile + extension));
            BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;
            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    img[i][x] = RGBarray[c];
                    c++;
                }
            }
            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {

                    bufferedImage.setRGB(col, row, img[row][col]);
                }
            }
            ImageIO.write(bufferedImage, "jpg", new File(System.getProperty("user.dir") + "\\Edited-Photos\\" +"new-" + oldFile + extension));
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static BufferedImage convertAndSaveImage(int[][] img) {
        try {
            BufferedImage bufferedImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);

            for (int row = 0; row < h; row++) {
                for (int col = 0; col < w; col++) {

                    bufferedImage.setRGB(col, row, img[row][col]);
                }
            }
            ImageIO.write(bufferedImage, "jpg", new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + "new-" + name + extension));
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}