package sample;
import java.util.*;

public class Student {
    public UUID studID;
    public String studFN;
    public String studLN;
    public int studAge;
    public String studMajor;
    public float studGPA;

    @Override
    public String toString(){
        return this.studFN + " " + this.studLN;
    }

    public void setStudID(UUID ID){
        studID = ID;
    }
    public void setStudFN(String firstName){
        studFN = firstName;
    }
    public void setStudLN(String lastName){
        studLN = lastName;
    }
    public void setStudAge(int Age){
        studAge = Age;
    }
    public void setStudMajor (String Major){
        studMajor = Major;
    }
    public void setStudGPA (Float GPA){
        studGPA = GPA;
    }
    public UUID getStudID(){
        return studID;
    }
    public String getStudFN(){
        return studFN;
    }
    public String getStudLN(){
        return studLN;
    }
    public int getStudAge(){
        return studAge;
    }
    public String getStudMajor(){
        return studMajor;
    }
    public float getStudGPA(){
        return studGPA;
    }

}
