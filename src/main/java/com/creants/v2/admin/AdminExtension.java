package com.creants.v2.admin;

import com.creants.creants_2x.QAntServer;
import com.creants.creants_2x.core.QAntEventType;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.extension.QAntExtension;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.v2.admin.handlers.events.LoginEvtHandler;
import com.creants.v2.admin.handlers.requests.DashboardModuleReqHandler;
import com.creants.v2.admin.handlers.requests.GameMasterReqHandler;

/**
 * @author LamHM
 *
 */
public class AdminExtension extends QAntExtension {
	public static final String ADMIN_ROOM_NAME = "AdminRoom";
	public static final String RESP_ERROR = "error";


	@Override
	public void init() {
		QAntTracer.debug(this.getClass(), "Admin Extension started");
		Zone zone = QAntServer.getInstance().getZoneManager().getZoneByName("--=={{{ AdminZone }}}==--");
		System.out.println("[DEBUG]" + zone);
		addEventRequestHandler();
	}


	private void addEventRequestHandler() {
		addEventHandler(QAntEventType.USER_LOGIN, LoginEvtHandler.class);
		addRequestHandler("dashboard", DashboardModuleReqHandler.class);
		addRequestHandler("game_master", GameMasterReqHandler.class);
	}

}
