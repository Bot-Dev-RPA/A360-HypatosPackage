package com.automationanywhere.botcommand.Utils;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class HypatosActions {



    public static String getProjects(String url, String token,String apiVersion, String limit) throws Exception  {
        url = url + apiVersion +"/projects?limit=" + limit;


        String auth = "Basic " + token;
        String response = "";
        response = HTTPRequest.Request(url,  auth);
        return response;
    }

    public static String getDocuments(String url, String token,String apiVersion,String projectID,List<Value> list, String limit) throws Exception  {
        url = url + apiVersion +"/projects/"+projectID+"/documents?limit=" + limit;

        if(list!=null && list.size()>0){
            for (Value element : list){
                Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                String name = customValuesMap.containsKey("NAME") ? ((StringValue)customValuesMap.get("NAME")).get() : "";
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                if(value != null){
                    url =url +"&"+ URLEncoder.encode(name, StandardCharsets.UTF_8) +"="+URLEncoder.encode(value, StandardCharsets.UTF_8);
                }
            }
        }


        String auth = "Basic " + token;
        return HTTPRequest.Request(url, auth);
    }

    public static String getDocumentByID(String url, String token, String apiVersion, String projectID, String documentID) throws Exception  {
        url = url + apiVersion +"/projects/"+projectID+"/documents/" + documentID;

        String auth = "Basic " + token;
        return HTTPRequest.Request(url, auth);
    }

    public static String updateDocument(String url, String token,String apiVersion,String projectID,String documentID,List<Value> list) throws Exception {
        url = url + apiVersion +"/projects/"+projectID+"/documents/" + documentID;
        JSONObject jsonBody = new JSONObject();

        if(list!=null && list.size()>0){
            for (Value element : list){
                Map<String, Value> customValuesMap = ((DictionaryValue)element).get();
                String name = customValuesMap.containsKey("NAME") ? ((StringValue)customValuesMap.get("NAME")).get() : "";
                String value = (customValuesMap.getOrDefault("VALUE", null) == null) ? null : ((StringValue)customValuesMap.get("VALUE")).get();
                if(value != null){
                    jsonBody.put(name, value);
                }
            }
        }

        String auth = "Basic " + token;
        return HTTPRequest.httpPatch(url, auth, jsonBody);
    }

    public static String downloadDocumentByID(String url, String apiVersion, String token,
                                              String projectID, String documentID, String folderPath,
                                              String fileName) throws Exception  {
        String downloadURL = url + apiVersion +"/projects/"+projectID+"/documents/" + documentID+"/download";
        URL urlFormatted = new URL(downloadURL);
        HttpURLConnection httpConn = (HttpURLConnection)urlFormatted.openConnection();
        httpConn.setRequestProperty("Accept","*/*");
        httpConn.setRequestProperty("Authorization","Basic "+token);
        httpConn.setRequestProperty("Content-Type","application/json");

        int responseCode = httpConn.getResponseCode();
        if (responseCode != 200)
            throw new BotCommandException("ERROR HTTP code: " +httpConn.getResponseCode() +"Message: "+httpConn.getResponseMessage());

        String fileNameFromHeader="";
        String disposition = httpConn.getHeaderField("Content-Disposition");

        if (disposition != null && !disposition.isEmpty())
            fileNameFromHeader = ContentDispositionFileNameParser.parse(disposition);

        if(fileNameFromHeader!= null && !fileNameFromHeader.isEmpty())
            fileName = fileNameFromHeader;

        InputStream inputStream = httpConn.getInputStream();
        File directory = new File(folderPath);
        if (! directory.exists()){
            directory.mkdirs();
        }
        String downloadFilePath = Paths.get(folderPath+"\\"+fileName).toString();
        FileOutputStream outputStream = new FileOutputStream(downloadFilePath);

        byte[] buffer = new byte[4096];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        outputStream.close();
        inputStream.close();
        return  downloadFilePath;

    }
}