package i150213_assignment_3_practice;

import static i150213_assignment_3_practice.FXMLDocumentController.global_user;
import java.net.ServerSocket;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 *
 * @author Hamza
 */
public class Server {   // to send and recieve msgs from clients

    public static final int PORT = 4444;
    ServerSocket s_socket;

    ArrayList<ServerConnectionThread> connections = new ArrayList<ServerConnectionThread>(); //for storing incoming client connections

    public static void main(String[] args) throws IOException {
        new Server().serverStart();
    }

    public void signup_server(String usernamee, String passwordd, String namee, String emaill, TextField susername_box, Label enter_info_again, Button submitButton) {
        //SignupController sc = new SignupController();
        boolean duplicate = false;

        Connection conn = null;
        PreparedStatement pstmt = null;
        PreparedStatement pstmt3 = null;

        try {

            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection("jdbc:mysql:///user", "hamza", "iAmrosh2");
            pstmt = conn.prepareStatement("insert into user_details (username,password,name,email,online_status) values(?,?,?,?,?)");
            pstmt3 = conn.prepareStatement("Select * from user_details where username=?");
            pstmt3.setString(1, susername_box.getText());
            ResultSet rss = pstmt3.executeQuery();
            while (rss.next()) {
                duplicate = true;

            }
            if (duplicate) // if username already exixts in database
            {
                susername_box.clear();
                enter_info_again.setText("Enter different username again please");

            }
            if (duplicate == false) {
                pstmt.setString(1, usernamee);
                pstmt.setString(2, passwordd);
                pstmt.setString(3, namee);
                pstmt.setString(4, emaill);
                pstmt.setString(5, "ofline");
                int j = pstmt.executeUpdate();
                if (j > 0) {
                    enter_info_again.setText("Info saved");
                    Stage stage = (Stage) submitButton.getScene().getWindow();
                    stage.close();
                } else {
                    enter_info_again.setText("Info not saved");

                }
            }

        } catch (Exception ee) {
            System.out.println(ee);
        }
    }

    public boolean is_valid(TextField username_box, TextField password_box, FXMLDocumentController fdc) throws SQLException {
        boolean found = false;
        Connection connn = null;
        PreparedStatement pstmtt = null;
        PreparedStatement pstmtt4 = null; //for online status

        try {
            Class.forName("com.mysql.jdbc.Driver");
            connn = DriverManager.getConnection("jdbc:mysql:///user", "hamza", "iAmrosh2");
            pstmtt = connn.prepareStatement("Select * from user_details where username=? and password=?");
            String query = "update user_details set online_status='online' where username=?";

            pstmtt.setString(1, username_box.getText());
            pstmtt.setString(2, password_box.getText());
            ResultSet rs = pstmtt.executeQuery();

            while (rs.next()) {

                fdc.global_user = username_box.getText();

                found = true;
            }
            if (found) //if vlaid then he becomes online
            {
                pstmtt4 = connn.prepareStatement(query);
                pstmtt4.setString(1, global_user);
                pstmtt4.executeUpdate();

            }
        } catch (Exception e) {
            System.out.println(e);
        }

        return found;
    }

