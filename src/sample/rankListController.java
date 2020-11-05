package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import tableModels.meritListTableModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class rankListController implements Initializable {

    public Button backBtn;
    @FXML
    private TableView<meritListTableModel> meritListTableView;

    @FXML
    public TableColumn<meritListTableModel,String> rank;

    @FXML
    public TableColumn<meritListTableModel,String> tableRoll;

    @FXML
    public TableColumn<meritListTableModel,String> tableName;

    @FXML
    public TableColumn<meritListTableModel,String> tableCategory;

    @FXML
    public TableColumn<meritListTableModel,String> totalMarks;

    @FXML
    public TableColumn<meritListTableModel,String> tableMaths;

    @FXML
    public TableColumn<meritListTableModel,String> tablePhysics;

    @FXML
    public TableColumn<meritListTableModel,String> tableChemistry;

    ObservableList<meritListTableModel> oblist = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle) {


        try {
            this.populateTableFromDatabase();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }

    public void populateTableFromDatabase() throws SQLException {
        Connection conn = databaseConnection.connect();
        Statement stmt = conn.createStatement();

        String sql = "SELECT students.rollnumber, students.studentName,students.category, marks.maths+marks.physics+marks.chemistry AS total, marks.maths, marks.physics, marks.chemistry FROM students, marks WHERE students.rollnumber=marks.rollnumber ORDER BY total DESC, marks.maths DESC, marks.physics DESC, marks.chemistry DESC";
        ResultSet rs = stmt.executeQuery(sql);

        int i=0;
        while(rs.next()){
            StringBuilder genRoll = new StringBuilder();

            genRoll.append("0".repeat(Math.max(0, 6 - (String.valueOf(rs.getInt("rollnumber")).length()+ 1))));   //appending required 0's to roll

            genRoll.append(rs.getInt("rollnumber"));

            if(rs.getString("category").equals("general")){
                genRoll.append('G');
            }else{
                genRoll.append('R');
            }

            oblist.add(new meritListTableModel(++i,genRoll.toString(),rs.getString("studentname").substring(0,1).toUpperCase()+rs.getString("studentname").substring(1),rs.getString("category").substring(0,1).toUpperCase()+rs.getString("category").substring(1),rs.getInt("total"),rs.getInt("maths"),rs.getInt("physics"),rs.getInt("chemistry")));
        }

        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        tableRoll.setCellValueFactory(new PropertyValueFactory<>("rollnumber"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        tableCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        totalMarks.setCellValueFactory(new PropertyValueFactory<>("totalMarks"));
        tableMaths.setCellValueFactory(new PropertyValueFactory<>("maths"));
        tablePhysics.setCellValueFactory(new PropertyValueFactory<>("physics"));
        tableChemistry.setCellValueFactory(new PropertyValueFactory<>("chemistry"));

        meritListTableView.setItems(oblist);
        stmt.closeOnCompletion();
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
