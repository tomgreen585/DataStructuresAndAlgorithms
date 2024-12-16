// This program is copyright VUW.
// You are granted permission to use it to construct your answer to a COMP103 assignment.
// You may not distribute it in any other way without permission.

/* Code for COMP103 - 2022T2, Assignment 2
 * Name: Thomas Green
 * Username: greenthom
 * ID: 300536064
 */

import ecs100.*;
import java.util.*;
import java.util.Map.Entry;
import java.io.*;
import java.nio.file.*;

/**
 * WellingtonTrains
 * A program to answer queries about Wellington train lines and timetables for
 *  the train services on those train lines.
 *
 * See the assignment page for a description of the program and what you have to do.
 */

public class WellingtonTrains{
    //Fields to store the collections of Stations and Lines
    Map <String, Station> stations = new TreeMap<>();
    Map <String, TrainLine> trainLines = new TreeMap<>();
    
    
    //int zone = 0;
    String nextTrainName = "name";
    int indexLine = 0; 
    // Fields for the suggested GUI.
    private String stationName;        // station to get info about, or to start journey from
    private String lineName;           // train line to get info about.
    private String destinationName;    
    private int startTime = 0;         // time for enquiring about
    private static boolean loadedData = false;  // used to ensure that the program is called from main.
    
    /**
     * main method:  load the data and set up the user interface
     */
    public static void main(String[] args){
        WellingtonTrains wel = new WellingtonTrains();
        wel.loadData();   // load all the data
        wel.setupGUI();   // set up the interface
    }

    /**
     * Load data files
     */
    public void loadData(){
        loadStationData();
        UI.println("Loaded Stations");
        loadTrainLineData();
        UI.println("Loaded Train Lines");
        loadTrainServicesData();
        UI.println("Loaded Train Services");
        UI.println("Please state all stations and lines as capital letters");
        loadedData = true;
    }

    /**
     * User interface has buttons for the queries and text fields to enter stations and train line
     * You will need to implement the methods here.
     */
    public void setupGUI(){
        UI.addButton("All Stations",        this::listAllStations);
        UI.addButton("Stations by name",    this::listStationsByName);
        UI.addButton("All Lines",           this::listAllTrainLines);
        UI.addTextField("Station",          (String name) -> {this.stationName=name;});
        UI.addTextField("Train Line",       (String name) -> {this.lineName=name;});
        UI.addTextField("Destination",      (String name) -> {this.destinationName=name;});
        UI.addTextField("Time (24hr)",      (String time) ->
        {try{this.startTime=Integer.parseInt(time);}catch(Exception e){UI.println("Enter four digits");}});
        UI.addButton("Lines of Station",    () -> {listLinesOfStation(this.stationName);});
        UI.addButton("Stations on Line",    () -> {listStationsOnLine(this.lineName);});
        UI.addButton("Stations connected?", () -> {checkConnected(this.stationName, this.destinationName);});
        UI.addButton("Next Services",       () -> {findNextService(this.stationName, this.startTime);});
        //UI.addButton("NS",       () -> {nS(this.stationName, this.startTime);});
        UI.addButton("Find Trip",           () -> {findTrip(this.stationName, this.destinationName, this.startTime);});

        UI.addButton("Quit", UI::quit);
        UI.setMouseListener(this::doMouse);

        UI.setWindowSize(900, 400);
        UI.setDivider(0.2);
        
        if (! loadedData){
            UI.setFontSize(36);
            UI.drawString("Start the program from main", 2, 36);
            UI.drawString("in order to load the data", 2, 80);
            UI.sleep(2000);
            UI.quit();
        }
        else {
            UI.drawImage("data/geographic-map.png", 0, 0);
            UI.drawString("Click to list closest stations", 2, 12);
        }
    }

    /**
     * Mouse method to click anywhere on map and find the 10 closest stations
     * Uses mouse listener methods within the setupGUI
     */
    public void doMouse(String action, double x, double y){
        if(action.equals("released")){
            UI.clearText();
            Map<Double, Station> closeStations = new TreeMap<Double, Station>();
            List<Double> distances = new ArrayList<Double>();
            for (Station s: stations.values()){
                double statX = s.getXCoord();
                double statY = s.getYCoord();
                double lgthX = Math.abs(x - statX);
                double lgthY = Math.abs(y - statY);
                double distance = Math.hypot(lgthX, lgthY);
                closeStations.put(distance, s);
                distances.add(distance);
            }
            Collections.sort(distances);
            for(int i = 0; i < 10; i++){
                double currDist = distances.get(i);
                double currDistKm = currDist/10;
                double roundDistance = Math.round(currDistKm * 100.0) / 100.0;
                Station currentStation = closeStations.get(currDist);
                String stationName = currentStation.getName();
                UI.printf( "%.2f  kms:%s %n" , currDistKm, stationName ); // currentDistanceKms
            }
        }
    }
    
