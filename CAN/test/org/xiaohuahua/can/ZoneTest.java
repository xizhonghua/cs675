package org.xiaohuahua.can;

import static org.junit.Assert.*;

import org.junit.Test;

public class ZoneTest {
  @Test
  public void testConstructor() {
    Zone zone = new Zone();
    assertSame(0, zone.x);
    assertSame(0, zone.y);
    assertSame(0, zone.width);
    assertSame(0, zone.height);
  }

  @Test
  public void testConstructorWithParameters() {
    Zone zone = new Zone(1, 2, 3, 4);
    assertSame(1, zone.x);
    assertSame(2, zone.y);
    assertSame(3, zone.width);
    assertSame(4, zone.height);
  }

  @Test
  public void testSplitSquare() {

    Zone zone = new Zone(0, 0, 10, 10);

    Zone splitZone = zone.split();

    assertEquals(new Zone(0, 0, 5, 10), zone);
    assertEquals(new Zone(5, 0, 5, 10), splitZone);
  }

  @Test
  public void testSplitRectangle() {

    Zone zone = new Zone(0, 0, 5, 10);

    Zone splitZone = zone.split();

    assertEquals(new Zone(0, 0, 5, 5), zone);
    assertEquals(new Zone(0, 5, 5, 5), splitZone);
  }

  @Test
  public void testMergeHorizontal() {
    Zone zone1 = new Zone(0, 0, 5, 10);
    Zone zone2 = new Zone(5, 0, 5, 10);
    zone1.merge(zone2);

    assertEquals(new Zone(0, 0, 10, 10), zone1);
  }

  @Test
  public void testMergeVertical() {
    Zone zone1 = new Zone(0, 0, 10, 5);
    Zone zone2 = new Zone(0, 5, 10, 5);
    zone1.merge(zone2);

    assertEquals(new Zone(0, 0, 10, 10), zone1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeNonadjacent() {
    Zone zone1 = new Zone(0, 0, 5, 10);
    Zone zone2 = new Zone(5, 10, 5, 10);
    zone1.merge(zone2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeDifferentWidth() {
    Zone zone1 = new Zone(0, 0, 5, 10);
    Zone zone2 = new Zone(0, 5, 2, 10);
    zone1.merge(zone2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testMergeDifferentHeight() {
    Zone zone1 = new Zone(0, 0, 5, 10);
    Zone zone2 = new Zone(0, 5, 5, 2);
    zone1.merge(zone2);
  }

}
