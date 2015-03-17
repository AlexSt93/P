package ParkingPac;

import ServerPac.Server;
import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Александр
 */
public class Parking implements Serializable {

    private int idPark = 1;

    private Map<String, ArrayList<Place>> places;
    private String adress;
    private int capacity;
    private int maxIdPlace;
    private Map<String, ArrayList<String>> areaList;
    //static final DBConnection con = DBConnection.getInsance();

    private static Parking instance;

    public Parking() {
        this.places = getPlacesFromDB();
        ArrayList<String> listTop = new ArrayList<>();
        listTop.add("A");
        listTop.add("B");
        listTop.add("C");

        ArrayList<String> listBot = new ArrayList<>();
        listBot.add("C");
        listBot.add("B");
        listBot.add("A");

        areaList = new HashMap();
        this.areaList.put("top", listTop);
        this.areaList.put("bot", listBot);
    }

    private Map<String, ArrayList<Place>> getPlacesFromDB() {

        Map<String, ArrayList<Place>> places = new HashMap();
        String queryArea = "select * from place order by areaName, placePosition";
        ArrayList<Place> placeList;
        try {
            ResultSet rs2 = Server.con.getSt().executeQuery("select max(id) from place");
            rs2.next();
            maxIdPlace = rs2.getInt(1);

            rs2.close();
            ResultSet rs = Server.con.getSt().executeQuery(queryArea);
            if (rs == null) {
                places.put("", new ArrayList<Place>());
            }
            while (rs.next()) {
                String areaName = rs.getString("areaName");
                if (places.containsKey(areaName)) {
                    placeList = places.get(areaName);
                } else {
                    placeList = new ArrayList();
                }
                Place p = new Place(rs.getInt(1),
                        rs.getInt(2),
                        rs.getString(3),
                        rs.getInt(4),
                        rs.getInt(5),
                        rs.getInt(6),
                        rs.getInt(7));
                placeList.add(p);
                places.put(areaName, placeList);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return places;
    }

    private void parkingChanged(String[] query) {
        try {
            for (String q : query) {
                if (q != null) {
                    System.out.println(q);
                    Server.con.getSt().execute(q);
                    System.out.println("DB updated");
                }

            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    public synchronized static Place searchPlace(int carSize, String entryPoint) {

        ArrayList<String> areas = instance.getAreaList().get(entryPoint);
        Place place = new Place("!");
        System.out.println(areas);
        for (String a : areas) {
            String areaName = a;
            String[] query = new String[3];
            ArrayList<Place> placeList = instance.getPlaces().get(areaName);
            if (placeList == null) {
                placeList = new ArrayList();
            }
            int sizeFreePlaces = 0;
            int space = 0;
            int posForNewPlace = 0;
            //Place PrevPlace = new Place(0, 1, areaName, 1, 0, 0, 1);
            List<Place> freePlaces = new ArrayList();
            for (Place p : placeList) {
                int placeStatus = p.getStatus();
                int placeSize = p.getPlaceSize();
                if (placeStatus == 0) {
                    if (freePlaces.size() > 0) {
                        if (placeSize + sizeFreePlaces >= carSize) {

                            placeList.get(placeList.indexOf(freePlaces.get(0))).setStatus(1);
                            placeList.get(placeList.indexOf(freePlaces.get(0))).setPlaceSize(carSize);
                            place = freePlaces.get(0); //place = 
                            query[0] = "update place "
                                    + "set status = 1,"
                                    + "placeSize = " + carSize
                                    + " where id =" + freePlaces.get(0).getId() + "; ";
                            if (freePlaces.size() > 1) {
                                placeList.remove(freePlaces.get(1));
                                query[1] = "delete from place "
                                        + "where id =" + freePlaces.get(1).getId() + "; ";
                            }
                            if ((sizeFreePlaces + placeSize - carSize) == 0) {
                                placeList.remove(p);
                                query[2] = "delete from place "
                                        + "where id =" + p.getId() + "; ";

                            } else {
                                p.setPlaceSize(sizeFreePlaces + placeSize - carSize);
                                p.setPlacePosition(p.getPlacePosition() + (sizeFreePlaces + placeSize - carSize));
                                query[2] = "update place set placeSize =" + p.getPlaceSize()
                                        + ", placePosition = " + p.getPlacePosition()
                                        + " where id = " + p.getId();
                            }
                            sizeFreePlaces -= carSize;
                            freePlaces.clear();
                            break;
                        } else {

                            freePlaces.add(p);
                            sizeFreePlaces += placeSize;
                        }
                    } else {

                        if (placeSize >= carSize) {

                            if (placeSize == carSize) {
                                p.setStatus(1);
                                query[0] = "update place "
                                        + "set status = 1 "
                                        + "where id =" + p.getId() + "; ";
                            } else {
                                p.setPlaceSize(carSize);
                                p.setStatus(1);
                                query[0] = "update place "
                                        + "set status = 1, "
                                        + "placeSize =" + carSize
                                        + " where id =" + p.getId() + "; ";
                                Place newPlace = new Place(++instance.maxIdPlace, 0, areaName, 1, placeSize - carSize, p.getPlacePosition() + carSize, 1);
                                placeList.add(placeList.indexOf(p) + 1, newPlace);
                                //placeList сортировка 
                                query[1] = "insert into place values (" + newPlace.getId() + ","
                                        + "0,"
                                        + "'" + areaName + "'"
                                        + ",1,"
                                        + newPlace.getPlaceSize() + ","
                                        + newPlace.getPlacePosition() + ","
                                        + "1); ";
                            }
                            place = p; //place =
                            break;
                        } else {
                            freePlaces.add(p);
                            sizeFreePlaces += placeSize;
                        }

                    }

                } else {
                    posForNewPlace = p.getPlacePosition() + placeSize;
                }
                space += placeSize;
            }

            if ((space + carSize) <= 24 && place.getId() == 0) {
                if (freePlaces.size() > 0) {
                    placeList.removeAll(freePlaces);
                    freePlaces.clear();
                }
                Place newPlace = new Place(++instance.maxIdPlace, 1, areaName, 1, carSize, posForNewPlace, 1);
                query[0] = "insert into place values (" + newPlace.getId() + ","
                        + "1,"
                        + "'" + areaName + "'"
                        + ",1,"
                        + carSize + ","
                        + posForNewPlace + ","
                        + "1); ";
                placeList.add(newPlace);
                place = newPlace;
            }
            if (place.getId() != 0) {
                instance.getPlaces().put(areaName, placeList);
                instance.parkingChanged(query);
                break;

            }
        }
        //places.put(areaName, placeList);
        return place;
    }

    public synchronized static Place carIsLeaving(Place place) {
        Place foundPlace = new Place();
        String[] query = new String[1];
        ArrayList<Place> placeList = instance.getPlaces().get(place.getAreaName());
        for (Place p : placeList) {
            if (p.getPlacePosition() == place.getPlacePosition()) {
                p.setStatus(0);
                query[0] = "update place set status = 0 where id = " + p.getId();
                instance.parkingChanged(query);
                foundPlace = p;
                instance.getPlaces().put(place.getAreaName(), placeList);
                break;
            }
        }
        return foundPlace;
    }

    public static Parking getInstance() {
        if (instance == null) {
            instance = new Parking();
        }
        return instance;
    }

    public void showParking(String entryPoint) {
        ArrayList<String> areas = this.getAreaList().get(entryPoint);
        for (String a : areas) {
            System.out.println("Area " + a + ": ");
            ArrayList<Place> placeList = this.getPlaces().get(a);
            if (placeList == null) {
                continue;
            }
            for (Place p : placeList) {
                System.out.print(" |" + p.getAreaName() + p.getPlacePosition() + " " + p.getPlaceSize() + "(" + p.getStatus() + ")| ");
            }
            System.out.println("\n");
        }
    }

    public boolean isAvaible() {
        boolean res = true;
        if (getPlaces() == null) {
            res = false;
        }
        return res;
    }

    /**
     * @return the places
     */
    public Map<String, ArrayList<Place>> getPlaces() {
        return places;
    }

    /**
     * @return the areaList
     */
    public Map<String, ArrayList<String>> getAreaList() {
        return areaList;
    }

    /**
     * @param areaList the areaList to set
     */
    public void setAreaList(Map<String, ArrayList<String>> areaList) {
        this.areaList = areaList;
    }
}
