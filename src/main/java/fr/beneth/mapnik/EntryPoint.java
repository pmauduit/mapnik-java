package fr.beneth.mapnik;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;

import mapnik.Box2d;
import mapnik.FreetypeEngine;
import mapnik.Image;
import mapnik.MapDefinition;
import mapnik.Mapnik;
import mapnik.Projection;
import mapnik.Renderer;

public class EntryPoint {

    public static void main(String[] args) throws ClassNotFoundException, URISyntaxException, IOException {

        Mapnik.initialize();
        // France métropolitaine
        // -9.86 41.15
        // 10.38 51.56
        Box2d bounds = new Box2d(-9.86, 41.15, 10.38, 51.56);
        MapDefinition m = new MapDefinition();

        URL myMapFile = EntryPoint.class.getResource("/data/france.xml");
        // This should be done by the initialize() call, but ...
        FreetypeEngine.registerFonts(Mapnik.getInstalledFontsDir(), true);

        m.loadMap(new File(myMapFile.toURI()).getAbsolutePath(), false);
        m.setSrs(Projection.LATLNG_PARAMS);
        m.resize(4096, 4096);
        m.zoomToBox(bounds);

        Image image = new Image(4096, 4096);
        Renderer.renderAgg(m, image);

        byte[] contents = image.saveToMemory("png");
        image.dispose();
        m.dispose();

        FileOutputStream fos = new FileOutputStream("france.png");
        fos.write(contents);
        fos.flush();
        fos.close();
    }

}
