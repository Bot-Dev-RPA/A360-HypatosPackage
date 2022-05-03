/*
 * Copyright (c) 2020 Automation Anywhere.
 * All rights reserved.
 *
 * This software is the proprietary information of Automation Anywhere.
 * You shall use it only in accordance with the terms of the license agreement
 * you entered into with Automation Anywhere.
 */

package com.automationanywhere.botcommand.commands;

import com.automationanywhere.botcommand.Utils.HypatosServer;
import com.automationanywhere.commandsdk.annotations.BotCommand;
import com.automationanywhere.commandsdk.annotations.*;
import com.automationanywhere.commandsdk.annotations.rules.NotEmpty;
import com.automationanywhere.core.security.SecureString;

import java.util.Base64;
import java.util.Map;

import static com.automationanywhere.commandsdk.model.AttributeType.*;
import static com.automationanywhere.commandsdk.model.DataType.STRING;

@BotCommand
@CommandPkg(label = "Start Session",
		description = "Starts Hypatos Session with Credentials",
		icon = "hypatos.svg",
		name = "startHypatosSession",
		node_label = "Start Session {{sessionName}}",
		group_label="Session",
		comment = true,
		text_color =  "#0c2a7a"
		)
public class Admin1_StartSession {

	@Sessions
	private Map<String, Object> sessionMap;

	@Execute
	public void execute(
			@Idx(index = "1", type = TEXT)
			@Pkg(label = "Start Session", default_value_type = STRING, default_value = "Default")
			@NotEmpty String name,
			@Idx(index = "2", type = TEXT) @Pkg(label = "Hypatos URL", description = "e.g. https://api.studio.hypatos.ai/") @NotEmpty String url,
			@Idx(index = "3", type = CREDENTIAL) @Pkg(label = "User Name" ,description = "application_id from Hypatos Api Setting") @NotEmpty SecureString username,
			@Idx(index = "4", type = CREDENTIAL) @Pkg(label = "Password",description = "application_key from Hypatos Api Setting") @NotEmpty SecureString password,
			@Idx(index = "5", type = HELP)@Pkg(label = "Note",description = "This action does not check for correctness of user and pass, only maps to use with actions of particular session") String help
	) {

		String ins_username = username.getInsecureString();
		String ins_password = password.getInsecureString();
		url = url.replaceAll("/*$", "") + "/";
		String token = Base64.getEncoder().encodeToString((ins_username+":"+ins_password).getBytes());
		if (!sessionMap.containsKey(name))
		{
			HypatosServer hypatosServer = new HypatosServer(url, token ,ins_username);
			sessionMap.put(name, hypatosServer);
		}

	}

	public void setSessionMap(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}

}
