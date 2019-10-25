import javafx.application.Application;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;
import javafx.event.*;
import javafx.scene.*;
import javafx.scene.input.*;
import java.awt.Color;
import java.util.*;
import javafx.scene.control.*;

public class Main extends Application {

    static int w;
    static int h;
    static String name;
    static String extension;
    static boolean scaleWithColor = false;
    static int amountOfPixelsMoved = 10;
    static int tolerance = 30;
    static int minTolerance = 0;
    static int lightTolerance = 200;
    static int darkTolerance = 30;
    static int contrastTolerance = 2;
    static boolean first = true;

    public void start(Stage stage) throws Exception { // jpg/jpeg files are not recommended due to compression!
    	try {
            Scanner scan = new Scanner(System.in);
            System.out.println("What is the file name?(with extension)");
            String tempstr = scan.nextLine();
            name = tempstr.substring(0, tempstr.indexOf("."));
            extension = tempstr.substring(tempstr.indexOf("."));
            try{
                BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Photos\\" + name + extension));
                w = imgBuf.getWidth();
                h = imgBuf.getHeight();
            }catch(Exception e){
                System.out.println("Invalid file");
                System.exit(1);
            }
            copyImage(name);
            Image image = new Image("\\Photos\\"  + name + extension);
            ImageView iv1 = new ImageView();
            iv1.setImage(image);
            iv1.setFitWidth(1000);
            iv1.setFitHeight(600);
            iv1.setPreserveRatio(true);
            iv1.setSmooth(true);
            iv1.setCache(true);

            iv1.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        System.out.println(mouseEvent.getSceneX() + "  " + mouseEvent.getY());
                    }
                }
            });

            Label label1 = new Label("Scale with color(y/n)"); 
            final ToggleGroup group = new ToggleGroup();
            RadioButton rb1 = new RadioButton("Use Smart Scaling");
            rb1.setUserData("true");
            rb1.setToggleGroup(group);
            rb1.setSelected(true);
            RadioButton rb2 = new RadioButton("Disable smart scaling");
            rb2.setUserData("");
            rb2.setToggleGroup(group);
            Label label2 = new Label("# of pixels moved"); 
            TextField pixels = new TextField();
            Label label3 = new Label("Min Tolerance");
            TextField minTol = new TextField(); 
            Label label4 = new Label("Tolerance");
            TextField tol = new TextField();

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

            Button contrast = new Button("Increase Contrast");
            contrast.setOnAction((event) -> {
                iv1.setImage(increaseContrast("new-" + name));
            });

            Button mystifyButton = new Button("Blue/Green");
            mystifyButton.setOnAction((event) -> {
                iv1.setImage(mystify("new-" + name));
            });

            Button pastelButton = new Button("Pastel color");
            pastelButton.setOnAction((event) -> {
                iv1.setImage(pastel("new-" + name));
            });

            Button set = new Button("Enter");
            set.setOnAction((event) -> {
                if(group.getSelectedToggle().getUserData().toString().equals("true"))
                    scaleWithColor = true;
                else{
                    scaleWithColor = false;   
                }
                try{ 
                    amountOfPixelsMoved = Integer.parseInt(pixels.getText());
                }catch(Exception e){
                    amountOfPixelsMoved = 0;
                    pixels.setText("Invalid value");
                }
                try{ 
                    minTolerance = Integer.parseInt(minTol.getText());               
                }catch(Exception e){
                    minTolerance = 0;
                    minTol.setText("Invalid value");
                }
                try{ 
                    tolerance = Integer.parseInt(tol.getText());               
                }catch(Exception e){
                    tolerance = 0;
                    tol.setText("Invalid value");
                }
                
            });

            Button resetImage = new Button("Reset Image");
            resetImage.setOnAction((event) -> {
                iv1.setImage(new Image("\\Photos\\" + name + extension));
                first = true;
                copyImage(name);
            });

            Button saveImage = new Button("Save Image");
            saveImage.setOnAction((event) -> {
                saveCurrentImage();

            });
            
            

            VBox options = new VBox(5);
            
            options.getChildren().add(rb1);
            options.getChildren().add(rb2);            
            options.getChildren().add(label1);
            options.getChildren().add(label2);
            options.getChildren().add(pixels);

            VBox tolBox = new VBox(15);
            HBox minTolBox = new HBox(10);
            minTolBox.getChildren().add(label3);
            minTolBox.getChildren().add(minTol);

            HBox tolBoxGroup = new HBox(10);
            tolBoxGroup.getChildren().add(label4);
            tolBoxGroup.getChildren().add(tol);

            tolBox.getChildren().add(minTolBox);
            tolBox.getChildren().add(tolBoxGroup);

            options.getChildren().add(tolBox);
            options.getChildren().add(set);
            options.getChildren().add(resetImage);
            options.getChildren().add(saveImage);

            HBox buttons = new HBox(3);
            buttons.getChildren().add(brighten);
            buttons.getChildren().add(darken);
            buttons.getChildren().add(pixelSort);
            buttons.getChildren().add(richDark);
            buttons.getChildren().add(richWhite);
            buttons.getChildren().add(contrast);
            buttons.getChildren().add(mystifyButton);
            buttons.getChildren().add(pastelButton);

            VBox vbox = new VBox(10);
            vbox.getChildren().add(buttons);
            vbox.getChildren().add(iv1);

            HBox full = new HBox(20);
            full.setPadding(new Insets(10, 50, 50, 50));
            full.getChildren().add(vbox);
            full.getChildren().add(options);

            Group root = new Group();
            root.getChildren().add(full);

            Scene scene = new Scene(root);
            // scene.setFill();

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
                    if ((int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() < tolerance && (int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() > minTolerance)
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
                    if (temp.getRed() > lightTolerance && temp.getGreen() > lightTolerance && temp.getRed() > lightTolerance) {
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
                    if (temp.getRed() < darkTolerance && temp.getGreen() < darkTolerance && temp.getBlue() < darkTolerance) {
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
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image increaseContrast(String imgName) {
        try {
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color newColor;
                    if(
                        temp.getRed() <= 255 - contrastTolerance && 
                        temp.getGreen() <= 255 - contrastTolerance && 
                        temp.getBlue() <= 255 - contrastTolerance &&
                        temp.getRed() >= 255/2 - contrastTolerance && 
                        temp.getGreen() >= 255/2 - contrastTolerance && 
                        temp.getBlue() >= 255/2 - contrastTolerance)
                            newColor = new Color(
                                (int) temp.getRed() + contrastTolerance, 
                                (int) temp.getGreen() + contrastTolerance, 
                                (int) temp.getBlue() + contrastTolerance);
                    else if (
                        temp.getRed() >= 0 + contrastTolerance && 
                        temp.getGreen() >= 0 + contrastTolerance && 
                        temp.getBlue() >= 0 + contrastTolerance)
                            newColor = new Color(
                                (int) temp.getRed() - contrastTolerance, 
                                (int) temp.getGreen() - contrastTolerance, 
                                (int) temp.getBlue() - contrastTolerance);
                    else{
                        newColor = new Color(
                            (int) temp.getRed(), 
                            (int) temp.getGreen(), 
                            (int) temp.getBlue());
                    }
                    img[i][x] = newColor.getRGB();
                    c++;
                }
            }
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
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image mystify(String imgName) {
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
                            (int) temp.getGreen() + 5 <= 255 ? (int) temp.getGreen() + 5 : (int) temp.getGreen(),
                            (int) temp.getBlue() + 5 <= 255 ? (int) temp.getBlue() + 5 : (int) temp.getBlue());
                    img[i][x] = bright.getRGB();
                    c++;
                }
            }
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Image pastel(String imgName) {
        try {
            BufferedImage imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright = new Color(
                            (int) temp.getRed() -5 >= 0 ? (int) temp.getRed() -5 : (int) temp.getRed(),
                            (int) temp.getGreen() - 10 >= 0 ? (int) temp.getGreen() - 10 : (int) temp.getGreen(),
                            (int) temp.getBlue() - 10 >= 0 ? (int) temp.getBlue() - 10 : (int) temp.getBlue());
                    img[i][x] = bright.getRGB();
                    c++;
                }
            }
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
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\new-" + oldFile + extension));
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
            ImageIO.write(bufferedImage, extension.substring(1), new File(System.getProperty("user.dir") + "\\Edited-Photos\\new-" + oldFile + extension));
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
            ImageIO.write(bufferedImage, extension.substring(1), new File(System.getProperty("user.dir") + "\\Edited-Photos\\new-" + name + extension));
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveCurrentImage() {
        try {
            System.out.println("Saved in\n" + new File(System.getProperty("user.dir") + "\\Saved-Photos" + "\nas\n"+ name + "(" + Long.toString(System.currentTimeMillis()/10000) + ")" + extension));
            ImageIO.write(copyImage(name), extension.substring(1), new File(System.getProperty("user.dir") + "\\Saved-Photos\\" + name + "(" + Long.toString(System.currentTimeMillis()/10000) + ")" + extension));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}