    /**
     * Loads data from stations data into a map of the stations
     * stations data contains; station name, zone and x,y positions
     * Constructs new station object each line
     */
    public void loadStationData(){
        UI.clearText();
        try{
            List<String> allLines = Files.readAllLines(Path.of("data/stations.data"));
            for(String line: allLines){
                Scanner sc = new Scanner(line);
                String name = sc.next();
                int zone = sc.nextInt();
                double xPos = sc.nextInt();
                double yPos = sc.nextInt();
                stations.put(name, new Station(name, zone, xPos, yPos));
            }
        }catch (IOException e) { UI.printf("Failed to open");}
    }

    /**
     * Train Line data consisting of stations and area information
     */
    public void loadTrainLineData(){
        UI.clearText();
        try{
            List<String> allLines = Files.readAllLines(Path.of("data/train-lines.data"));
            for(String L : allLines){
                Scanner sc = new Scanner(L);
                String name = sc.next();
                TrainLine trainLine = new TrainLine(name);
                trainLines.put(name, trainLine);
                List<String> statNames = Files.readAllLines(Path.of("data/"+ name +"-stations.data"));
                for(String eachStat : statNames){
                    Scanner scan = new Scanner(eachStat);
                    String nameStat = scan.next();
                    Station newStat = stations.get(nameStat);
                    trainLine.addStation(newStat);
                    newStat.addTrainLine(trainLine);
                }
            }
            UI.println(trainLines);
        }catch (IOException e) { UI.printf("Failed to open");}
    }

    /**
     *  * Train Line data consisting of stations, schedule and zones
     */
    public void loadTrainServicesData(){
        for(Map.Entry<String, TrainLine>currentTrainLineEntry : trainLines.entrySet()){
        TrainLine cTrainLine = currentTrainLineEntry.getValue();
        String specificTrainServicesDataFileName = "data/" + cTrainLine.getName() + "-services.data";
        try{
            Scanner trainLineServiceScanner = new Scanner(Path.of(specificTrainServicesDataFileName));
            while (trainLineServiceScanner.hasNext()){
                TrainService rTrainService = new TrainService(cTrainLine);
                cTrainLine.addTrainService(rTrainService);
                String stringTimeSequence = trainLineServiceScanner.nextLine();
                String[] splitStringTimeSequence = stringTimeSequence.split(" ");
                for (int i = 0; i < splitStringTimeSequence.length; i++){
                    rTrainService.addTime(Integer.parseInt(splitStringTimeSequence[i]));
                }
            }
            }catch (IOException e) { UI.printf("Failed to open");}
        }
    }

    /**
     * List of all stations in the Wellington region
     */
    public void listAllStations(){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        try{
            UI.println("All Stations in region:");
            UI.println("                          ");
            List<String> allLines = Files.readAllLines(Path.of("data/stations.data"));
            for(String L: allLines){
                Scanner sc = new Scanner(L);
                String name = sc.next();
                UI.println(stations.get(name));
            }
        }catch (IOException e) { UI.printf("Failed to open");}
    }

    /**
     * List of all the stations in alphabetic order in the Wellington region
     */
    public void listStationsByName(){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        try{
           UI.println("All Stations in alphabetical order:");
           UI.println("                          ");
           List<String> allLines = Files.readAllLines(Path.of("data/stations.data"));
           List<String> namesInOrder = new ArrayList<>();
           for(String L: allLines){
                Scanner sc = new Scanner(L);
                String name = sc.next();
                namesInOrder.add(name);
           }
           Collections.sort(namesInOrder);
           for(String a : namesInOrder){
            UI.println(stations.get(a));
           }
        }catch (IOException e) { UI.printf("Failed to open");}
    }

    /** 
     * List of all the train lines traveling out into the region
     */
    public void listAllTrainLines(){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        try{
        UI.println("All Train Lines in region:");
        UI.println("                          ");
        List<String> allLines = Files.readAllLines(Path.of("data/train-lines.data"));
            for(String L: allLines){
                Scanner sc = new Scanner(L);
                String name = sc.next();
                UI.println(trainLines.get(name));
        }
        }catch (IOException e) { UI.printf("Failed to open");}
    }

    /**
     * List train lines that go through given station
     */
    public void listLinesOfStation(String stationName){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        Station stat = stations.get(stationName);
        UI.println("Lines that go throught this station:");
        UI.println("                          ");
        for(TrainLine b: stat.getTrainLines()){
            UI.println(b);
        }
    }
    
    /**
     * List the stations along a given train line
     */
    public void listStationsOnLine(String trainLineName){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        TrainLine trainline = trainLines.get(lineName);
        UI.println("All Stations within this Train Line:");
        UI.println("                          ");
        for(Station station : trainline.getStations()){
            UI.println(station.getName());
        }
    }

    //EVERYTHING AFTER THIS IS ALL TO MAKE findTRIP AND CHALLENGE TO WORK
    //IT IS A REVAMP OF PREVIOUS METHODS WITH ALOT OF ALTERCATIONS AND PAIN
    
