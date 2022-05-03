package com.automationanywhere.botcommand.commands;

import com.automationanywhere.botcommand.exception.BotCommandException;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;

import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.TEXT;
import static com.automationanywhere.commandsdk.model.DataType.STRING;


@BotCommand
@CommandPkg(label = "End Session",
        name = "EndHypatosSession",
        description = "Ends Hypatos session",
        icon = "hypatos.svg",
        node_label = "End Session {{sessionName}}",
        group_label = "Session",
        comment = true,
        text_color =  "#0c2a7a"
        )

public class Admin2_EndSession {

    @Sessions
    private Map<String, Object> sessions;

    @Execute
    public void end(@Idx(index = "1", type = TEXT) @Pkg(label = "Session name", default_value_type = STRING, default_value = "Default") @NotEmpty String sessionName){
        if (!sessions.containsKey(sessionName))
            throw new BotCommandException("Session not found with session name : "+sessionName);
        sessions.remove(sessionName);
    }
    public void setSessions(Map<String, Object> sessions) {
        this.sessions = sessions;
    }

}
