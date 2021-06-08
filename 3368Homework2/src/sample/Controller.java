package sample;

import com.jfoenix.controls.*;
import com.jfoenix.validation.*;
import javafx.beans.value.ObservableValue;
import javafx.collections.*;
import javafx.event.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

public class Controller implements Initializable {
    final String username = "admin";
    final String password = "uhcot2020";
    final String AWS_URL = "jdbc:mysql://studentdb.c5gtnl9fas5d.us-east-1.rds.amazonaws.com:3306/firstdb";

    @FXML JFXTextField studentID;
    @FXML JFXTextField studentFN;
    @FXML JFXTextField studentLN;
    @FXML JFXTextField studentAge;
    @FXML JFXTextField studentMajor;
    @FXML JFXTextField studentGPA;
    @FXML JFXButton clearButton;
    @FXML JFXButton addButton;
    @FXML JFXListView <Student> studentListView;
    @FXML Button createTablebutton;
    @FXML Button loadTablebutton;
    @FXML Button deleteTablebutton;
    @FXML Button DisplayCISbutton;
    @FXML Button DisplayAgeButton;
    @FXML Button DisplayGPAButton;
    @FXML Button clearFilters;

    public float randomGPA(){
        return (float) ((float)(Math.random() * ((3.00-1.50) + 1)) + 1.50);
    }

