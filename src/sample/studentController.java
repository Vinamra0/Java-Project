package sample;


import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class studentController implements Initializable {
    public static String tstudentName;
    public static String tstudentRoll;
    public static String tstudentSex;
    public static String tstudentCategory;
    public static String tstudentFName;
    public static String tstudentMName;
    public static int tstudentmM;
    public static int tstudentpM;
    public static int tstudentcM;
    public Button backBtn;
    public Label studentN;
    public Label studentR;
    public Label studentS;
    public Label studentF;
    public Label studentM;
    public Label marksM;
    public Label marksC;
    public Label marksP;
    public Label studentC;

    public void initialize(URL url, ResourceBundle resourceBundle) {


        studentN.setText(tstudentName);
        studentR.setText(tstudentRoll);
        studentS.setText(tstudentSex);
        studentC.setText(tstudentCategory);
        studentF.setText(tstudentFName);
        studentM.setText(tstudentMName);
        marksM.setText(String.valueOf(tstudentmM));
        marksP.setText(String.valueOf(tstudentpM));
        marksC.setText(String.valueOf(tstudentcM));
    }
    public void backBtnClick(javafx.event.ActionEvent actionEvent) throws IOException {


        Stage curStage = (Stage) backBtn.getScene().getWindow();
        curStage.close();

        Stage stage = new Stage();

        Parent login = FXMLLoader.load(getClass().getResource("login.fxml"));

        Scene scene = new Scene(login, Main.height, Main.width);

        stage.setTitle("Student Management System");
        stage.setScene(scene);
        stage.show();
    }
}
