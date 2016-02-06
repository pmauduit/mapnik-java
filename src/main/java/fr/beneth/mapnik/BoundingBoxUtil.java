package fr.beneth.mapnik;

/**
 * Bounding box utility calculation class, stolen/adapted from
 * http://wiki.openstreetmap.org/wiki/Slippy_map_tilenames#Tile_bounding_box
 *
 */
public class BoundingBoxUtil {

    double north = 0.0;
    double south = 0.0;
    double east = 0.0;
    double west = 0.0;

    public static BoundingBoxUtil tile2boundingBox(final int x, final int y,
            final int zoom) {
        BoundingBoxUtil bb = new BoundingBoxUtil();
        bb.north = tile2lat(y, zoom);
        bb.south = tile2lat(y + 1, zoom);
        bb.west = tile2lon(x, zoom);
        bb.east = tile2lon(x + 1, zoom);
        return bb;
    }

    public static double tile2lon(int x, int z) {
        return x / Math.pow(2.0, z) * 360.0 - 180;
    }

    public static double tile2lat(int y, int z) {
        double n = Math.PI - (2.0 * Math.PI * y) / Math.pow(2.0, z);
        return Math.toDegrees(Math.atan(Math.sinh(n)));
    }
}
