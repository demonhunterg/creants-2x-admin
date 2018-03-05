package com.creants.v2.admin.handlers.requests;

import java.lang.management.ManagementFactory;
import java.util.List;

import com.creants.creants_2x.core.annotations.Instantiation;
import com.creants.creants_2x.core.entities.Zone;
import com.creants.creants_2x.core.util.QAntTracer;
import com.creants.creants_2x.socket.gate.entities.IQAntArray;
import com.creants.creants_2x.socket.gate.entities.IQAntObject;
import com.creants.creants_2x.socket.gate.entities.QAntArray;
import com.creants.creants_2x.socket.gate.entities.QAntObject;
import com.creants.creants_2x.socket.gate.wood.QAntUser;
import com.creants.v2.admin.utils.PerformanceMonitor;

/**
 * @author LamHM
 *
 */
@Instantiation(Instantiation.InstantiationMode.SINGLE_INSTANCE)
public class DashboardModuleReqHandler extends BaseAdminModuleReqHandler {
	public static final String MODULE_ID = "Dashboard";
	private static final String COMMANDS_PREFIX = "dashboard";
	private static final String REQ_GET_DATA = "getData";
	private static final String REQ_THREAD_TEST = "test";
	private static final String RESP_DATA = "data";
	private PerformanceMonitor cpuMonitor;


	public DashboardModuleReqHandler() {
		super(COMMANDS_PREFIX, MODULE_ID);
		this.cpuMonitor = new PerformanceMonitor();
	}


	@Override
	protected void handleAdminRequest(QAntUser user, IQAntObject params) {
		String cmd = params.getUtfString("__[[REQUEST_ID]]__");
		IQAntObject outParams = QAntObject.newInstance();
		QAntTracer.debug(this.getClass(), "Request received >> " + cmd);
		if (cmd.equals(REQ_GET_DATA)) {
			outParams.putDouble("pCpu", roundToDecimals(this.cpuMonitor.getProcessCpuLoad() * 100.0, 2));
			outParams.putDouble("sCpu", roundToDecimals(this.cpuMonitor.getSystemCpuLoad() * 100.0, 2));
			outParams.putLong("freeMem", Runtime.getRuntime().freeMemory());
			outParams.putLong("maxMem", Runtime.getRuntime().maxMemory());
			outParams.putLong("totalMem", Runtime.getRuntime().totalMemory());
			IQAntArray threadsList = QAntArray.newInstance();
			long totalThreadsCpuTime = 0L;
			long[] threadIds = ManagementFactory.getThreadMXBean().getAllThreadIds();
			for (int i = 0; i < threadIds.length; ++i) {
				long id = threadIds[i];
				IQAntObject thread = QAntObject.newInstance();
				thread.putLong("id", id);
				thread.putUtfString("name", ManagementFactory.getThreadMXBean().getThreadInfo(id).getThreadName());
				long cpuTime = 0L;
				if (ManagementFactory.getThreadMXBean().isThreadCpuTimeSupported()
						&& ManagementFactory.getThreadMXBean().isThreadCpuTimeEnabled()) {
					cpuTime = ManagementFactory.getThreadMXBean().getThreadCpuTime(id);
					totalThreadsCpuTime += cpuTime;
				}
				thread.putLong("cpu", cpuTime);
				threadsList.addQAntObject(thread);
			}
			// outParams.putQAntArray("thList", threadsList);
			outParams.putLong("thCpu", totalThreadsCpuTime);
			outParams.putInt("ccuMax", qantServer.getUserManager().getHighestCCU());
			List<Zone> zones = qantServer.getZoneManager().getZoneList();
			int totalRooms = 0;
			int gameRooms = 0;
			for (int z = 0; z < zones.size(); ++z) {
				totalRooms += zones.get(z).getTotalRoomCount();
				gameRooms += zones.get(z).getGameRoomCount();
			}
			outParams.putInt("rTotal", totalRooms);
			outParams.putInt("rGame", gameRooms);
			sendResponse(RESP_DATA, outParams, user);
		} else if (cmd.equals(REQ_THREAD_TEST)) {
			int iterations = 47;
			if (params.containsKey("i")) {
				iterations = params.getInt("i");
			}
			Thread t = new Thread(new Fibo(iterations), "Fibonacci-" + iterations);
			t.start();
		}
	}


	private static double roundToDecimals(final double d, final int c) {
		int temp = (int) (d * Math.pow(10.0, c));
		return temp / Math.pow(10.0, c);
	}

}
