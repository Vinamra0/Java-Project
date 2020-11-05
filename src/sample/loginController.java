package sample;

import com.jfoenix.controls.*;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.ResourceBundle;

public class loginController implements Initializable {

    public Button rankBTN;
    public Button loginBTN;
    public Button signupBTN;
    public TextField loginrollno;
    public TextField loginpassword;
    public TextField signupname;
    public TextField signupfather;
    public TextField signupmother;
    public TextField signupcontact;
    public TextField signuppassword;
    public RadioButton generalradio;
    public RadioButton reservationradio;

    public RadioButton maleradio;
    public RadioButton femaleradio;
    public JFXComboBox day;
    public JFXComboBox month;
    public JFXComboBox year;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<Integer> yearList = year.getItems();
        ObservableList<Integer> dayList = day.getItems();
        ObservableList<String> monthList = month.getItems();

        for(int i=2010;i>=1980;--i){
            yearList.add(i);
        }

        for(int i=1;i<=31;++i){
            dayList.add(i);
        }

        String[] months = {"January","February","March","April","May","June","July", "August", "September", "October", "November", "December"};

        monthList.addAll(Arrays.asList(months));

    }

    public void loginBTNclick(ActionEvent actionEvent) {
        try{
            String sid = loginrollno.getText();
            String pwd = loginpassword.getText();

            String category;

            //  checking if user is admin
            if(sid.equals("admin") && pwd.equals("admin")){
                //closing current stage
                Stage stage = (Stage) loginBTN.getScene().getWindow();
                stage.close();

                //making another stage
                Stage adminStage = new Stage();


                Parent admin = FXMLLoader.load(getClass().getResource("admin.fxml"));

                Scene scene = new Scene(admin, Main.height , Main.width);


                adminStage.setTitle("Admin Page");



                adminStage.setScene(scene);
                adminStage.show();
            }else{
                if(sid.charAt(sid.length()-1)=='G'){
                    category = "general";
                }else if(sid.charAt(sid.length()-1)=='R'){
                    category = "reservation";
                }else{
                    throw new Exception("Incorrect Roll Number!");
                }

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

                String sql = "SELECT * FROM students WHERE students.rollnumber='"+id+"' AND students.password='"+pwd+"' AND students.category='"+category+"'";
                ResultSet rs = stmt.executeQuery(sql);

                if(rs.next()){
                    //getting student info
                    sql = "SELECT * FROM students,marks WHERE students.rollnumber='"+id+"' AND students.password='"+pwd+"' AND students.category='"+category+"' AND students.rollnumber=marks.rollnumber";
                    rs = stmt.executeQuery(sql);

                    studentController.tstudentName =  rs.getString("studentName").substring(0,1).toUpperCase()+rs.getString("studentName").substring(1);
                    studentController.tstudentRoll =  sid;
                    studentController.tstudentSex =  rs.getString("sex").substring(0,1).toUpperCase()+rs.getString("sex").substring(1);
                    studentController.tstudentCategory =  rs.getString("category").substring(0,1).toUpperCase()+rs.getString("category").substring(1);
                    studentController.tstudentFName =  rs.getString("fName").substring(0,1).toUpperCase()+rs.getString("fName").substring(1);
                    studentController.tstudentMName =  rs.getString("mName").substring(0,1).toUpperCase()+rs.getString("mName").substring(1);
                    studentController.tstudentmM =  rs.getInt("maths");
                    studentController.tstudentpM =  rs.getInt("physics");
                    studentController.tstudentcM =  rs.getInt("chemistry");

                    //closing current stage
                    Stage stage = (Stage) loginBTN.getScene().getWindow();
                    stage.close();

                    //making another stage
                    Stage studentStage = new Stage();


                    Parent admin = FXMLLoader.load(getClass().getResource("student.fxml"));

                    Scene scene = new Scene(admin, Main.height , Main.width);


                    studentStage.setTitle("Student Details");


                    studentStage.setScene(scene);
                    studentStage.show();

                }else
                    throw new Exception("Roll Number or Password is incorrect!");
                stmt.closeOnCompletion();
            }
        }catch (Exception e){
            JOptionPane.showMessageDialog(null,e.getMessage());
        }
    }

    public void signupBTNclick(ActionEvent actionEvent) {
        try{
            String studentName = signupname.getText();
            String password = signuppassword.getText();
            String fName = signupfather.getText();
            String mName = signupmother.getText();
            String selectedSex = maleradio.isSelected()?"male":"female";
            String category = generalradio.isSelected()?"general":"reservation";

            if(studentName.length()==0 && fName.length()==0 && mName.length()==0){
                throw new Exception("Please fill all the fields.");
            }

            int dobDay = (int) day.getValue();
            String dobMonth = (String) month.getValue();
            int dobYear = (int) year.getValue();

            Connection conn = databaseConnection.connect();
            Statement stmt = conn.createStatement();

            String sql;

            //  checking if the candidate already exists
            sql = "SELECT * FROM students WHERE students.studentName='"+studentName+"' AND students.password='"+password+"' AND students.category='"+category+"' AND students.fName='"+fName+"' AND students.mName='"+mName+"' AND students.dobD='"+dobDay+"' AND students.dobM='"+dobMonth+"' AND students.dobY='"+dobYear+"'";
            ResultSet rs = stmt.executeQuery(sql);

            if(rs.next()){
                throw new Exception("Candidate Already exists!");
            }

            //  inserting into students database
            sql = "INSERT INTO students(password, studentName, fName, mName, sex, category, dobD, dobM, dobY) VALUES ('"+password+"','"+studentName+"', '"+fName+"','"+mName+"', '"+selectedSex+"', '"+category+"', '"+dobDay+"', '"+dobMonth+"', '"+dobYear+"')";
            stmt.execute(sql);

            //  getting the roll number from database
            sql = "SELECT * FROM students WHERE students.studentName='"+studentName+"' AND students.password='"+password+"' AND students.category='"+category+"' AND students.fName='"+fName+"' AND students.mName='"+mName+"' AND students.dobD='"+dobDay+"' AND students.dobM='"+dobMonth+"' AND students.dobY='"+dobYear+"'";
            rs = stmt.executeQuery(sql);

            int databaseRoll = rs.getInt("rollNumber");

            //  inserting into marks database
            sql = "INSERT INTO marks VALUES ('"+databaseRoll+"','"+ 0 +"', '"+0+"','"+0+"')";
            stmt.execute(sql);

            StringBuilder genRoll = new StringBuilder();

            genRoll.append("0".repeat(Math.max(0, 6 - (String.valueOf(databaseRoll).length()+ 1))));   //appending required 0's to roll

            genRoll.append(databaseRoll);

            if(category.equals("general")){
                genRoll.append('G');
            }else{
                genRoll.append('R');
            }

            JOptionPane.showMessageDialog(null,"Registered Successfully! Your roll Number is "+ genRoll);
            stmt.closeOnCompletion();
        }
        catch (Exception e){
            JOptionPane.showMessageDialog(null,e.getMessage());
        }

    }

    public void rankBTNclick(ActionEvent actionEvent) throws IOException {
        //closing current stage
        Stage stage = (Stage) rankBTN.getScene().getWindow();
        stage.close();

        //making another stage
        Stage rankListStage = new Stage();


        Parent admin = FXMLLoader.load(getClass().getResource("rankList.fxml"));

        Scene scene = new Scene(admin, Main.height , Main.width);

        rankListStage.setTitle("Rank List");
        

        rankListStage.setScene(scene);
        rankListStage.show();

    }
}