    public void make_offline(String global_user) throws ClassNotFoundException, SQLException {
        String query = "update user_details set online_status='ofline' where username=?";

        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection("jdbc:mysql:///user", "hamza", "iAmrosh2");
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, global_user);  //puts offline in db for that client who wants to loggout
        pstmt.executeUpdate();
    }

    public void serverStart() throws IOException {
        s_socket = new ServerSocket(PORT);
        System.out.println("Server port number: " + PORT + "\n");
        System.out.println("Server Running");
        while (true) {
            Socket loop_s = s_socket.accept(); //client accept
            System.out.println("A client connected from IP adress: " + loop_s.getInetAddress());
            ServerConnectionThread sct = new ServerConnectionThread(loop_s, this);
            sct.start();
            connections.add(sct);//clients k lye server thread list me add
        }
    }

    public class ServerConnectionThread extends Thread {

        Socket thread_s;
        Server server;
        DataInputStream din;
        DataOutputStream dout;
        Boolean shouldrun = true;

        public ServerConnectionThread(Socket loop_s, Server server) {
            this.thread_s = loop_s;
            this.server = server;
        }

        public void chat_msg_to_all(String text) throws IOException {
            for (int i = 0; i < server.connections.size(); i++) {
                ServerConnectionThread st = server.connections.get(i);
                st.chat_msg_to_one(text);
            }

        }

        public void chat_msg_to_one(String text) throws IOException {
            dout.writeUTF("chat message sending");
            dout.flush();
            dout.writeUTF(text);
            dout.flush();

        }

        public void text_file_stream_to_all(String file_stream) throws IOException {

            for (int i = 0; i < server.connections.size(); i++) {
                ServerConnectionThread st = server.connections.get(i);
                st.text_file_stream_to_one(file_stream);
            }

        }

        public void text_file_stream_to_one(String file_stream) throws IOException {
            dout.writeUTF("text file sharing");
            dout.flush();
            dout.writeUTF(file_stream);
            dout.flush();
        }

        public void wav_file_stream_to_all(String wav_file_stream) throws IOException {
            for (int i = 0; i < server.connections.size(); i++) {
                ServerConnectionThread st = server.connections.get(i);
                st.wav_file_stream_to_one(wav_file_stream);
            }
        }

        public void wav_file_stream_to_one(String wav_file_stream) throws IOException {
            dout.writeUTF("wav file sharing");
            dout.flush();
            dout.writeUTF(wav_file_stream);
            dout.flush();
        }

        public void wav_file_byte_stream_to_all(byte b[], int readbytes) throws IOException {
            for (int i = 0; i < server.connections.size(); i++) {
                ServerConnectionThread st = server.connections.get(i);
                st.wav_file_byte_stream_to_one(b, readbytes);
            }
        }

        public void wav_file_byte_stream_to_one(byte b[], int readbytes) throws IOException {
            dout.write(b, 0, readbytes);
            dout.flush();
        }

        @Override
        public void run() {
            try {
                din = new DataInputStream(thread_s.getInputStream());
                dout = new DataOutputStream(thread_s.getOutputStream()); //for sending message again to other clients
                byte b[] = new byte[1024];
                int bytesRead;

                String str = "";
                String chk = "";

                while (shouldrun) {

                    while (din.available() == 0) {
                        Thread.sleep(1);
                    }

                    chk = din.readUTF();

                    if (chk.equals("wav file sharing")) //if the recorded message is being sent
                    {

                        str = din.readUTF(); // gets only the first name

                        if (str.contains(".wav")) {  //working good
                            System.out.println("Got the filename: " + str);
                            wav_file_stream_to_all(str);

                        }

                        /**
                         * ****************Writer do while here for recieving
                         * bytes**************************
                         */
                        do {
                            bytesRead = din.read(b, 0, b.length);

                            wav_file_byte_stream_to_all(b, bytesRead);
                            // System.exit(PORT);

                        } while (!(bytesRead < 1024));

                        System.out.println("a File was Received by server");

                    }

                    /*else {
                            do {
                                bytesRead = din.read(b, 0, b.length);

                                wav_file_byte_stream_to_all(b);

                            } while (true && !str.equals("wav file sharing stop") && !(bytesRead < 1024));
                            System.out.println("a File was Received by server");
                        }

                    }*/
                    if (chk.equals("text file sharing")) {  // if this comes then it means text file has to be sent
                        do {
                            str = din.readUTF();
                            if (str.contains(".txt")) {
                                text_file_stream_to_all(str);
                            }
                            if (!str.contains(".txt")) {
                                text_file_stream_to_all(str);
                            }

                            if (str == null) {
                                break;
                            }

                        } while (true && !str.equals("text file sharing stop"));

                        System.out.println("a File was Received by server");
                    }
                    if (chk.equals("chat message sending")) {
                        String textIn = din.readUTF();
                        System.out.println("Message recievd by server: " + textIn);

                        chat_msg_to_all(textIn);
                    }

                }

            } catch (IOException e) {
                close();
                e.printStackTrace();
            } catch (InterruptedException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        public void close() {
            try {
                dout.close();
                din.close();
                thread_s.close();
            } catch (IOException ex) {
                Logger.getLogger(ServerConnectionThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }
}
