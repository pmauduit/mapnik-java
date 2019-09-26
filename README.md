# Purpose

This is a simple tileserver based on Spring and the libmapnik JNI bindings.

# Requirements

The `mapnik-jni` bindings for mapnik 3 will be needed. The original authors of
Mapnik do not maintain such a tool, but there is a one available here:

```
 git@github.com:jlnr/mapnik-jni.git
```

Once cloned, the repository will require the `libmapnik3.0-dev` package. Then a
simple `make` will result in the `build/dist/libmapnik-jni.so` file to be
generated.

Copy this `libmapnik-jni.so` to `/usr/lib/jni/` directory, the
`libmapnik-jni.jar` into `src/main/resources` of this repository, then you
should be done with the mapnik bindings setup.

# Data

Using `osm2pgsql`, load a postgis database, then edit the file `simplemap.xml`,
the postgresql configuration around line 140 to adapt to your setup.


# Launch

```
$ mvn jetty:run
```

Then a minimalist webserver should be available at
`http://localhost:8080/demo`.

# Results

![Chambéry](https://raw.githubusercontent.com/pmauduit/mapnik-java/master/chambery.png)

# Caveats

This is not much than a Proof-of-Concept ! Do not expect to use it in
production, as there is no caching of the generated tiles, which can take ~ 1
minute on my computer to render ...

