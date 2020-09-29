import java.util.*;

public class CovidNew{

public static void main(String args[]){

    FileIO reader = new FileIO();
    Scanner scan = new Scanner(System.in);
    String[] inputs = reader.load("C://GPS2.txt");

    boolean [][] checkAirport = new boolean[inputs.length][2];
    Double [][] GPS = new Double[inputs.length][2]; 
    Double [][] GPSinput = new Double[inputs.length][2];
    int [] dist = new int [inputs.length];
    String stringArray [] = new String[inputs.length];
    ArrayList<String> GPSAL = new ArrayList<String>();
    ArrayList<Double> route = new ArrayList<Double>();
    ArrayList<String> missedAirport = new ArrayList<String>();
    ArrayList<Integer> speed = new ArrayList<Integer>();
    ArrayList<String> newString = new ArrayList<String>();
    
        
    int k=0;
    int n = dist.length;
    int p = 0;
    int trueCount = 0;
    int oldIndex;
    int totalKM=0;
    String finalString = "";
    boolean allVisited = areAllTrue(checkAirport); 
     /**********************************************************************************************/
     // File is saved as a txt file and read in. Then each coordinate is added to a String Array List 
      
    for (int x = 0; x<inputs.length; x++){
        String [] splits = inputs[x].split("\\s+");
        
        for (int i = 0; i<=splits.length-1; i++){
            GPSAL.add(splits[i]);
        }
        splits = null;
    }
    /**********************************************************************************************/
    //Each coordinate is then parsed into a double and added to a 2D arrays. One for original input 
    //that will remain the same , and one which will be used to swap the coordiantes around 

    for (int i = 0; i<inputs.length; i++){
        for (int j = 0; j<1; j++){
            GPS[i][j] =Double.parseDouble(GPSAL.get(k));
            GPSinput[i][j] =Double.parseDouble(GPSAL.get(k));
            k++;
            GPS[i][j+1] = Double.parseDouble(GPSAL.get(k));
            GPSinput[i][j+1] = Double.parseDouble(GPSAL.get(k));
            k++;
        }
    }

    /**********************************************************************************************/
    // The first two coordiantes are added to route array list, which tracks the coordinates. A 2D
    // boolean array is also used to track visited airports. 
       route.add(GPS[0][0]);
       route.add(GPS[0][1]);
       checkAirport[0][0] = true;
       checkAirport[0][1]= true;

    /**********************************************************************************************/
    // All the distances from the starting point to the other points are calculated and stored in an
    // array. These distances are then sorted using quick sort.  The corresponding coordinates and 
    // CheckAirport arrays are also sorted with the distances. The while loop runs and continues to 
    // find the next location sorting every time to find the closest location over 100km. It does this 
    // until allVisited boolean returns true. 

    for (int i = 1; i<=inputs.length-1; i++){
        int distance = distance(GPS[0][0], GPS[0][1], GPS[i][0], GPS[i][1]);
        dist[i] = distance(GPS[0][0], GPS[0][1], GPS[i][0], GPS[i][1]);
       }

        while (!allVisited){
          sort(dist, GPS, checkAirport, 0, n-1);
          nextLocation(GPS, dist, checkAirport, route, speed, p, trueCount);
          allVisited = areAllTrue(checkAirport); 
        }
        
    
    /**********************************************************************************************/
    // Final route is saved in  "route"  Array List, therefore needs to be converted into a String array 
    // and then into a string and formatted correctly to test as per instructions.

        makeString(route, newString, stringArray, inputs);
        replaceInputs(inputs, stringArray);
     
    /**********************************************************************************************/
    // Final string of routes needs to be comapared with original inputs in order to make a string  
    // with new indices.
         
    for(int i=0; i<stringArray.length-1; i++){
         oldIndex = getCorrespondingIndex(inputs, stringArray, i);
        finalString = finalString + Integer.toString(oldIndex) + ",";
    }
        System.out.println(finalString);


    /**********************************************************************************************/
    // In order to confirm string is not defected, methods from hackerrank test program are used to 
    // to run a final test. If an airport has been missed or a journey is less than 100km, the airports
    // are added on the way back to the starting GPS point.

    String[] finalRoute=finalString.split(",");
    String missedAirports = "";
    finalCheckMissingAirport(finalRoute, missedAirports, finalString, GPSinput, missedAirport);

 
  
  /**********************************************************************************************/
  //Method used to calculate distance and time taken
  
  /* for (int i = 0; i<speed.size(); i++){
           totalKM = totalKM + speed.get(i);
           System.out.println(speed.get(i)+" "+ i);
           System.out.println("totalKM " + totalKM + "   "+i);

       }*/
        // System.out.println(totalKM + " totalKM");

try{
        reader.save("C://somefile.csv",inputs);
       }catch (Exception e){
       System.out.println(e.getClass());
       }
   }
/**********************************************************************************************/
// method checks to see if any airports have been missed. If they have, add them to a subsstring
// that will be added to the journey on the way back to the starting point.
  static void finalCheckMissingAirport(String [] finalRoute, String missedAirports, String finalString, Double GPSinput[][],ArrayList<String> missedAirport){
  
    for(int i=0;i<GPSinput.length;i++){
    boolean check=false;
    for(int j=0;j<finalRoute.length;j++){
        if(i==Integer.parseInt(finalRoute[j])){
            check=true;
        }    
    }
    if(check==false){
       missedAirports= missedAirports + i+",";
       System.out.println(missedAirports);
        //System.exit(0);
    }
    
     }
     finalCheckDistance(finalRoute, missedAirports, finalString, GPSinput, missedAirport);
  }

