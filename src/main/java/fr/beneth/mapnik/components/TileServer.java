package fr.beneth.mapnik.components;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import fr.beneth.mapnik.BoundingBoxUtil;
import fr.beneth.mapnik.EntryPoint;
import mapnik.Box2d;
import mapnik.FreetypeEngine;
import mapnik.Image;
import mapnik.MapDefinition;
import mapnik.Mapnik;
import mapnik.Projection;
import mapnik.Renderer;

@Controller
public class TileServer {

    private final int TILE_SIZE = 256;
    private boolean mapnikAvailable = false;
    private static Logger logger = Logger.getLogger(TileServer.class);

    public TileServer() {
        // All the logic here is not really safe ... in fact, a loadLibrary
        // which fails might disturb previous loads.
        try {
            Class.forName("mapnik.Mapnik");
        } catch (ClassNotFoundException e) {
            logger.error("Unable to load Mapnik Java bindings.", e);
        }
        try {
            Mapnik.initialize();
            FreetypeEngine.registerFonts(Mapnik.getInstalledFontsDir(), true);
        } catch (Throwable e) {
            // if "already loaded in another classloader", then consider
            // everything went well ...
            if (e.getMessage().contains("already loaded in another classloader")) {
                logger.info("Library has already been loaded");
            } else {
                logger.error("unable to load the native lib", e);
                return;
            }
        }
        mapnikAvailable = true;
    }
    @RequestMapping("/tile/{z}/{x}/{y}.png")
    @ResponseBody
    public byte[] getTile(@PathVariable int z, @PathVariable int x, @PathVariable int y,
            HttpServletResponse response) throws Exception {
        response.setContentType("image/png");

        if (! mapnikAvailable) {
            return null;
        }
        BoundingBoxUtil bb = BoundingBoxUtil.tile2boundingBox(x, y, z);

        logger.debug(String.format("before proj: %f,%f,%f,%f",
                bb.getEast(), bb.getSouth(), bb.getWest(), bb.getNorth()));

        bb = BoundingBoxUtil.reprojectFromWgsToGoog(bb);

        logger.debug(String.format("after proj: %f,%f,%f,%f",
                bb.getEast(), bb.getSouth(), bb.getWest(), bb.getNorth()));

        Box2d bounds = new Box2d(bb.getWest(), bb.getSouth(), bb.getEast(), bb.getNorth());
        MapDefinition m = new MapDefinition();

        URL myMapFile = EntryPoint.class.getResource("/data/simplemap.xml");
        m.loadMap(new File(myMapFile.toURI()).getAbsolutePath(), false);
        m.setSrs(Projection.SRS900913_PARAMS);
        m.resize(TILE_SIZE, TILE_SIZE);
        m.zoomToBox(bounds);
        Image image = new Image(TILE_SIZE, TILE_SIZE);
        Renderer.renderAgg(m, image);

        byte[] contents = image.saveToMemory("png");
        image.dispose();
        m.dispose();

        return contents;
    }
    @RequestMapping("/demo")
    @ResponseBody
    public byte[] demo(HttpServletResponse response)
        throws Exception {
        response.setContentType("text/html; charset=utf-8");
        URL sampleHtml = this.getClass().getResource("/sample.html");
        return Files.readAllBytes(Paths.get(sampleHtml.toURI()));
    }   
}
