package core.game.Colliders;

import org.geotools.geometry.jts.GeometryBuilder;

import core.game.PrimitiveShape;
import glm_.vec2.Vec2;




public class Sphere extends PrimitiveShape {
    public Sphere(int x, int y)
    {
        super(new Vec2(x, y));
        GeometryBuilder gb = new GeometryBuilder();
        org.locationtech.jts.geom.Polygon circle;
        
        circle = gb.circle(0, 0, 1.0, 6);
        
    
    }
}
