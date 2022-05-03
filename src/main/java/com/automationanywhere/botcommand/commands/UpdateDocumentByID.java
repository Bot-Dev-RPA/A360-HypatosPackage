package com.automationanywhere.botcommand.commands;

import com.automationanywhere.botcommand.Utils.HypatosActions;
import com.automationanywhere.botcommand.Utils.HypatosServer;
import com.automationanywhere.botcommand.Utils.JSONUtils;
import com.automationanywhere.botcommand.data.Value;
import com.automationanywhere.botcommand.data.impl.DictionaryValue;
import com.automationanywhere.botcommand.data.impl.StringValue;
import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListAddButtonLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEmptyLabel;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListEntryUnique;
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.model.DataType;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.ENTRYLIST;
import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.DICTIONARY;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand
@CommandPkg(
        //Unique name inside a package and label to display.
        name = "UpdateDocumentByID",
        label = "Update document by ID",
        node_label = "Update detail of document with document ID {{projectID}}",
        group_label = "Document API",
        description = "Update details of document by its document ID ",
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

public class UpdateDocumentByID {
    @Sessions
    private Map<String, Object> sessionMap;
    @Idx(index = "5.3", type = TEXT, name="NAME")
    @Pkg(label = "Name", default_value_type = DataType.STRING)
    @NotEmpty
    private String name;

    @Idx(index = "5.4", type = TEXT, name="VALUE")
    @Pkg(label = "Value", default_value_type = DataType.STRING)
    private String value;

    @Execute
    public DictionaryValue action(
            @Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default")
            @NotEmpty String sessionName,

            @Idx(index = "2", type = TEXT) @Pkg(label = "API version", default_value_type = STRING, default_value = "v1")
            @NotEmpty String apiVersion,

            @Idx(index = "3", type = TEXT) @Pkg(label = "Project ID", default_value_type = STRING ,description = "The project identifier where the documents are stored")
            @NotEmpty String projectID,
            @Idx(index = "4", type = TEXT) @Pkg(label = "Document ID", default_value_type = STRING ,description = "The identifier of the document to update")
            @NotEmpty String documentID,

            @Idx(index = "5", type = ENTRYLIST, options = {
                    @Idx.Option(index = "5.1", pkg = @Pkg(title = "NAME", label = "Hypatos document key")),
                    @Idx.Option(index = "5.2", pkg = @Pkg(title = "VALUE", label = "Value")),
            })
            //Label you see at the top of the control
            @Pkg(label = "Update specific document properties", description = "Use this action to update document properties, such as state,failureReason,fileName" +
                    "e.g. key: state, value: transferFailed")
            //Header of the entry form
            @EntryListLabel(value = "Provide entry")
            //Button label which displays the entry form
            @EntryListAddButtonLabel(value = "Add entry")
            //Uniqueness rule for the column, this value is the TITLE of the column requiring uniqueness.
            @EntryListEntryUnique(value = "NAME")
            //Message to display in table when no entries are present.
            @EntryListEmptyLabel(value = "No value to update")
            @NotEmpty List<Value> values

    ) throws IOException {

        try {
            if (!sessionMap.containsKey(sessionName))
                throw new BotCommandException("Session not found with session name : "+sessionName);

            HypatosServer hypatosServer = (HypatosServer) this.sessionMap.get(sessionName);
            String token = hypatosServer.getToken();
            String url = hypatosServer.getURL();

            String response = HypatosActions.updateDocument(url,token,apiVersion,projectID,documentID,values);
            JSONObject json_resp = new JSONObject(response);

            JSONUtils parser = new JSONUtils();
            DictionaryValue map = parser.parseJSONObj(json_resp);

            Map<String, Value> returnValue = new HashMap<>();
            returnValue.put("JSONString", new StringValue(response));
            returnValue.put("DictionaryValue", map);

            return new DictionaryValue(returnValue);

        } catch(Exception e){
            throw new BotCommandException("Error during request.Exception caught: " + e);
        }

    }
    public void setSessionMap(Map<String, Object> sessionMap) {
        this.sessionMap = sessionMap;
    }
}
