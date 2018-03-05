package com.creants.v2.admin.handlers.requests;

import java.util.List;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.extension.BaseClientRequestHandler;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
public abstract class BaseAdminModuleReqHandler extends BaseClientRequestHandler {
	protected QAntServer qantServer;
	private String commandPrefix;
	private String moduleId;


	public BaseAdminModuleReqHandler(String commandPrefix, String moduleName) {
		this.qantServer = QAntServer.getInstance();
		this.commandPrefix = commandPrefix;
		this.moduleId = moduleName;
	}


	public String getCommandPrefix() {
		return this.commandPrefix;
	}


	public String getModuleId() {
		return this.moduleId;
	}


	public final void handleClientRequest(QAntUser sender, IQAntObject params) {
		this.handleAdminRequest(sender, params);
	}


	protected abstract void handleAdminRequest(QAntUser user, IQAntObject params);


	protected void sendUnexpectedError(String message, QAntUser recipient) {
		// getParentExtension().sendUnexpectedError(message, recipient);
	}


	protected String getFullCommand(String cmd) {
		return commandPrefix + "." + cmd;
	}


	protected void sendResponse(String cmd, QAntUser recipient) {
		send(getFullCommand(cmd), QAntObject.newInstance(), recipient);
	}


	protected void sendResponse(String cmd, IQAntObject params, QAntUser recipient) {
		send(getFullCommand(cmd), params, recipient);
	}


	protected void sendResponse(String cmd, IQAntObject params, List<QAntUser> recipients) {
		send(getFullCommand(cmd), params, recipients);
	}


	protected void sendResponse(final String cmd, final List<QAntUser> recipients) {
		send(getFullCommand(cmd), QAntObject.newInstance(), recipients);
	}
}