    public void clearStudent(){
        studentID.clear();
        studentFN.clear();
        studentLN.clear();
        studentAge.clear();
        studentMajor.clear();
        studentGPA.clear();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle){
        ObservableList<Student> dbStudentList = FXCollections.observableArrayList();
        studentListView.setItems(dbStudentList);

        studentListView.getSelectionModel().selectedItemProperty().addListener((
                ObservableValue<? extends Student> ov, Student old_val, Student new_val) -> {
            clearStudent();
            studentFN.setLabelFloat(false);
            if (!dbStudentList.isEmpty()){
                Student selectedItem = studentListView.getSelectionModel().getSelectedItem();
                studentID.setText(String.valueOf(selectedItem.getStudID()));
                studentFN.setText((selectedItem).getStudFN());
                studentLN.setText((selectedItem).getStudLN());
                studentAge.setText(String.valueOf((selectedItem).getStudAge()));
                studentMajor.setText((selectedItem).getStudMajor());
                studentGPA.setText(String.valueOf((selectedItem).getStudGPA()));
            }
            studentFN.setLabelFloat(true);
            });

        try{
            Connection conn = DriverManager.getConnection(AWS_URL, username, password);
            Statement stmt = conn.createStatement();

            String sqlStatement = "SELECT * FROM Student";
            ResultSet result = stmt.executeQuery(sqlStatement);

            while(result.next()){
                Student student = new Student();
                student.setStudID(UUID.fromString(result.getString("StID")));
                student.setStudFN(result.getString("StFN"));
                student.setStudLN(result.getString("StLN"));
                student.setStudAge(Integer.parseInt(result.getString("age")));
                student.setStudMajor(result.getString("major"));
                student.setStudGPA(Float.parseFloat(result.getString("gpa")));
                dbStudentList.add(student);
            }
            stmt.close();
            conn.close();
        }
        catch(Exception ex){
            System.out.println(ex.getMessage());
        }

        createTablebutton.setOnAction(actionEvent -> {
            try {
                Connection conn = DriverManager.getConnection(AWS_URL, username, password);
                Statement stmt = conn.createStatement();
                try {
                    stmt.execute("CREATE TABLE Student (" +
                            "stID VARCHAR(36), " +
                            "stFN CHAR(25), " +
                            "stLN CHAR(25), " +
                            "age INT (3), " +
                            "major CHAR(30), " +
                            "gpa FLOAT(4,2)" + ");"
                    );
                    System.out.println("TABLE CREATED");
                    String[] MajorOptions = {"CIS", "Computer Science", "Biology", "CIS", "Sociology", "Psychology",
                    "Accounting", "Marketing", "Journalism", "CIS"};
                    String newID;

                    for (int i = 0; i < 10; i++) {
                        newID = UUID.randomUUID().toString();
                        Student newStudent = new Student();
                        newStudent.studFN = "John";
                        newStudent.studLN = "Doe" + i;
                        newStudent.studAge = 18 + i;
                        stmt.executeUpdate("INSERT INTO Student VALUES ('" + newID + "', '"
                                + newStudent.studFN + "', '" + newStudent.studLN + "', '" + newStudent.studAge + "', " +
                                "'" + MajorOptions[i] + "', '" + randomGPA() + "');"
                        );
                    }
                    stmt.close();
                    conn.close();
                    System.out.println("TABLE FILLED");
                } catch (Exception ex) {
                    System.out.println("TABLE ALREADY EXISTS, NOT CREATED");
                }
            }
            catch(Exception ex){
                System.out.println(ex.getMessage());
            }
        });
        deleteTablebutton.setOnAction(actionEvent -> {
            try{
                Connection conn = DriverManager.getConnection(AWS_URL, username, password);
                Statement stmt = conn.createStatement();
                stmt.execute("DROP TABLE Student");
                stmt.close();
                System.out.println("TABLE DROPPED");

                if(dbStudentList.size() > 0){
                    int dbSize = dbStudentList.size();
                    for (int i = 0; i < dbSize; i++){
                        dbStudentList.remove(0);
                    }
                }
                clearStudent();
            }
            catch(Exception ex){
                System.out.println("TABLE NOT DROPPED");
                System.out.println(ex.getMessage());
            }
        });
        loadTablebutton.setOnAction(actionEvent -> {
            clearStudent();
            try {
                Connection conn = DriverManager.getConnection(AWS_URL, username, password);
                Statement stmt = conn.createStatement();

                if (dbStudentList.size() > 0) {
                    int dbSize = dbStudentList.size();
                    for (int i = 0; i < dbSize; i++) {
                        dbStudentList.remove(0);
                    }
                }
                String sqlStatement = "SELECT * FROM Student";
                ResultSet result = stmt.executeQuery(sqlStatement);
                while (result.next()) {
                    Student student = new Student();
                    student.setStudID(UUID.fromString(result.getString("stID")));
                    student.setStudFN(result.getString("stFN"));
                    student.setStudLN(result.getString("stLN"));
                    student.setStudAge(result.getInt("age"));
                    student.setStudMajor(result.getString("major"));
                    student.setStudGPA(Float.parseFloat(result.getString("gpa")));
                    dbStudentList.add(student);
                }
                stmt.close();
                conn.close();
                System.out.println("DATA LOADED");
                studentListView.getSelectionModel().selectFirst();
                studentListView.requestFocus();
            }
            catch(Exception ex){
                System.out.println("DATA NOT LOADED");
                System.out.println(ex.getMessage());
            }
        });
        clearButton.setOnAction(actionEvent -> {
            clearStudent();
        });

        DisplayCISbutton.setOnAction(actionEvent -> {
            try{
                Connection conn = DriverManager.getConnection(AWS_URL, username, password);
                Statement stmt = conn.createStatement();
                String sqlStatement = "SELECT * FROM Student WHERE major = \"CIS\"";
                ObservableList<Student> filteredCISlist = FXCollections.observableArrayList();
                ResultSet result = stmt.executeQuery(sqlStatement);
                while (result.next()){
                    Student cis = new Student();
                    cis.setStudID(UUID.fromString(result.getString("StID")));
                    cis.setStudFN(result.getString("StFN"));
                    cis.setStudLN(result.getString("StLN"));
                    cis.setStudAge(Integer.parseInt(result.getString("age")));
                    cis.setStudMajor(result.getString("major"));
                    cis.setStudGPA(Float.parseFloat(result.getString("gpa")));
                    filteredCISlist.add(cis);
                }
                studentListView.setItems(filteredCISlist);
                stmt.close();
                conn.close();
                System.out.println("DATA FILTER APPLIED");
            }
            catch(Exception ex){
                System.out.println("DATA NOT LOADED");
                System.out.println(ex.getMessage());
            }
        });

        DisplayAgeButton.setOnAction(actionEvent -> {
            try{
                Connection conn = DriverManager.getConnection(AWS_URL, username, password);
                Statement stmt = conn.createStatement();
                String sqlStatement = "SELECT * FROM Student WHERE age < 22";
                ObservableList<Student> filteredAgelist = FXCollections.observableArrayList();
                ResultSet result = stmt.executeQuery(sqlStatement);
                while (result.next()){
                    Student filAge = new Student();
                    filAge.setStudID(UUID.fromString(result.getString("StID")));
                    filAge.setStudFN(result.getString("StFN"));
                    filAge.setStudLN(result.getString("StLN"));
                    filAge.setStudAge(Integer.parseInt(result.getString("age")));
                    filAge.setStudMajor(result.getString("major"));
                    filAge.setStudGPA(Float.parseFloat(result.getString("gpa")));
                    filteredAgelist.add(filAge);
                }
                studentListView.setItems(filteredAgelist);
                stmt.close();
                conn.close();
                System.out.println("DATA FILTER APPLIED");
            }
            catch(Exception ex){
                System.out.println("DATA NOT LOADED");
                System.out.println(ex.getMessage());
            }
        });

        DisplayGPAButton.setOnAction(actionEvent -> {
            try{
                Connection conn = DriverManager.getConnection(AWS_URL, username, password);
                Statement stmt = conn.createStatement();
                String sqlStatement = "SELECT * FROM Student WHERE gpa > 2.99";
                ObservableList<Student> filteredGPAlist = FXCollections.observableArrayList();
                ResultSet result = stmt.executeQuery(sqlStatement);
                while (result.next()){
                    Student filGPA = new Student();
                    filGPA.setStudID(UUID.fromString(result.getString("StID")));
                    filGPA.setStudFN(result.getString("StFN"));
                    filGPA.setStudLN(result.getString("StLN"));
                    filGPA.setStudAge(Integer.parseInt(result.getString("age")));
                    filGPA.setStudMajor(result.getString("major"));
                    filGPA.setStudGPA(Float.parseFloat(result.getString("gpa")));
                    filteredGPAlist.add(filGPA);
                }
                studentListView.setItems(filteredGPAlist);
                stmt.close();
                conn.close();
                System.out.println("DATA FILTER APPLIED");
            }
            catch(Exception ex){
                System.out.println("DATA NOT LOADED");
                System.out.println(ex.getMessage());
            }
        });






    }

}