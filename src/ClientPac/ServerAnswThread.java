package ClientPac;

import GUI.*;
import ParkingPac.Parking;
import ParkingPac.Place;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author User
 */
public class ServerAnswThread extends Thread {

    private Socket socket;
    private ViewController vContrl;
    private ObjectInputStream in;

    public ServerAnswThread(Socket socket, ViewController vContrl) throws IOException {
        this.socket = socket;
        this.vContrl = vContrl;
        this.in = new ObjectInputStream(Client.getSocket().getInputStream());
        this.start();
    }

    @Override
    public void run() {
        try {
            while (true) {
                Object obj = in.readObject();
                if (obj != null) {
                    if (obj instanceof Parking) {
                        Parking parking = (Parking) obj;
                        vContrl.setParking(parking);
                        if (vContrl.getEntryPoint() != null) {
                            vContrl.showParking();

                        }

                    } else if (obj instanceof Place) {
                        final Place place = (Place) obj;
                        vContrl.showParking();
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                vContrl.getPlaceScreen().setText(place.toString());
                                System.out.println(place.toString());
                            }
                        });
                    }
                }
            }
            }catch (IOException ex) {
            ex.printStackTrace();
        }catch (ClassNotFoundException ex) {
            ex.printStackTrace();
        }finally {
            try {
                in.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        }
    }
