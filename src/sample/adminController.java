package sample;

import com.jfoenix.controls.JFXBadge;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tableModels.adminTableModel;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ResourceBundle;

public class adminController implements Initializable {

    public Button backBtn;
    public JFXTextField studentMp;
    public JFXTextField studentMc;
    public JFXTextField studentMm;
    public Button updateBtn;
    public Label studentName;
    public Label studentRoll;
    public Label studentCategory;

    @FXML
    private TableView<adminTableModel> studentTableAdminView;

    @FXML
    public TableColumn<adminTableModel,String> tableRoll;

    @FXML
    public TableColumn<adminTableModel,String> tableName;

    @FXML
    public TableColumn<adminTableModel, String> tableCategory;

    @FXML
    public TableColumn<adminTableModel, Integer> tableMaths;

    @FXML
    public TableColumn<adminTableModel, Integer> tablePhysics;

    @FXML
    public TableColumn<adminTableModel, Integer> tableChemistry;

    ObservableList<adminTableModel> oblist = FXCollections.observableArrayList();

    public void initialize(URL url, ResourceBundle resourceBundle) {


        try{
            this.populateTableFromDatabase();

            //  adding event listener to table to populate fields when selected
            studentTableAdminView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<adminTableModel>() {
                @Override
                public void changed(ObservableValue<? extends adminTableModel> observableValue, adminTableModel adminTableModel, adminTableModel t1) {
                    if(studentTableAdminView.getSelectionModel().getSelectedItem()!=null){
                        TableView.TableViewSelectionModel selectionModel = studentTableAdminView.getSelectionModel();
                        adminTableModel selection = (tableModels.adminTableModel) studentTableAdminView.getSelectionModel().getSelectedItem();
                        populateFields(selection);
                    }
                }
            });

        }catch(Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }

    public void populateTableFromDatabase() throws SQLException {
        Connection conn = databaseConnection.connect();
        Statement stmt = conn.createStatement();

        String sql = "SELECT students.rollnumber, students.studentName,students.category, marks.maths, marks.physics, marks.chemistry FROM students, marks WHERE students.rollnumber=marks.rollnumber";
        ResultSet rs = stmt.executeQuery(sql);


        while(rs.next()){
            StringBuilder genRoll = new StringBuilder();

            genRoll.append("0".repeat(Math.max(0, 6 - (String.valueOf(rs.getInt("rollnumber")).length()+ 1))));   //appending required 0's to roll

            genRoll.append(rs.getInt("rollnumber"));

            if(rs.getString("category").equals("general")){
                genRoll.append('G');
            }else{
                genRoll.append('R');
            }

            oblist.add(new adminTableModel(genRoll.toString(),rs.getString("studentName").substring(0,1).toUpperCase()+rs.getString("studentName").substring(1),rs.getString("category").substring(0,1).toUpperCase()+rs.getString("category").substring(1),rs.getInt("maths"),rs.getInt("physics"),rs.getInt("chemistry")));
        }

        tableRoll.setCellValueFactory(new PropertyValueFactory<>("rollnumber"));
        tableName.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        tableCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        tableMaths.setCellValueFactory(new PropertyValueFactory<>("maths"));
        tablePhysics.setCellValueFactory(new PropertyValueFactory<>("physics"));
        tableChemistry.setCellValueFactory(new PropertyValueFactory<>("chemistry"));

        studentTableAdminView.setItems(oblist);
        stmt.closeOnCompletion();
    }

    public void populateFields(adminTableModel mod){
        this.studentName.setText(mod.getStudentName().substring(0,1).toUpperCase()+mod.getStudentName().substring(1));
        this.studentRoll.setText(mod.getRollnumber());
        this.studentCategory.setText(mod.getCategory().substring(0,1).toUpperCase()+mod.getCategory().substring(1));
        this.studentMm.setText(String.valueOf(mod.getMaths()));
        this.studentMp.setText(String.valueOf(mod.getPhysics()));
        this.studentMc.setText(String.valueOf(mod.getChemistry()));
    }

    public void updateBtnClick(ActionEvent actionEvent) throws SQLException {
        try{
            String sid = this.studentRoll.getText();

            int id = 0;
            for(int i=0;i<sid.length()-1;++i){
                if(sid.charAt(i)>='0'&& sid.charAt(i)<='9'){
                    id=id*10+(sid.charAt(i)-'0');
                }else{
                    throw new Exception("Incorrect Roll Number!");
                }
            }

            Connection conn = databaseConnection.connect();
            Statement stmt = conn.createStatement();

            String sql = "UPDATE marks SET maths="+Integer.parseInt(this.studentMm.getText())+",physics="+Integer.parseInt(this.studentMp.getText())+",chemistry="+Integer.parseInt(this.studentMc.getText())+" WHERE rollnumber="+id+"";

            stmt.execute(sql);

            //  clearing table
            oblist.clear();

            //  populating table
            populateTableFromDatabase();

            JOptionPane.showMessageDialog(null,"Update Successful!");
            stmt.closeOnCompletion();
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,e);
        }
    }
    public void backBtnClick(javafx.event.ActionEvent actionEvent) throws IOException
    {

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
