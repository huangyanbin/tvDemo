package com.palmwifi.view.loadingballs.factory;

import android.graphics.Path;
import android.graphics.Point;

import com.palmwifi.view.loadingballs.factory.path.Circle;
import com.palmwifi.view.loadingballs.factory.path.Diamond;
import com.palmwifi.view.loadingballs.factory.path.Infinite;
import com.palmwifi.view.loadingballs.factory.path.Square;
import com.palmwifi.view.loadingballs.factory.path.Star;
import com.palmwifi.view.loadingballs.factory.path.Triangle;

/**
 * @author Adrián García Lomas
 */
public class PathFactory {

  public static final String CIRCLE = "circle";
  public static final String SQUARE = "square";
  public static final String INFINITE = "infinite";
  public static final String STAR = "star";
  public static final String TRIANGLE = "triangle";
  public static final String DIAMOND = "diamond";

  public static Path makePath(String path, Point center, int pathWidth, int pathHeight,
      int maxBallSize) {

    switch (path) {
      case CIRCLE:
        return new Circle(center, pathWidth, pathHeight, maxBallSize).draw();
      case SQUARE:
        return new Square(center, pathWidth, pathHeight, maxBallSize).draw();
      case INFINITE:
        return new Infinite(center, pathWidth, pathHeight, maxBallSize).draw();
      case STAR:
        return new Star(center, pathWidth, pathHeight, maxBallSize).draw();
      case TRIANGLE:
        return new Triangle(center, pathWidth, pathHeight, maxBallSize).draw();
      case DIAMOND:
        return new Diamond(center, pathWidth, pathHeight, maxBallSize).draw();
      default:
        return new Infinite(center, pathWidth, pathHeight, maxBallSize).draw();
    }
  }
}

