package org.xiaohuahua.can;

import static org.junit.Assert.*;

import java.rmi.RemoteException;

import org.junit.Test;
import org.xiaohuahua.can.NodeImpl;

public class NodeTest {

	@Test
	public void testSetGetZone() throws RemoteException {
		NodeImpl n = new NodeImpl("mynode");
		Zone zone1 = new Zone(1, 2, 3, 4);
		n.setZone(zone1);
		Zone zone2 = n.getZone();
		assertEquals(zone1, zone2);
	}

}
