package org.xiaohuahua.can.util;

import java.awt.Point;

import org.xiaohuahua.can.Config;

public class HashUtil {
	
	
	/**
	 * Compute the coordinate of a keyword in virtual space
	 * @param keyword
	 * @return
	 */
	public static Point getCoordinate(String keyword) {
		int x = 0;
		int y = 0;
		for(int i=0;i<keyword.length();i+=2)
			x += keyword.charAt(i);
		for(int i=1;i<keyword.length();i+=2)
			y += keyword.charAt(i);
		
		x %= Config.LENGTH;
		y %= Config.LENGTH;
		
		Point p = new Point(x, y);
		
		return p;
	}
}
