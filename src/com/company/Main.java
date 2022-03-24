// -----------------------------------------------------
// Assignment 3
// Written by: Nicholas Pop 40210550 and Alessio Cipriano-Kardous 40210549
// -----------------------------------------------------
/*
This class has 3 main methods, the CSVAttributeMissing, CSVDataMissing and the ConvertCSVtoHTML methods. The CSVAttributeMissing and
 CSVDataMissing are exception methods. The ConvertCSVtoHTML takes a string of CSV file names, and transform their information into
 an html file. It will then ask the user for what html file to see and then display its content after the file name is checked.
 */

package com.company;

import java.io.*;
import java.util.Scanner;

//Exception CSVAttributeMissing
/**
 *  This returns a String with information on the Exception.
 * @return a String with the Exception message
 */
//CSVAttributeMissing Exception setup
class CSVAttributeMissing extends RuntimeException{
    public CSVAttributeMissing(String message){
        super(message);
    }
}

/**
 * Main represents the driver and the Converter method. It asks for the file name, transforms them into html files,
 * and then asks the user for which file they want to see, and displays it.
 *
 * @author Alessio Cipriano-Kardous
 * @author Nicholas Pop
 * @version 1.1
 */
public class Main {

    /**
     * This returns an html file with the information from the CSV file.
     *
     * @param listOfFiles String of all the file names
     * @return an html file with the information of the CSV file
     */
    //the ConvertCSVtoHTML method
    public static void ConvertCSVtoHTML(String listOfFiles){
        //initializes objects printwriter and scanner
        PrintWriter output = null;
        PrintWriter log = null;
        Scanner input = null;

        File logFile = new File("src\\Exception.log"); //initializes the exception file.

        //Appends log file
        if(logFile.exists() && logFile.canRead()){
            try {
                log = new PrintWriter(new FileOutputStream(logFile, true));
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }else{
            try {
                log = new PrintWriter(new FileOutputStream(logFile));
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }

        //Separate the names of files
        String[] arrayOfFileNames = listOfFiles.split(",");
        File[] svcArray = new File[arrayOfFileNames.length];
        for(int i = 0; i < arrayOfFileNames.length; i++){
            arrayOfFileNames[i] = "src\\" + arrayOfFileNames[i];
            svcArray[i] = new File(arrayOfFileNames[i]);
        }

        //setups the scanner object for the different files, setups the title, attributes, data and note
        for(File f : svcArray){
            try {
                input = new Scanner(new FileInputStream(f));
            } catch (FileNotFoundException e) {
                System.out.println(e.getMessage());
            }

            //Strings for title, attributes, attribute string array
            String title = input.nextLine();
            String attributes = input.nextLine();
            String[] attributesArray = attributes.split(",");

            //Checks if there are missing attributes and throws exception CSVAttributeMissing
            for(String att : attributesArray){
                if(att.equalsIgnoreCase("")){
                    log.println("ERROR: In file " + f.getName() + ". Missing attribute. File is not converted to HTML.");
                    log.close();
                    f.delete();
                    throw new CSVAttributeMissing("ERROR: In file " + f.getName() + ". Missing attribute. File is not converted to HTML.");
                }
            }

            String values = "";
            String note = "";

            title = title.substring(0, title.indexOf(',')); //substrings the title name

            //while loop that checks every line to see if it exists
            while(input.hasNextLine()){
                String text = input.nextLine();
                if (text.contains("Note:")){ //checks if note exists and adds it
                    note = text;
                    continue;
                }
                values = values + text + "\n";
            }

            //heading as a string
            String htmlHeading = "<!DOCTYPE html>\n" +
                    "<html>\n" +
                    "<style>\n" +
                    "table {font-family: arial, sans-serif;border-collapse: collapse;}\n" +
                    "td, th {border: 1px solid #000000;text-align: left;padding: 8px;}\n" +
                    "tr:nth-child(even) {background-color: #dddddd;}\n" +
                    "span{font-size: small}\n" +
                    "</style><body>";

            //adds title
            String htmlTableTitle = "<table><caption>" + title + "</caption>";
            String htmlAttributes = "\t<tr>";
            for(String att : attributesArray){ //adds attributes
                htmlAttributes = htmlAttributes + "\n\t\t<th>" + att + "</th>";
            }
            htmlAttributes = htmlAttributes + "\t</tr>";

            //checks if data is missing, throws exception, and adds the data in a table
            String htmlValues = "";
            int linecounter = 2;
            String[] tempStringArr = values.split("\n");
            String[] indiValues; //values of the data
            for(int i = 0; i < tempStringArr.length; i++){
                linecounter++;
                indiValues = tempStringArr[i].split(",");
                if(indiValues.length != 4 || indiValues[0].equalsIgnoreCase("") || indiValues[1].equalsIgnoreCase("") || indiValues[2].equalsIgnoreCase("")){
                    if(indiValues.length != 4){
                        log.println("WARNING: In file " + f + " line " + linecounter + " is not converted to HTML : missing data: " + attributesArray[3]);
                        continue;
                    } else if(indiValues[0].equalsIgnoreCase("")){
                        log.println("WARNING: In file " + f + " line " + linecounter + " is not converted to HTML : missing data: " + attributesArray[0]);
                        continue;
                    } else if(indiValues[1].equalsIgnoreCase("")){
                        log.println("WARNING: In file " + f + " line " + linecounter + " is not converted to HTML : missing data: " + attributesArray[1]);
                        continue;
                    } else if(indiValues[2].equalsIgnoreCase("")){
                        log.println("WARNING: In file " + f + " line " + linecounter + " is not converted to HTML : missing data: " + attributesArray[2]);
                        continue;
                    }
                }
                htmlValues = htmlValues + "\n\t<tr>";
                for(int j = 0; j < indiValues.length; j++){
                    htmlValues = htmlValues + "\n\t\t<th>" + indiValues[j] + "</th>";
                }
                htmlValues = htmlValues + "\n\t</tr>";
            }
            log.close(); //closes the log writer


            //adds the note
            String htmlNote = "</table>\n<span>" + note.substring(0, note.indexOf(',')) + "</span>\n</body>\n</html>";

            try {
                output = new PrintWriter(new FileOutputStream("src\\" + f.getName().substring(0, f.getName().indexOf('.')) + ".html"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            //prints out the heading, attributes, data and note.
            output.println(htmlHeading);
            output.println(htmlTableTitle);
            output.println(htmlAttributes);
            output.println(htmlValues);
            output.println(htmlNote);
            output.flush();
            output.close();
        }

    }

    //asks the user for the file names and applies the convertCSVtoHTML method.
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in); //to scan the file names
        System.out.print("What file(s) would you like to open: ");
        String listOfFiles = scan.nextLine();

        try {
            ConvertCSVtoHTML(listOfFiles);
        } catch (CSVAttributeMissing e) { //catches the CSVAttributeMissing exception
            System.out.println(e.getMessage());
            System.exit(0);
        }

        //Asks what html wants to be seen, checks if its valid, displays it using BufferedReader
        String[] splitoffiles = listOfFiles.split(",");
        System.out.print("What file would you like to see: ");
        String htmlfileopen = scan.nextLine();
        String[] htmlsplit = htmlfileopen.split("\\.");
        boolean condition = true;
        int count = 0;
        while(condition){
        for (int i = 0; i < splitoffiles.length; i++) {
            String[] tempi = splitoffiles[i].split("\\.");
            if (htmlfileopen.equals(splitoffiles[i]) || !(htmlsplit[0].equals(tempi[0]))) {
                if(count == 1){
                    System.out.println("Invalid, system will terminate.");
                    System.exit(0);
                }
                System.out.print("Invalid file name, please enter again (you have 1 more try: ");
                htmlfileopen = scan.nextLine();
                count++;
                break;
            } else {
                BufferedReader objReader = null;
                try {
                    objReader = new BufferedReader(new FileReader("src\\" + htmlfileopen));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                String strCurrentLine = "";
                while (true) {
                    try {
                        if (!((strCurrentLine = objReader.readLine()) != null)) {
                            condition = false;
                            break;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(strCurrentLine);
                    }
                }
            }
        }
    }
}
