package org.xiaohuahua.can.util;

import java.awt.Point;

import org.xiaohuahua.can.Config;

public class HashUtil {
	
	
	/**
	 * Compute the coordinate of a keyword in virtual space
	 * @param key
	 * @return
	 */
	public static Point getCoordinate(String key) {
		int x = 0;
		int y = 0;
		for(int i=0;i<key.length();i+=2)
			x += key.charAt(i);
		for(int i=1;i<key.length();i+=2)
			y += key.charAt(i);
		
		x %= Config.ZONE_SIZE;
		y %= Config.ZONE_SIZE;
		
		Point p = new Point(x, y);
		
		return p;
	}
}
