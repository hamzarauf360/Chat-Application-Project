/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package i150213_assignment_3_practice;

import com.sun.org.apache.bcel.internal.generic.GOTO;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author Hamza
 */
public class SignupController implements Initializable { // handles sign up window

    /**
     * Initializes the controller class.
     */
    @FXML
    public Button submitButton;
    @FXML
    public TextField susername_box;
    @FXML
    private TextField spassword_box;
    @FXML
    private TextField sname_box;
    @FXML
    private TextField semail_box;
    @FXML
    public Label enter_info_again;
    @FXML
    private void handlesignup(ActionEvent e)throws IOException{  //for signing up a client
        String usernamee=susername_box.getText();
        String passwordd=String.valueOf(spassword_box.getText());
        String namee=sname_box.getText();
        String emaill=semail_box.getText();
        
        if(usernamee.equals("")||passwordd.equals("")||namee.equals("")||emaill.equals("")) //if any information is not added
        {
            enter_info_again.setText("Enter all information please");
        }
        else{  //if every thing is allright
       
            Server s = new Server();
            s.signup_server(usernamee,passwordd,namee,emaill,susername_box,enter_info_again,submitButton); //db access in server
       
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
    
}
