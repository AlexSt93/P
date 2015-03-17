package ServerPac;



import ParkingPac.ParkingCommand;
import ParkingPac.Place;
import ParkingPac.Parking;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


/*
 * To change this license header, ch
 oose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Александр
 */
public class ClientThread extends Thread {

    private Socket socket;
    private Parking parking;
    private static final int SOCKET_TIMEOUT = 120000;

    public ClientThread(Socket socket) throws SQLException {
        this.socket = socket;
        parking = Parking.getInstance();
        this.start();
    }
    public void run() {
//        BufferedReader in = null;
//        PrintWriter out = null;
        ObjectInputStream in = null;
        ObjectOutputStream out = null;
        Place answPlace = null;
        try {
            //socket.setSoTimeout(SOCKET_TIMEOUT);
            //in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            //out = new PrintWriter(socket.getOutputStream());

            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            Server.getUserList().addUser(socket, in, out);
            Object obj;

            //boolean parkingChanged = true;
            out.writeObject(parking);
            out.flush();
            System.out.println("Parking sending");

            while (true) {

                obj = in.readObject();
                if (obj != null) {
                    if (obj instanceof ParkingCommand) {
                        ParkingCommand pc = (ParkingCommand) obj;
                        answPlace = pc.execute();
                        out.writeObject(answPlace);
                        parkingChanged(Server.getUserList().getUsers());
                        out.flush();
                        System.out.println("Answer sending");

                    }
                }

            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
//        } finally {
//            try {
//                //socket.close();
//                //in.close();
//                //out.close();
////            } catch (IOException ex) {
////                ex.printStackTrace();
////            }

        }
    }

    private void parkingChanged(ArrayList<User> userList) throws IOException {
        for (User user : userList) {
            user.getOut().reset();
            user.getOut().writeObject(this.parking);
            user.getOut().flush();
            System.out.println("User: " + user.getSocket().toString());
        }
    }
}
