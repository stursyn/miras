package kz.satpaev.sunkar;

import javafx.application.Application;
import javafx.event.EventType;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import kz.satpaev.sunkar.controllers.SellOperationController;
import kz.satpaev.sunkar.controllers.keyboardfx.KeyboardView;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;

@SpringBootApplication
@PropertySource("classpath:application.properties")
public class Main extends Application {

  public static ApplicationContext applicationContext;

  @Override
  public void start(Stage stage) throws Exception {
    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("fxml/Home.fxml"));
    loader.setControllerFactory(applicationContext::getBean);

    Parent root = loader.load();

    stage.setTitle("Сұңқар");
    stage.getIcons().add(new Image("icons/icon.png"));
    stage.setMaximized(true);
    stage.setFullScreen(true);

    Scene scene = new Scene(root);
    SellOperationController sellOperationController = loader.getController();
    scene.addEventFilter(KeyEvent.KEY_PRESSED, sellOperationController::keyPressed);
    stage.setScene(scene);

    stage.show();
  }

  //you can download the glyph browser - link provided.
  public static void main(String[] args) {
    applicationContext = new AnnotationConfigApplicationContext(Main.class);

    launch(args);
  }
}
