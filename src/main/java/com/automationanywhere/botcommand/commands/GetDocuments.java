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
import com.automationanywhere.commandsdk.annotations.rules.EntryList.EntryListLabel;
import com.automationanywhere.commandsdk.annotations.rules.GreaterThan;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.commandsdk.annotations.rules.NumberInteger;
import com.automationanywhere.commandsdk.model.AttributeType;
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
        name = "GetDocuments",
        label = "Get documents",
        node_label = "Retrieve list of documents from project ID {{projectID}}",
        group_label = "Document API",
        description = "Retrieve a list of documents in a project",
        icon = "hypatos.svg",
        comment = true,
        text_color =  "#0c2a7a",
        multiple_returns = {
                @CommandPkg.Returns(return_label="Assign output to a JSON String",
                        return_name = "JSONString",
                        return_description = "Select String variable to store JSON response as string",
                        return_type = STRING),
                @CommandPkg.Returns(return_label="Assign output to a list of dictionary",
                        return_name = "DictionaryValue",
                        return_description = "Select Dictionary variable to store response",
                        return_type =DICTIONARY)
        })

public class GetDocuments {
    @Sessions
    private Map<String, Object> sessionMap;

    @Idx(index = "4.3", type = TEXT, name="NAME")
    @Pkg(label = "Name", default_value_type = DataType.STRING)
    @NotEmpty
    private String name;

    @Idx(index = "4.4", type = TEXT, name="VALUE")
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

            @Idx(index = "4", type = ENTRYLIST, options = {
                    @Idx.Option(index = "4.1", pkg = @Pkg(title = "NAME", label = "Key")),
                    @Idx.Option(index = "4.2", pkg = @Pkg(title = "VALUE", label = "Value")),
            })
            //Label you see at the top of the control
            @Pkg(label = "Filter result based on document properties", description = "Use this action to filter records based on document properties, such as state,lastModifiedBefore,lastModifiedAfter" +
                    "e.g. key: state, value: done")
            //Header of the entry form
            @EntryListLabel(value = "Provide entry")
            //Button label which displays the entry form
            @EntryListAddButtonLabel(value = "Add entry")
            //Uniqueness rule for the column, this value is the TITLE of the column requiring uniqueness.
            //@EntryListEntryUnique(value = "NAME")
            //Message to display in table when no entries are present.
            @EntryListEmptyLabel(value = "No entries added, only documents in 'done' state will be fetched")
            List<Value> values,

            @Idx(index = "5", type = AttributeType.NUMBER) @Pkg(label = "Limit", default_value_type = DataType.NUMBER,default_value = "1000",
                    description = "Limits the total number of records returned")
                  @GreaterThan("0") @NumberInteger Double limit


    ) throws IOException {

        try {
            if (!sessionMap.containsKey(sessionName))
                throw new BotCommandException("Session not found with session name : "+sessionName);
            HypatosServer hypatosServer = (HypatosServer) this.sessionMap.get(sessionName);
            String token = hypatosServer.getToken();
            String url = hypatosServer.getURL();
            String sLimit="";

            if (limit!=null){
                int iLimit = limit.intValue();
                sLimit = Integer.toString(iLimit);
            }

            String response = HypatosActions.getDocuments(url,token,apiVersion,projectID,values,sLimit);
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