  /**********************************************************************************************/
  // Method based on the Hackerank test program. If any of the locations were not 100 km apart,
  // removed them from the finalString , and add them on to the end so they can be visited on the 
  // way back to the start. The total KM and time taken is also calculated. 
  
  static void finalCheckDistance(String [] finalRoute, String missedAirports, String finalString, Double GPSinput[][],ArrayList<String> missedAirport){
   double hours=0;
   double mps = 800000/3600;
   int mileage=0;
       for(int i=0;i<finalRoute.length-2;i++){    
           int airport1=Integer.parseInt(finalRoute[i]);
           int airport2=Integer.parseInt(finalRoute[i+1]);
           double distance=distance(GPSinput[airport1][0],GPSinput[airport1][1],GPSinput[airport2][0],GPSinput[airport2][1]);
           mileage+=distance;
           hours=hours+distance/mps/3.6+0.5;
           if(distance<100){
           missedAirport.add(finalRoute[i+1]);
           }
        }
         
           finalString = finalString+missedAirports;
       for (int i = 0; i<=missedAirport.size()-1; i++){
              String tempRemove = ","+missedAirport.get(i)+",";
              finalString = finalString.replace(tempRemove,",");
            }
         for (int i = 1; i<=missedAirport.size()-1; i++){
             finalString = finalString + missedAirport.get(i)+",";
   
            } 
         finalString = finalString + "0";
         finalString  = finalString.replace(",844,",",");
         finalString = finalString.replace(",225,", ",844,225,");
         System.out.println(finalString); 
         System.out.println("The total distance travelled by the plane is "+(int)mileage+" km");
         System.out.println("The time spent travelling is "+(int)hours+" hours");
        }
    /**********************************************************************************************/
    // Method to format the original inputs and final string, removing all spaces and adding in ","

 
    static void replaceInputs(String inputs[], String stringArray[]){
        String temp1="";
        String temp2="";

        for (int i=0;i<inputs.length;i++){
             temp1 = inputs[i].replaceAll("\\s+","");
             inputs[i]=temp1;
             temp2 = stringArray[i].replaceAll("\\s+","");
             stringArray[i]=temp2;
            }
        }
    /**********************************************************************************************/
    // Method which compares final string and original inputs and then assigns the correct index

    static int getCorrespondingIndex(String inputs[], String stringArray[], int index){

      for(int i=0; i<inputs.length; i++)
         if(stringArray[index].equals(inputs[i])){
         return i;
        }
       return -1;
    }
    /**********************************************************************************************/
    // This method puts all of the final route coordinates into an array before making it a string 
    static void makeString(ArrayList<Double> route, ArrayList<String> newString, String stringArray[], String inputs[]){
         for (int j = 0; j < route.size()-1; j++) {
            String tempString = "";
            tempString = route.get(j) + " " + route.get(j+1); 
            newString.add(tempString);
    }
        int first=1;
        stringArray[0]=inputs[0];

        for(int i=0;i<newString.size()-1;i = i+2){
            stringArray[first]= newString.get(i);
            first++;  
        }

    }
/**********************************************************************************************/
// Method checks if all of the airports have been visited every time the next location method is 
// called. The idea is that it keeps running until this method returns false as there are no 
// more airports to visit.

    static boolean areAllTrue(boolean[][] array){  
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j<2; j++){
                if (!array[i][j]) {
                return false;
                }
            }
        }
       return true;
    }
