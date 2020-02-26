/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package i150213_assignment_3_practice;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Hamza
 */
public class FXMLDocumentController implements Initializable {
    public static String global_user;  //for displaying username on the text field of chat
    
   
     
    
    
    @FXML
    private void signupAction(ActionEvent e)throws IOException{   //when users clicks sign up 
        Parent signup_parent = FXMLLoader.load(getClass().getResource("signup.fxml"));
        Scene signup_scene = new Scene(signup_parent);
        Stage app_stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        app_stage.hide();
        app_stage.setScene(signup_scene);

        app_stage.show();
        
    }
    
    @FXML
    private Label invalid_label;
    @FXML
    public TextField username_box;
    @FXML
    private TextField password_box;
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, SQLException
    {
     

        Parent Client_chat_screen_parent = FXMLLoader.load(getClass().getResource("Client_chat_screen.fxml"));
        Scene Client_chat_screen_scene = new Scene(Client_chat_screen_parent);
        Stage app_stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
         Server s = new Server();
                 FXMLDocumentController fdc = new FXMLDocumentController();

        if(s.is_valid(username_box,password_box,fdc)) //if username exists at server
        { 
            app_stage.hide();

            app_stage.setScene(Client_chat_screen_scene);

            app_stage.show();
        }
        
        else
        {
            username_box.clear();
            password_box.clear();
            invalid_label.setText("Invalid credentials!");
        }
            
    }
   
    
    @FXML
    private Button closeButton;  // for closing window
    @FXML
    private void close_window(ActionEvent e)  //closes window
    {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO            
           
        
    }    
    
}