     /**
     * Return of method connected
     * Print name of the train line that goes from a 
       station to a destination on the same line
     */
    public void checkConnected(String stationName, String destinationName){
        String name = Connected(stationName, destinationName);
        UI.println("The Station are Connected through the "+ name +" line .");
        }
    
    /**
     * Method for checkConnected
     */
    public String Connected(String stationName, String destinationName){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        String name = "does not exist";
        if((stationName != null) && (destinationName!= null)){
            int cStart = 0;
            int cEnd = 0;
            for(TrainLine b: trainLines.values()){
                boolean s = false;
                boolean e = false;
                List<Station> stat = b.getStations();
                for(Station a: stat){
                    if(stationName.equalsIgnoreCase(a.getName())){
                        s = true;
                        cStart = stat.indexOf(a);
                    }
                    if(destinationName.equalsIgnoreCase(a.getName())){
                        e = true;
                        cEnd = stat.indexOf(a);
                    }
                }
                if((s == true) && (e == true)){
                    if(cStart < cEnd){
                        name = b.getName();
                    }
                }
            }
        }
        return name;
    }

    /**
     * method for findNextServices
     */
    public void findNextService(String stationName, int startTime){
        UI.clearText();
        UI.println("Please state all stations and lines as capital letters");
        int nFastest = 0;
        if((stationName != null)&&(startTime != -1)){
            for(TrainLine nextTrain : trainLines.values()){
                int nTime = 0;
                List<Station> station = nextTrain.getStations();
                int indexes = 0;
                for(Station a: station){
                    if(a.getName().equals(stationName)){
                        int index = station.indexOf(a);
                        List<TrainService> nextService = nextTrain.getTrainServices();
                        int difference = 0;
                        int previousDifference = 10000;
                        for(TrainService c: nextService){
                            int nextServiceTime = c.getTimes().get(index);
                            if(nextServiceTime > startTime){
                                difference = (nextServiceTime-startTime);
                                if(difference < previousDifference){
                                    nTime = nextServiceTime;
                                    indexes = nextService.indexOf(c);
                                }
                                previousDifference = difference;
                            }
                        }
                    }
                }
                if(nTime != 0){
                    //nFastest = nTime;
                    nextTrainName = nextTrain.getName();
                    indexLine = indexes;
                    UI.println("The next service for the "+ nextTrainName + " leaves at " + nTime +".");
                }
            }
        }
        //return nFastest;
    }
    
    /**
     * Finds journey from given stations and time
     */
    public void findTrip(String stationName, String destinationName, int startTime){
        String name = Connected(stationName, destinationName);
        int nFastest = 0;
        int indexLine = 0;
        if((stationName != null)&&(startTime != -1)){
            for(TrainLine nextTrain : trainLines.values()){
                if(name.equals(nextTrain.getName())){
                int nTime = 0;
                List<Station> station  =nextTrain.getStations();
                int indexes = 0;
                for(Station a: station){
                    if(a.getName().equals(stationName)){
                        int index = station.indexOf(a);
                        List<TrainService> nextService = nextTrain.getTrainServices();
                        int difference = 0;
                        int previousDifference = 10000;
                        for(TrainService c: nextService){
                            int nextServiceTime = c.getTimes().get(index);
                            if(nextServiceTime > startTime){
                                difference = (nextServiceTime-startTime);
                                if(difference < previousDifference){
                                    nTime = nextServiceTime;
                                    indexes = nextService.indexOf(c);
                                }
                                previousDifference = difference;
                            }
                        }
                    }
                }
                if(nTime != 0){
                    nFastest = nTime;
                    nextTrainName = nextTrain.getName();
                    indexLine = indexes;
                }
                }
            }
        }
        int finalTime = 0;
        for(TrainLine nextTrain : trainLines.values()){
            if(name.equals(nextTrain.getName())){
                List<TrainService> nextService = nextTrain.getTrainServices();
                TrainService destinationTime = nextService.get(indexLine);
                List<Station> station = nextTrain.getStations();
                int index = 0;
                //int zone = Math.abs((stations.get(destinationName).getZone()) - (stations.get(stationName).getZone())) + 1;
                for(Station a: station){
                    if(a.getName().equals(destinationName)){
                        index = station.indexOf(a);
                    }
                }
                finalTime = destinationTime.getTimes().get(index);
                
            }
        }
        
        if(finalTime != 0){
            UI.println("The next service from " + stationName + " to " +destinationName+" on the "+ name+" line leaves "+stationName);
            UI.println("at " + nFastest+ ", then arrives " + destinationName + " at " + finalTime + ".");
        }
    }
    
    //CHALLENGE DIDNT WORK HAVE GOT ALL SET UP WITHIN THE CONNECT AND FIND SERVICE METHODS JUST STRUGGLED WITH LAYOUT. 
    //WOULD LOVE FEEDBACK AS HAVE DEFO OVERCOMPLICATED
    
}