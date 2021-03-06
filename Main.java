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

    static int w, h, selectedW, selectedH;
    static String name, extension;
    static boolean scaleWithColor = false;
    static int amountOfPixelsMoved = 10;
    static int minTolerance = 0, tolerance = 30;
    static int lightTolerance = 200, darkTolerance = 30;
    static int contrastTolerance = 2;
    static boolean first = true;
    static boolean selecting = false, selected = false;
    static int selectingCount = 0;
    static TextField selection1, selection2;
    static int x1, x2, y1, y2;
    static double aspectRatio;
    static double realWidth;
    static double realHeight;
    public void start(Stage stage) throws Exception { // jpg/jpeg files are not recommended due to compression!
        try {
            Scanner scan = new Scanner(System.in);
            System.out.println("What is the file name?(with extension)");
            String tempstr = scan.nextLine();
            name = tempstr.substring(0, tempstr.indexOf("."));
            extension = tempstr.substring(tempstr.indexOf("."));
            // RandomAccessFile RAF1 = new RandomAccessFile(new File(System.getProperty("user.dir") + "\\Photos\\" + name + extension), "rw");
            // RandomAccessFile RAF2 = new RandomAccessFile(new File(System.getProperty("user.dir") + "\\Photos\\" + "maleBW.jpg"), "rw");
            // RandomAccessFile RAF = new RandomAccessFile(new File(System.getProperty("user.dir") + "\\Photos\\" + "new test" + extension), "rw");
            // for(int i = 0; i < 200; i++){
            //     RAF1.readLine().getBytes();
            //     RAF2.readLine().getBytes();
            // }
            // RAF.write(RAF1.readLine().getBytes());
            // RAF.write(RAF2.readLine().getBytes());

            try {
                BufferedImage imgBuf;
                if (System.getProperty("os.name").indexOf("Mac") != -1)
                    imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Photos/" + name + extension));
                else
                    imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Photos\\" + name + extension));
                w = imgBuf.getWidth();
                h = imgBuf.getHeight();
            } catch (Exception e) {
                System.out.println("Invalid file");
                System.exit(1);
            }
            copyImage(name);
            Image image;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                image = new Image("/Photos/" + name + extension);
            else
                image = new Image("\\Photos\\" + name + extension);
            ImageView iv1 = new ImageView();
            iv1.setImage(image);
            iv1.setFitWidth(1000);
            iv1.setFitHeight(600);
            iv1.setPreserveRatio(true);
            iv1.setSmooth(true);
            iv1.setCache(true);

            aspectRatio = image.getWidth() / image.getHeight();
            realWidth = Math.min(iv1.getFitWidth(), iv1.getFitHeight() * aspectRatio);
            realHeight = Math.min(iv1.getFitHeight(), iv1.getFitWidth() / aspectRatio);

            Label label1 = new Label("Scale with color(y/n)");
            ToggleGroup group = new ToggleGroup();
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

            selection1 = new TextField();
            selection2 = new TextField();

            iv1.setOnMouseClicked(new EventHandler < MouseEvent > () {
                @Override
                public void handle(MouseEvent mouseEvent) {
                    if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
                        if (selecting == true && selectingCount == 0) {
                            selection1.setText((int) mouseEvent.getSceneX() + "  " + (int) mouseEvent.getY());
                            selectingCount++;
                        } else if (selecting == true && selectingCount == 1) {
                            selection2.setText((int) mouseEvent.getSceneX() + "  " + (int) mouseEvent.getY());
                            selecting = false;
                            selectingCount = 0;
                            setSelectionHelper();
                        }
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

            Button contrast = new Button("Increase Contrast");
            contrast.setOnAction((event) -> {
                iv1.setImage(increaseContrast("new-" + name));
            });

            Button decContrast = new Button("Decrease Contrast");
            decContrast.setOnAction((event) -> {
                iv1.setImage(decreaseContrast("new-" + name));
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
                if (group.getSelectedToggle().getUserData().toString().equals("true"))
                    scaleWithColor = true;
                else {
                    scaleWithColor = false;
                }
                try {
                    amountOfPixelsMoved = Integer.parseInt(pixels.getText());
                } catch (Exception e) {
                    amountOfPixelsMoved = 0;
                    pixels.setText("Invalid value");
                }
                try {
                    minTolerance = Integer.parseInt(minTol.getText());
                } catch (Exception e) {
                    minTolerance = 0;
                    minTol.setText("Invalid value");
                }
                try {
                    tolerance = Integer.parseInt(tol.getText());
                } catch (Exception e) {
                    tolerance = 0;
                    tol.setText("Invalid value");
                }

            });

            Button resetImage = new Button("Reset Image");
            resetImage.setOnAction((event) -> {
                if (System.getProperty("os.name").indexOf("Mac") != -1)
                    iv1.setImage(new Image("/Photos/" + name + extension));
                else
                    iv1.setImage(new Image("\\Photos\\" + name + extension));
                first = true;
                copyImage(name);
            });

            Button saveImage = new Button("Save Image");
            saveImage.setOnAction((event) -> {
                saveCurrentImage();

            });

            Button setSelection = new Button("Set Selection");
            setSelection.setOnAction((event) -> {
                setSelection();
            });

            Button resetSelection = new Button("Reset Selection");
            resetSelection.setOnAction((event) -> {
                resetSelection();
            });

            Button randomColors = new Button("Random Colors");
            randomColors.setOnAction((event) -> {
                iv1.setImage(randomColors("new-" + name));
            });


            VBox options = new VBox(5);

            options.getChildren().add(rb1);
            options.getChildren().add(rb2);
            options.getChildren().add(label1);
            options.getChildren().add(label2);
            options.getChildren().add(pixels);

            HBox minTolBox = new HBox(10);
            minTolBox.getChildren().add(label3);
            minTolBox.getChildren().add(minTol);

            HBox tolBox = new HBox(10);
            tolBox.getChildren().add(label4);
            tolBox.getChildren().add(tol);

            options.getChildren().add(minTolBox);
            options.getChildren().add(tolBox);

            options.getChildren().add(set);
            options.getChildren().add(selection1);
            options.getChildren().add(selection2);
            options.getChildren().add(setSelection);
            options.getChildren().add(resetSelection);
            options.getChildren().add(resetImage);
            options.getChildren().add(saveImage);

            HBox buttons = new HBox(3);
            buttons.getChildren().add(brighten);
            buttons.getChildren().add(darken);
            buttons.getChildren().add(pixelSort);
            buttons.getChildren().add(richDark);
            buttons.getChildren().add(richWhite);
            buttons.getChildren().add(contrast);
            buttons.getChildren().add(decContrast);
            buttons.getChildren().add(mystifyButton);
            buttons.getChildren().add(pastelButton);
            buttons.getChildren().add(randomColors);

            VBox vbox = new VBox(10);
            vbox.getChildren().add(buttons);
            vbox.getChildren().add(iv1);

            HBox full = new HBox(20);
            full.setPadding(new Insets(10, 10, 10, 10));
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

   

    private static void setSelectionHelper() {
        x1 = Integer.parseInt(selection1.getText().substring(0, selection1.getText().indexOf(" ")));
        y1 = Integer.parseInt(selection1.getText().substring(selection1.getText().indexOf(" ") + 2));
        x2 = Integer.parseInt(selection2.getText().substring(0, selection2.getText().indexOf(" ")));
        y2 = Integer.parseInt(selection2.getText().substring(selection2.getText().indexOf(" ") + 2));

        System.out.println(w + " " + h);
        System.out.println(realWidth + " " + realHeight);
        
        int temp;
        if(x1 > x2 && y1 > y2){
            temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        selectedW = x2 - x1;
        selectedH = y2 - y1;
        selected = true;
    }

    public static void resetSelection() {
        selection1.setText("No Selection");
        selection2.setText("No Selection");
        selected = false;
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
            ArrayList < int[] > marked = new ArrayList < int[] > ();
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];

            int c = 0;
            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    if (
                        selected && 
                        x >= x1 && x <= x2 && 
                        i >= y1 && i <= y2 && 
                        (int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() < tolerance && 
                        (int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() > minTolerance){
                            marked.add(new int[] {i,x});
                            System.out.println(i + " " + x);
                        }
                    else if (
                        !selected &&
                        (int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() < tolerance &&
                        (int) temp.getRed() + (int) temp.getGreen() + (int) temp.getBlue() > minTolerance)
                            marked.add(new int[] {i,x});
                    img[i][x] = temp.getRGB();
                    c++;
                }
            }
            int amount = 0;
            for (int[] arr: marked) {
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

    public static Image randomColors(String imgName){
        try {
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright;
                    if(selected && 
                        x >= x1 && x <= x2 && 
                        i >= y1 && i <= y2){
                        int temp1 = (int)(Math.random()*temp.getRed());
                        int temp2 = (int)(Math.random()*temp.getGreen());
                        int temp3 = (int)(Math.random()*temp.getBlue());
                        int desc = (int)Math.round(Math.random());
                        int brightness = temp.getRed() + temp.getGreen() + temp.getBlue();
                            int newRed = (int)(Math.random() * 255);
                            int newGreen = (int)(Math.random() * 255);
                            int newBlue = (int)(Math.random() * 255);
                            for(int t = 0; newRed + newGreen + newBlue > brightness && newRed < 255 && newGreen < 255 && newBlue < 255;t++){
                                if(newGreen > 255 || newBlue > 255 || newRed > 255){
                                    newGreen--;
                                    newBlue--;
                                    newRed--;
                                }

                                if(t % 3 == 0 && newRed > 0)
                                    newRed--;
                                else if(t % 2 == 0 && newGreen > 0)
                                    newGreen--;
                                else if(newBlue > 0)
                                    newBlue--;
                            }
                            bright = new Color(
                                newRed,
                                newGreen,
                                newBlue);
                        }
                        else if(!selected){
                            int brightness = temp.getRed() + temp.getGreen() + temp.getBlue();
                            int newRed = (int)(Math.random() * 255);
                            int newGreen = (int)(Math.random() * 255);
                            int newBlue = (int)(Math.random() * 255);
                            for(int t = 0; newRed + newGreen + newBlue > brightness && newRed < 255 && newGreen < 255 && newBlue < 255;t++){
                                if(newGreen > 255 || newBlue > 255 || newRed > 255){
                                    newGreen--;
                                    newBlue--;
                                    newRed--;
                                }

                                if(t % 3 == 0 && newRed > 0)
                                    newRed--;
                                else if(t % 2 == 0 && newGreen > 0)
                                    newGreen--;
                                else if(newBlue > 0)
                                    newBlue--;
                            }
                            bright = new Color(
                                newRed,
                                newGreen,
                                newBlue);
                        }
                       
                        else{
                            bright = temp;
                        }
                        img[i][x] = bright.getRGB();
                    c++;
                }
            }
            System.out.println("done");
            return convertToFxImage(convertAndSaveImage(img));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setSelection() {
        selecting = true;
        selectingCount = 0;
    }

    public static Image enhanceWhites(String imgName) {
        try {
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    if (
                        (selected && 
                        i >= x1 && i <= x2 && 
                        x >= y1 && x <= y2 && 
                        temp.getRed() > lightTolerance && 
                        temp.getGreen() > lightTolerance && 
                        temp.getRed() > lightTolerance) || 
                        (!selected &&
                        temp.getRed() > lightTolerance && 
                        temp.getGreen() > lightTolerance && temp.getRed() > lightTolerance)){
                            Color bright;
                            if (temp.getRed() > 245 && temp.getGreen() < 245 && temp.getBlue() < 245)
                                bright = new Color(255, 255, 255);
                            else
                                bright = new Color(
                                    (int) temp.getRed() + 10 <= 255 ? (int) temp.getRed() + 10 : (int) temp.getRed(),
                                    (int) temp.getGreen() + 10 <= 255 ? (int) temp.getGreen() + 10 : (int) temp.getGreen(),
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
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
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
                                (int) temp.getGreen() - 10 >= 0 ? (int) temp.getGreen() - 10 : (int) temp.getGreen(),
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
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright;
                    if(selected && 
                        x >= x1 && x <= x2 && 
                        i >= y1 && i <= y2)
                        bright = new Color(
                            (int) temp.getRed() + 10 <= 255 ? (int) temp.getRed() + 10 : (int) temp.getRed(),
                            (int) temp.getGreen() + 10 <= 255 ? (int) temp.getGreen() + 10 : (int) temp.getGreen(),
                            (int) temp.getBlue() + 10 <= 255 ? (int) temp.getBlue() + 10 : (int) temp.getBlue());
                        else if(!selected)
                            bright = new Color(
                                (int) temp.getRed() + 10 <= 255 ? (int) temp.getRed() + 10 : (int) temp.getRed(),
                                (int) temp.getGreen() + 10 <= 255 ? (int) temp.getGreen() + 10 : (int) temp.getGreen(),
                                (int) temp.getBlue() + 10 <= 255 ? (int) temp.getBlue() + 10 : (int) temp.getBlue());
                        else
                            bright = temp;
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
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color newColor;
                    if (
                        temp.getRed() <= 255 - contrastTolerance &&
                        temp.getGreen() <= 255 - contrastTolerance &&
                        temp.getBlue() <= 255 - contrastTolerance &&
                        temp.getRed() >= 255 / 2 - contrastTolerance &&
                        temp.getGreen() >= 255 / 2 - contrastTolerance &&
                        temp.getBlue() >= 255 / 2 - contrastTolerance)
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
                    else {
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

    public static Image decreaseContrast(String imgName) {
        try {
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color newColor;
                    if (
                        temp.getRed() <= 255 &&
                        temp.getGreen() <= 255 &&
                        temp.getBlue() <= 255 &&
                        temp.getRed() >= 255 / 2 &&
                        temp.getGreen() >= 255 / 2 &&
                        temp.getBlue() >= 255 / 2)
                            newColor = new Color(
                                (int) temp.getRed() - contrastTolerance,
                                (int) temp.getGreen() - contrastTolerance,
                                (int) temp.getBlue() - contrastTolerance);
                    else if (
                        temp.getRed() >= 0 &&
                        temp.getGreen() >= 0 &&
                        temp.getBlue() >= 0 &&
                        temp.getRed() <= 255 - contrastTolerance &&
                        temp.getGreen() <= 255 - contrastTolerance &&
                        temp.getBlue() <= 255 - contrastTolerance)
                            newColor = new Color(
                                (int) temp.getRed() + contrastTolerance,
                                (int) temp.getGreen() + contrastTolerance,
                                (int) temp.getBlue() + contrastTolerance);
                    else {
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
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
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
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright;
                    if (
                        temp.getRed() - 5 >= 0 &&
                        temp.getGreen() + 5 <= 255 &&
                        temp.getBlue() + 5 <= 255)
                        bright = new Color(
                            (int) temp.getRed() - 5,
                            (int) temp.getGreen() + 5,
                            (int) temp.getBlue() + 5);
                    else
                        bright = new Color(
                            (int) temp.getRed(),
                            (int) temp.getGreen(),
                            (int) temp.getBlue());
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
            BufferedImage imgBuf;
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/" + imgName + extension));
            else
                imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\" + imgName + extension));
            int[] RGBarray = imgBuf.getRGB(0, 0, w, h, null, 0, w);
            int[][] img = new int[h][w];
            int c = 0;

            for (int i = 0; i < h; i++) {
                for (int x = 0; x < w; x++) {
                    Color temp = new Color(RGBarray[c]);
                    Color bright;
                    if (
                        temp.getRed() + 10 <= 255 &&
                        temp.getGreen() - 5 >= 0 &&
                        temp.getBlue() - 10 >= 0)
                            bright = new Color(
                                (int) temp.getRed() + 5,
                                (int) temp.getGreen() - 1,
                                (int) temp.getBlue() - 5);
                    else
                        bright = temp;
                    // if (
                    //     temp.getRed() >= 255 / 2 &&
                    //     temp.getGreen() >= 255 / 2 &&
                    //     temp.getBlue() >= 255 / 2)
                    //         bright = new Color(
                    //             (int) temp.getRed() - contrastTolerance,
                    //             (int) temp.getGreen() - contrastTolerance,
                    //             (int) temp.getBlue() - contrastTolerance);
                    // else if (
                    //     temp.getRed() >= 0 + contrastTolerance &&
                    //     temp.getGreen() >= 0 + contrastTolerance &&
                    //     temp.getBlue() >= 0 + contrastTolerance &&
                    //     temp.getRed() <= 255 / 2 &&
                    //     temp.getGreen() <= 255 / 2 &&
                    //     temp.getBlue() <= 255 / 2)
                    //         bright = new Color(
                    //             (int) temp.getRed() + contrastTolerance,
                    //             (int) temp.getGreen() + contrastTolerance,
                    //             (int) temp.getBlue() + contrastTolerance);
                    // else {
                    //     bright = new Color(
                    //         (int) temp.getRed(),
                    //         (int) temp.getGreen(),
                    //         (int) temp.getBlue());
                    //}
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
            if (first) {
                if (System.getProperty("os.name").indexOf("Mac") != -1)
                    imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Photos/" + oldFile + extension));
                else
                    imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Photos\\" + oldFile + extension));
                first = false;
            } else {
                if (System.getProperty("os.name").indexOf("Mac") != -1)
                    imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "/Edited-Photos/new-" + oldFile + extension));
                else
                    imgBuf = ImageIO.read(new File(System.getProperty("user.dir") + "\\Edited-Photos\\new-" + oldFile + extension));
            }
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
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                ImageIO.write(bufferedImage, extension.substring(1), new File(System.getProperty("user.dir") + "/Edited-Photos/new-" + oldFile + extension));
            else
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
            if (System.getProperty("os.name").indexOf("Mac") != -1)
                ImageIO.write(bufferedImage, extension.substring(1), new File(System.getProperty("user.dir") + "/Edited-Photos/new-" + name + extension));
            else
                ImageIO.write(bufferedImage, extension.substring(1), new File(System.getProperty("user.dir") + "\\Edited-Photos\\new-" + name + extension));
            return bufferedImage;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void saveCurrentImage() {
        try {
            if (System.getProperty("os.name").indexOf("Mac") != -1) {
                System.out.println("Saved in\n" + new File(System.getProperty("user.dir") + "/Saved-Photos" + "\nas\n" + name + "(" + Long.toString(System.currentTimeMillis() / 10000) + ")" + extension));
                ImageIO.write(copyImage(name), extension.substring(1), new File(System.getProperty("user.dir") + "/Saved-Photos/" + name + "(" + Long.toString(System.currentTimeMillis() / 10000000) + ")" + extension));
            } else {
                System.out.println("Saved in\n" + new File(System.getProperty("user.dir") + "\\Saved-Photos" + "\nas\n" + name + "(" + Long.toString(System.currentTimeMillis() / 10000) + ")" + extension));
                ImageIO.write(copyImage(name), extension.substring(1), new File(System.getProperty("user.dir") + "\\Saved-Photos\\" + name + "(" + Long.toString(System.currentTimeMillis() / 10000000) + ")" + extension));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}