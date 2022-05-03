package com.automationanywhere.botcommand.commands;

import com.automationanywhere.botcommand.Utils.HypatosActions;
import com.automationanywhere.botcommand.Utils.HypatosServer;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.FileFolder;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "DownloadDocumentByID",
        label = "Download document by ID",
        node_label = "Download documents with document ID {{documentID}}",
        group_label = "Document API",
        description = "Download document by its document ID",
        icon = "hypatos.svg",
        comment = true,
        text_color =  "#0c2a7a",
        return_label = "Select variable to store saved file path",
        return_type = STRING
)

public class DownloadDocumentByID {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public Value<String> action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,

            @Idx(index = "2", type = TEXT) @Pkg(label = "API version", default_value_type = STRING, default_value = "v1")
            @NotEmpty String apiVersion,

            @Idx(index = "3", type = TEXT) @Pkg(label = "Project ID", default_value_type = STRING ,description = "The project identifier where the documents are stored")
            @NotEmpty String projectID,

            @Idx(index = "4", type = TEXT) @Pkg(label = "Document ID", default_value_type = STRING ,description = "The identifier of the document to retrieve")
            @NotEmpty String documentID,

            @Idx(index = "5", type = TEXT)
            @Pkg(label = "Enter folder path to download")
            @NotEmpty
            @FileFolder
                    String folderPath,

            @Idx(index = "6", type = TEXT)
            @Pkg(label = "Enter filename with extension",description = "this filename will be used only if response does not provide it's own filename")
            @NotEmpty
                    String fileName


    ) throws IOException {

        try {
            if (!sessionMap.containsKey(sessionName))
                throw new BotCommandException("Session not found with session name : "+sessionName);
            HypatosServer hypatosServer = (HypatosServer) this.sessionMap.get(sessionName);
            String token = hypatosServer.getToken();
            String url = hypatosServer.getURL();

            folderPath= Paths.get(folderPath).toString();
            String downloadFilePath = HypatosActions.downloadDocumentByID(url,apiVersion,token,projectID,documentID,
                    folderPath,fileName);
            return new StringValue(downloadFilePath);

        } catch(Exception e){
            throw new BotCommandException("Error during request. Exception caught: " + e);
        }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
