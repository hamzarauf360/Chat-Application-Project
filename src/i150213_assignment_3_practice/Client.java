/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package i150213_assignment_3_practice;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
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
import javafx.scene.control.TextArea;

/**
 *
 * @author Hamza
 */
public class Client {

    File file = null;
    ClientThread ct;
    String data;
    ArrayList<String> posted = new ArrayList<>();
    String status;
    ArrayList<TextArea> ta = new ArrayList<TextArea>();  //for adding users
    ArrayList<TextArea> online_people_area_list = new ArrayList<TextArea>();
    String username;
    public TextArea area_msg;
    public TextArea online_people_2;
    String filename;

    public Client() {

        try {

            Socket s = new Socket("localhost", 4444);

            ct = new ClientThread(s, this);

            ct.start();

        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void getting_client_connection(String whole_msg, String user, TextArea textarea, Boolean new_user, TextArea online_people1) throws InterruptedException, IOException {

        if (new_user) //if new user then add in textarealist
        {
            ta.add(textarea);
            online_people_area_list.add(online_people1);
        }

        ct.msg_to_server(whole_msg);

    }

    public void send_file(File file, String user) throws IOException //for sending file
    {
        String temp = file.toString();
        String filenam = temp.substring(temp.lastIndexOf("\\") + 1, temp.indexOf("."));
        if (temp.contains(".txt")) {
            filenam += ".txt";
        } else if (temp.contains(".wav")) {
            filenam += ".wav";
        }
        filename = filenam;
        this.file = file;

        if (file != null && temp.contains(".txt")) {
            ct.file_to_server(file);
        } else if (file != null && temp.contains(".wav")) {
            ct.wav_file_to_server(file);
        }
    }

    public class ClientThread extends Thread {

        Socket s;
        DataInputStream din;
        DataOutputStream dout;
        FileWriter fstream;
        PrintWriter out;
        boolean shouldrun = true;

        public ClientThread(Socket socket, Client client) throws IOException {
            s = socket;

        }

        public void msg_to_server(String Text) throws IOException {
            dout.writeUTF("chat message sending");
            dout.flush();
            dout.writeUTF(Text);
            dout.flush();

        }

        public void file_to_server(File server_file) throws IOException {
            FileInputStream fin = new FileInputStream(server_file);
            BufferedReader bcr = new BufferedReader(new InputStreamReader(fin));
            String str;
            int read;
            dout.writeUTF("text file sharing");
            dout.flush();
            dout.writeUTF(filename);
            dout.flush();
            while ((str = bcr.readLine()) != null) {

                dout.writeUTF(str);
                dout.flush();
            }
            dout.writeUTF("text file sharing stop");
            dout.flush();
            fin.close();

        }

        public void wav_file_to_server(File wav_server_file) throws FileNotFoundException, IOException {
            FileInputStream fin = new FileInputStream(wav_server_file);
            byte b[] = new byte[1024];
            int read;
            dout.writeUTF("wav file sharing"); //works good 
            dout.flush();
            dout.writeUTF(filename); //works good
            dout.flush();
            while ((read = fin.read(b)) != -1) {
                dout.write(b, 0, read);
                dout.flush();
            }
            /* dout.writeUTF("wav file sharing stop");
            dout.flush();*/
            System.out.println("send of file completed");
            fin.close();
            dout.flush();
        }

        public void run() {
            try {

                din = new DataInputStream(s.getInputStream());
                dout = new DataOutputStream(s.getOutputStream()); //for sending message again to other clients

                byte b[] = new byte[1024];
                int bytesRead;
                String chk = "";
                String reply = "";
                String str = "";
                while (shouldrun) {

                    while (din.available() == 0) {

                        Thread.sleep(1);
                    }
                    chk = din.readUTF();
                    if (chk.equals("wav file sharing")) {

                        str = din.readUTF();
                        if (str.contains(".wav")) {
                            FXMLDocumentController fdc = new FXMLDocumentController();

                            filename = fdc.global_user;
                            filename += str;
                            System.out.println("The filename has been calculated successfully : " + filename); //working great till here
                        }
                        FileOutputStream fos = new FileOutputStream(new File(filename), true);
                        do {

                            bytesRead = din.read(b, 0, b.length);
                            fos.write(b, 0, b.length);
                        } while (!(bytesRead < 1024));
                        for (int i = 0; i < ta.size(); i++) {
                            area_msg = ta.get(i);
                            area_msg.appendText("An audio attachment has been recieved check your project folder" + "\n");

                        }
                    }
                    if (chk.equals("text file sharing")) {
                        do {
                            str = din.readUTF();
                            if (str.contains(".txt")) {
                                FXMLDocumentController fdc = new FXMLDocumentController();
                                filename = fdc.global_user;
                                filename += str;
                                fstream = new FileWriter(filename);
                                out = new PrintWriter(fstream);
                            }
                            if (!str.equals("text file sharing stop") && !str.equals("text file sharing") && !str.contains(".txt")) { // dont send to all clients if str is stop
                                out.println(str);
                                out.flush();
                                if (str == null) {
                                    break;
                                }
                            }

                            if (str == null) {
                                break;
                            }

                        } while (true && !str.equals("text file sharing stop"));
                        for (int i = 0; i < ta.size(); i++) {
                            area_msg = ta.get(i);
                            area_msg.appendText("A text attachment has been recieved check your project folder" + "\n");

                        }
                    }
                    if (chk.equals("chat message sending")) { // if it is for chat message recieveing then do this
                        reply = din.readUTF();  //the message recieved by all clients

                        for (int i = 0; i < online_people_area_list.size(); i++) {

                            String query = "Select * from user_details";

                            Class.forName("com.mysql.jdbc.Driver");
                            Connection conn = DriverManager.getConnection("jdbc:mysql:///user", "hamza", "iAmrosh2");
                            PreparedStatement pstmt = conn.prepareStatement(query);
                            //   pstmt.setString(1, "ehsan121");
                            ResultSet rss = pstmt.executeQuery();
                            while (rss.next()) {

                                username = rss.getString("username");
                                status = rss.getString("online_status");
                                if (status.equals("online")) // for displaying names of those people who are online
                                {
                                    online_people_2 = online_people_area_list.get(i);
                                    data = online_people_2.getText();

                                    if (!data.contains(username)) {
                                        online_people_2.appendText(username + "\n");

                                    }
                                } else if (status.equals("ofline")) //if a user becomes offline suddenly then his name should be cleared from online box
                                {

                                    online_people_2 = online_people_area_list.get(i);
                                    data = online_people_2.getText();
                                    if (data.contains(username)) {
                                        data = data.replaceAll("\n" + username, "");
                                        online_people_2.appendText(username + " has gone offline so online people now are :\n");
                                        online_people_2.appendText(data);
                                    }

                                }

                            }
                        }

                        for (int i = 0; i < ta.size(); i++) {
                            area_msg = ta.get(i);
                            area_msg.appendText(reply + "\n");

                        }
                    }

                }

            } catch (IOException ex) {
                try {
                    close();

                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex1) {
                    Logger.getLogger(ClientThread.class.getName()).log(Level.SEVERE, null, ex1);
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            }/* catch (ClassNotFoundException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        } */


        }

        public void close() throws IOException {
            din.close();
            dout.close();
            s.close();
            out.close();
        }
    }

}
