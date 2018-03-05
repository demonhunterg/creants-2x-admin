package com.creants.v2.admin.handlers.requests;

import com.creants.creants_2x.core.annotations.Instantiation;
import com.creants.creants_2x.core.exception.QAntException;
import com.creants.creants_2x.core.extension.IQAntExtension;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;

/**
 * @author LamHM
 *
 */
@Instantiation(Instantiation.InstantiationMode.SINGLE_INSTANCE)
public class GameMasterReqHandler extends BaseAdminModuleReqHandler {
	public static final String MODULE_ID = "GameMaster";
	private static final String COMMANDS_PREFIX = "game_master";


	public GameMasterReqHandler() {
		super(COMMANDS_PREFIX, MODULE_ID);
	}


	@Override
	protected void handleAdminRequest(QAntUser user, IQAntObject params) {
		String cmd = params.getUtfString("__[[REQUEST_ID]]__");
		QAntTracer.debug(this.getClass(), "Request received >> " + cmd);
		try {
			IQAntObject data = params.getQAntObject("data");
			String zoneName = data.getUtfString("zn");
			IQAntExtension extension = qantServer.getZoneManager().getZoneByName(zoneName).getExtension();
			extension.handleClientRequest(getFullCommand(cmd), user, data);
		} catch (QAntException e) {
			e.printStackTrace();
		}
	}

}
