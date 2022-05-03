package com.automationanywhere.botcommand.commands;

import com.automationanywhere.botcommand.Utils.HypatosActions;
import com.automationanywhere.botcommand.Utils.HypatosServer;
import com.automationanywhere.botcommand.Utils.JSONUtils;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.DICTIONARY;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "GetDocumentByID",
        label = "Get document by ID",
        node_label = "Retrieve detail of documents with document ID {{documentID}}",
        group_label = "Document API",
        description = "Retrieve detail of document by its document ID",
        icon = "hypatos.svg",
        comment = true,
        text_color =  "#0c2a7a",
        multiple_returns = {
                @CommandPkg.Returns(return_label="Assign output to a JSON String",
                        return_name = "JSONString",
                        return_description = "Select String variable to store JSON response as string",
                        return_type = STRING),
                @CommandPkg.Returns(return_label="Assign output to a dictionary",
                        return_name = "DictionaryValue",
                        return_description = "Select Dictionary variable to store response",
                        return_type = DICTIONARY)
        }
)

public class GetDocumentByID {
    @Sessions
    private Map<String, Object> sessionMap;

    @Execute
    public DictionaryValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,

            @Idx(index = "2", type = TEXT) @Pkg(label = "API version", default_value_type = STRING, default_value = "v1")
            @NotEmpty String apiVersion,

            @Idx(index = "3", type = TEXT) @Pkg(label = "Project ID", default_value_type = STRING ,description = "The project identifier where the documents are stored")
            @NotEmpty String projectID,

            @Idx(index = "4", type = TEXT) @Pkg(label = "Document ID", default_value_type = STRING ,description = "The identifier of the document to retrieve")
            @NotEmpty String documentID


    ) throws IOException {

        try {
            if (!sessionMap.containsKey(sessionName))
                throw new BotCommandException("Session not found with session name : "+sessionName);
            HypatosServer hypatosServer = (HypatosServer) this.sessionMap.get(sessionName);
            String token = hypatosServer.getToken();
            String url = hypatosServer.getURL();

            String response = HypatosActions.getDocumentByID(url,token,apiVersion,projectID,documentID);
            JSONObject json_resp = new JSONObject(response);

            JSONUtils parser = new JSONUtils();
            DictionaryValue map = parser.parseJSONObj(json_resp);

            Map<String, Value> returnValue = new HashMap<>();
            returnValue.put("JSONString", new StringValue(response));
            returnValue.put("DictionaryValue", map);

            return new DictionaryValue(returnValue);

        } catch(Exception e){
            throw new BotCommandException("Error during request. Exception caught: " + e);
        }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
