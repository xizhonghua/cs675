package org.xiaohuahua.can;

import static org.junit.Assert.*;

import org.junit.Test;
import org.xiaohuahua.can.Node;

public class NodeTest {

	@Test
	public void testSetGetZone() {
		Node n = new Node("mynode");
		Zone zone1 = new Zone(1, 2, 3, 4);
		n.setZone(zone1);
		Zone zone2 = n.getZone();
		assertEquals(zone1, zone2);
	}

}
