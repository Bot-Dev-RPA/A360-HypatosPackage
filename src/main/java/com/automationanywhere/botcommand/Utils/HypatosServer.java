package com.automationanywhere.botcommand.Utils;

public class HypatosServer {


    public String getToken() {
        return Token;
    }

    public String getURL() {
        return Url;
    }

    public String getUserID() {
        return UserID;
    }

    String Token;
    String Url;
    String UserID;



    public HypatosServer(String Url, String Token, String UserID){
        this.Url = Url;
        this.Token = Token;
        this.UserID = UserID;
    }
}