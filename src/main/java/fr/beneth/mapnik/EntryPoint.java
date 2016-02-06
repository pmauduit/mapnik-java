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
        // Some java black magic to load the interned jar
        URL mapnikJniJar = EntryPoint.class.getResource("/lib/mapnik-jni.jar");
        URLClassLoader child = new URLClassLoader(new URL[] { mapnikJniJar }, EntryPoint.class.getClassLoader());
        Class.forName("mapnik.Mapnik", true, child);

        Mapnik.initialize();

        Box2d bounds = new Box2d(5.91478, 45.56237, 5.92904, 45.56921);
        MapDefinition m = new MapDefinition();

        URL myMapFile = EntryPoint.class.getResource("/data/simplemap.xml");
        // This should be done by the initialize() call, but ...
        FreetypeEngine.registerFonts(Mapnik.getInstalledFontsDir(), true);

        m.loadMap(new File(myMapFile.toURI()).getAbsolutePath(), false);
        m.setSrs(Projection.LATLNG_PARAMS);
        m.resize(512, 512);
        m.zoomToBox(bounds);

        Image image = new Image(512, 512);
        Renderer.renderAgg(m, image);

        byte[] contents = image.saveToMemory("png");
        image.dispose();
        m.dispose();

        FileOutputStream fos = new FileOutputStream("chambery.png");
        fos.write(contents);
        fos.flush();
        fos.close();
    }

}
