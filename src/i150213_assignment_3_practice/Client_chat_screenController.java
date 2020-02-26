/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package i150213_assignment_3_practice;

import static i150213_assignment_3_practice.FXMLDocumentController.global_user;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.sound.sampled.LineUnavailableException;

/**
 * FXML Controller class
 *
 * @author Hamza
 */
public class Client_chat_screenController implements Initializable {

    /**
     * Initializes the controller class.
     * 
     */

    @FXML
    private TextField message_field;
    public TextArea area_msg;
    public TextArea online_people;
    Client clie = new Client();
     String  username;
static int count=0;    
static Boolean new_user;
    public void sender(ActionEvent sb) throws InterruptedException, IOException  {
        //when client presss send button
                  FXMLDocumentController fdc = new  FXMLDocumentController();

              if(count==0) //if new user is logged in
              {
                            clie.getting_client_connection("Welcome",fdc.global_user,area_msg,new_user,online_people);

                  new_user=true;
                  count++;
              }
              else
              {
                  new_user=false;
              }
        String whole_msg  = "";
        String send_message = message_field.getText();
        message_field.clear();
        whole_msg+=fdc.global_user+": "+send_message;
          clie.getting_client_connection(whole_msg,fdc.global_user,area_msg,new_user,online_people);

                
             
    }
    
    
    @FXML
    public void file_opener(ActionEvent e) throws IOException{  //for sending file
        FileChooser fileChooser = new FileChooser();
                FXMLDocumentController fdc = new  FXMLDocumentController();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)","*.txt");
                FileChooser.ExtensionFilter extFilter1 = new FileChooser.ExtensionFilter("WAV files(*.wav)","*.wav");

    fileChooser.getExtensionFilters().addAll(extFilter,extFilter1);
    File file = fileChooser.showOpenDialog(null);
        clie.send_file(file,fdc.global_user);
    }
    
    
        
    @FXML
    private Label cusername_box;
    public void handleusernameshow(ActionEvent event) throws IOException, SQLException, ClassNotFoundException
    {

                          
              FXMLDocumentController fdc = new  FXMLDocumentController();

         username =fdc.global_user;
      
        cusername_box.setText(username);
        
    }
    @FXML
    public void voice_recorder(ActionEvent e) throws LineUnavailableException, InterruptedException
    {
         FXMLDocumentController fdc = new  FXMLDocumentController();
         audio_recorder ar=new audio_recorder(fdc.global_user);
         ar.audio_record();
            
         

    }
    @FXML
    private Button closee;
    public void chat_window_close(ActionEvent even) throws SQLException, ClassNotFoundException
    {
        Server s = new Server();
       s.make_offline(global_user);
             
         Stage stage = (Stage) closee.getScene().getWindow();
        stage.close();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
           
    }    
    
}
