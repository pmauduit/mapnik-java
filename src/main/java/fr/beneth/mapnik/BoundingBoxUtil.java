package fr.beneth.mapnik;

import org.geotools.geometry.jts.ReferencedEnvelope;
import org.geotools.referencing.CRS;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.geometry.BoundingBox;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.TransformException;

/**
 * Bounding box utility calculation class, stolen/adapted from
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Tile_bounding_box
 *
 */
public class BoundingBoxUtil {

    private double north = 0.0;
    private double south = 0.0;
    private double east = 0.0;
    private double west = 0.0;

    public static BoundingBoxUtil tile2boundingBox(final int x, final int y,
            final int zoom) {
        BoundingBoxUtil bb = new BoundingBoxUtil();
        bb.north = tile2lat(y, zoom);
        bb.south = tile2lat(y + 1, zoom);
        bb.west = tile2lon(x, zoom);
        bb.east = tile2lon(x + 1, zoom);
        return bb;
    }

    public BoundingBoxUtil(double minx, double miny, double maxx, double maxy) {
        west = minx; south = miny; east = maxx; north = maxy;
    }
    
    public BoundingBoxUtil() {}

    public static BoundingBoxUtil reprojectFromWgsToGoog(BoundingBoxUtil u) throws TransformException, FactoryException {
        ReferencedEnvelope envelope = new ReferencedEnvelope(u.west, u.east,
                u.south, u.north, DefaultGeographicCRS.WGS84);
        CoordinateReferenceSystem nproj = CRS.decode("EPSG:3857");
        BoundingBox newb = envelope.toBounds(nproj);
        return new BoundingBoxUtil(newb.getMinX(), newb.getMinY(),
                newb.getMaxX(), newb.getMaxY());
    }
    
    public static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    public static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }

    public double getNorth() {
        return north;
    }

    public double getSouth() {
        return south;
    }

    public double getEast() {
        return east;
    }

    public double getWest() {
        return west;
    }
}