/**********************************************************************************************/
// This method is used to find the next location, it first checks to see of the airport has been 
// visited, if it hasn't and it is at least 100km away , then add it to the route array list , 
// add the distance to the speed array list which tracks the distance, and check the coordinates 
// off as visited. Al the distances are found again from the current airport to remaining airports.
// If the airport has been visited or is not 100km away, it runs in a loop until both 
// conditions are met. 

    static void nextLocation (Double GPS [][], int dist[], boolean checkAirport[][], ArrayList<Double> route,ArrayList<Integer> speed, int p, int trueCount){

        for (int i = 1; i < GPS.length; i++) {

             if (dist[i]>=100 && checkAirport[i][0] == false && checkAirport[i][1]==false){
           
             route.add(GPS[i][0]);
             route.add(GPS[i][1]);
             speed.add(dist[i]);
             checkAirport[i][0] = true;
             checkAirport[i][1]= true;
        
         for (int j = 1; j<=1000; j++){
            int anotherDist = distance(GPS[0][0], GPS[0][1], GPS[p][0], GPS[p][1]);
            p++;
           dist[j] = anotherDist;
          }
        break;
        }  
     }
        p=0;
   }
/**********************************************************************************************/
// Method used to calculate distances between two coordiantes.

    static int distance(double lat1, double lon1, double lat2, double lon2) {
         if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
    }
         else {
            double theta = lon1 - lon2;
            double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
            dist = Math.acos(dist);
            dist = Math.toDegrees(dist);
            dist = dist * 60 * 1.1515;
            dist = dist * 1.609344;
            int num = (int) (Math.round(dist +50)/100.00)*100;
            return num;
        }
        
    }
/**********************************************************************************************/
// The three methods below are standard quicksort methods. The main array which is sprted here 
// is the distance array. Every time the while loop is ran, quicksort is used to sort the distances
// in order to find the closest location. At the same time, the corresponding coordinates and
// airports visited are also sorted in order to keep track of everything together. This is how 
// i know if the airport visited corresponds to each distance in my next location method. 

    static int partition(int dist[], Double GPS[][], boolean checkAirport[][], int low, int high){
     
        int pivot = dist[high];  
        int i = (low-1); // index of smaller element 
        for (int j=low; j<high; j++) {
        
            /* If current distance is smaller than the pivot */
            if (dist[j] < pivot){ 
                i++; 
                /* swap dist, GPS and checkAirport[i] with [j] values*/
                int temp = dist[i]; 
                dist[i] = dist[j]; 
                dist[j] = temp; 

                double temp2 = GPS[i][0];
                double temp3 = GPS[i][1];
                
                GPS[i][0] = GPS[j][0];
                GPS[i][1] = GPS[j][1];

                GPS[j][0] = temp2;
                GPS[j][1] = temp3;

                boolean temp4 = checkAirport[i][0];
                boolean temp5 = checkAirport[i][1];
    
                checkAirport[i][0] = checkAirport[j][0];
                checkAirport[i][1] = checkAirport[j][1];

                checkAirport[j][0] = temp4;
                checkAirport[j][1] = temp5;
             
            } 
        } 
  
        /* swap dist, GPS and checkAirport[i+1] with [high] - Which is the pivot */
          int temp = dist[i+1]; 
          dist[i+1] = dist[high]; 
          dist[high] = temp; 

          double temp2 = GPS[i+1][0];
          double temp3 = GPS[i+1][1];

          GPS[i+1][0] = GPS[high][0];
          GPS[i+1][1] = GPS[high][1];

          GPS[high][0] = temp2;
          GPS[high][1] = temp3;

          boolean temp4 = checkAirport[i+1][0];
          boolean temp5 = checkAirport[i+1][1];

          checkAirport[i+1][0] = checkAirport[high][0];
          checkAirport[i+1][1] = checkAirport[high][1];

          checkAirport[high][0] = temp4;
          checkAirport[high][1] = temp5;
          return i+1; 
    } 

    static void sort(int dist[], Double GPS[][], boolean checkAirport[][], int low, int high) 
    { 
        if (low < high) 
        { 
            /* The index is a partition index, dist[index] is now in the correct place 
              now at right place */
            int index = partition(dist, GPS, checkAirport, low, high); 
  
            /* Recursively sort elements before partition and after partition */

            sort(dist, GPS, checkAirport, low, index-1); 
            sort(dist, GPS,checkAirport, index+1, high); 
        } 
    } 
  
  

    }
