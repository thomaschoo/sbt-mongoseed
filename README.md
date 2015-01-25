sbt-mongoseed
=============
[sbt plugin] for seeding [MongoDB].

Plugin
======
Add the plugin to your `project/plugins.sbt` file:
```scala
addSbtPlugin("com.thomaschoo.sbt" % "sbt-mongoseed" % "1.0.0")
```

Add the [Sonatype releases] resolver:
```scala
resolvers += Resolver.sonatypeRepo("releases")
```

Enable the plugin in your project build file:
```scala
lazy val root = (project in file(".")).enablePlugins(SbtMongoSeed)
```

Configure
=========
Set the *MongoDB URI* setting. For example:
```scala
mongoSeedUri := "mongodb://localhost:27017/test"
```

Create your seed file(s). The file(s) must have the suffix ```.conf``` and be written using the [HOCON] format.

By default the plugin will look for your seed file(s) using the following [PathFinders]:

```scala
(baseDirectory.value / "conf" / "seeds" / "dev")
(baseDirectory.value / "conf" / "seeds" / "test")
(baseDirectory.value / "conf" / "seeds" / "prod")
```

Make sure the name of the file matches the collection you want seeded. For example:

`demo.conf` will seed into the *demo* collection

MongoDB
=======
The following are the supported MongoDB methods:

Methods     | .conf Keys  | Values
------------|-------------|-------
dropIndex   | dropIndexes | [{doc1},{doc2},...]
ensureIndex | indexes     | [{doc1},{doc2},...]
insert      | inserts     | [{doc1},{doc2},...]
remove      | removes     | [{doc1},{doc2},...]

If seed ordering is required, prepend numbers to your HOCON keys. For example `demo.conf`:

```JSON
2.inserts: [{"hello" : "world"}]

1.removes: [{}]
```

Usage
=====
```scala
sbt mongoSeedDev
```

```scala
sbt mongoSeedTest
```

```scala
sbt mongoSeedProd
```

Additional Settings
===================
The following *sbt-mongoseed* settings can be overridden with your own values:

Settings         | Default
-----------------|--------
mongoSeedUri     | will look for this key in (baseDirectory.value / "conf" / "application.conf")
mongoSeedDirDev  | (baseDirectory.value / "conf" / "seeds" / "dev")
mongoSeedDirTest | (baseDirectory.value / "conf" / "seeds" / "test")
mongoSeedDirProd | (baseDirectory.value / "conf" / "seeds" / "prod")

License
=======
This code is licensed under the [MIT License].

[sbt plugin]:http://www.scala-sbt.org/0.13/tutorial/Using-Plugins.html
[MongoDB]:http://www.mongodb.org/
[Sonatype releases]:https://oss.sonatype.org/content/repositories/releases/
[HOCON]:https://github.com/typesafehub/config/blob/master/HOCON.md
[Pathfinders]:http://www.scala-sbt.org/0.12.1/docs/Detailed-Topics/Paths.html#path-finders
[MIT License]:http://opensource.org/licenses/MIT
