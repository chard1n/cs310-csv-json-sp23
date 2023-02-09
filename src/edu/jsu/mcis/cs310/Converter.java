//Author: Jonathan Cade Hardin
//Class: CS 310 Software Engineering 1

package edu.jsu.mcis.cs310;

import com.github.cliftonlabs.json_simple.*;
import com.opencsv.*;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.io.Reader;
import java.text.DecimalFormat;


public class Converter {
    
    /*
        
        Consider the following CSV data, a portion of a database of episodes of
        the classic "Star Trek" television series:
        
        "ProdNum","Title","Season","Episode","Stardate","OriginalAirdate","RemasteredAirdate"
        "6149-02","Where No Man Has Gone Before","1","01","1312.4 - 1313.8","9/22/1966","1/20/2007"
        "6149-03","The Corbomite Maneuver","1","02","1512.2 - 1514.1","11/10/1966","12/9/2006"
        
        (For brevity, only the header row plus the first two episodes are shown
        in this sample.)
    
        The corresponding JSON data would be similar to the following; tabs and
        other whitespace have been added for clarity.  Note the curly braces,
        square brackets, and double-quotes!  These indicate which values should
        be encoded as strings and which values should be encoded as integers, as
        well as the overall structure of the data:
        
        {
            "ProdNums": [
                "6149-02",
                "6149-03"
            ],
            "ColHeadings": [
                "ProdNum",
                "Title",
                "Season",
                "Episode",
                "Stardate",
                "OriginalAirdate",
                "RemasteredAirdate"
            ],
            "Data": [
                [
                    "Where No Man Has Gone Before",
                    1,
                    1,
                    "1312.4 - 1313.8",
                    "9/22/1966",
                    "1/20/2007"
                ],
                [
                    "The Corbomite Maneuver",
                    1,
                    2,
                    "1512.2 - 1514.1",
                    "11/10/1966",
                    "12/9/2006"
                ]
            ]
        }
        
        Your task for this program is to complete the two conversion methods in
        this class, "csvToJson()" and "jsonToCsv()", so that the CSV data shown
        above can be converted to JSON format, and vice-versa.  Both methods
        should return the converted data as strings, but the strings do not need
        to include the newlines and whitespace shown in the examples; again,
        this whitespace has been added only for clarity.
        
        NOTE: YOU SHOULD NOT WRITE ANY CODE WHICH MANUALLY COMPOSES THE OUTPUT
        STRINGS!!!  Leave ALL string conversion to the two data conversion
        libraries we have discussed, OpenCSV and json-simple.  See the "Data
        Exchange" lecture notes for more details, including examples.
        
    */
    
    @SuppressWarnings("unchecked")
    public static String csvToJson(String csvString) {
        
        String result = "{}"; // default return value; replace later!
        
        try {
            
         //readers are initialized
            Reader read = new StringReader(csvString);
            CSVReader csvRead = new CSVReader(read);   
            
            
        //make arrays 
            JsonArray colHeadings = new JsonArray();
            JsonArray data = new JsonArray();
            JsonArray prod_nums = new JsonArray();
            
            
            //stores headings
            String[] row = csvRead.readNext();
            
            //new json object
            JsonObject obj = new JsonObject();
            
            
            for(String headings : row){
                colHeadings.add(headings);
            }
            
            //get the next row
            row = csvRead.readNext();
            
            //while loop storing the first column in prod_num if row exists
            while(row != null){
                prod_nums.add(row[0]);
                
                JsonArray insideData = new JsonArray();

                for(int j = 1; j < row.length; j++){
                    
                    if(j == colHeadings.indexOf("Season") || j == colHeadings.indexOf("Episode")){
                        insideData.add(Integer.valueOf(row[j]));
                    }
                    else{
                        insideData.add(row[j]);
                    }
                        
                }
                
                data.add(insideData);


                    //get the next row
                row = csvRead.readNext();
                
            }
            
                            
                obj.put("ProdNums", prod_nums);
                obj.put("ColHeadings", colHeadings);
                obj.put("Data", data);
                
                //print headings
                result = Jsoner.serialize(obj);
        }
            
        
        catch (Exception e) {
            e.printStackTrace();
        }

        return result.trim();
        
    }
    
    @SuppressWarnings("unchecked")
    public static String jsonToCsv(String jsonString) {
        
        String result = ""; // default return value; replace later!
        DecimalFormat decimal_format = new DecimalFormat("00");
        
        try {
            
            //deserialize json obj
            JsonObject jsonObj = Jsoner.deserialize(jsonString, new JsonObject());
            
            //get
            
            JsonArray product_num = new JsonArray();
            product_num = (JsonArray)(jsonObj.get("ProdNums"));
            
            JsonArray colheading = new JsonArray();
            colheading = (JsonArray)(jsonObj.get("ColHeadings"));
            
            JsonArray data = new JsonArray();
            data = (JsonArray)(jsonObj.get("Data"));
            
            StringWriter string_writer = new StringWriter();
            CSVWriter csvwriter = new CSVWriter(string_writer, ',', '"', '\\', "\n");
            
            //insert the headings
            String[] headings = new String[colheading.size()];
            for(int i = 0; i < colheading.size(); i++){
                headings[i] = colheading.getString(i).toString();
            }
            csvwriter.writeNext(headings);
            
            
            for(int i = 0; i < product_num.size(); i++){
                String[] row = new String[colheading.size()];
                JsonArray insideData = ((JsonArray)data.get(i));
                
                row[0] = product_num.get(i).toString();
                
                for(int j = 0; j < insideData.size(); j++){
                    if(j == colheading.indexOf("Episode")-1){
                        int num = Integer.parseInt(insideData.get(j).toString());
                        String formatted_num = "";
                        
                        formatted_num = decimal_format.format(num);
                        
                        row[j+1] = formatted_num;
                    }
                    else{
                        row[j+1] = insideData.get(j).toString();
                    }
                }            
                
                csvwriter.writeNext(row);
                
                
                
            }
            
            result = string_writer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        
        return result.trim();
        
    }
    
}
