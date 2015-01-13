package com.thomaschoo.sbt.mongoseed

import sbt._
import Keys._

object SbtMongoSeed extends AutoPlugin {

  object autoImport {
    val mongoSeedDir = settingKey[File]("The directory for the seed files.")
    val mongoSeedUri = settingKey[String]("The MongoDB connection string.")
    val mongoSeedDev = taskKey[Unit]("Seeds mongodb for development")
    val mongoSeedTest = taskKey[Unit]("Seeds mongodb for test")
    val mongoSeedProd = taskKey[Unit]("Seeds mongodb for production")
  }

  import autoImport._
  import com.mongodb.casbah.MongoDB

  override def requires = sbt.plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] = Seq(
    mongoSeedDir := (baseDirectory.value / "conf" / "seeds"),

    mongoSeedUri <<= getMongoUri,

    mongoSeedDev <<= seedDb,
    mongoSeedTest <<= seedDb,
    mongoSeedProd <<= seedDb
  )

  def getMongoUri = Def.setting {
    import com.typesafe.config.ConfigFactory
    val file = baseDirectory.value / "conf" / "application.conf"
    val conf = ConfigFactory.parseFile(file).getConfig("mongodb").resolve()
    conf.getString("uri")
  }

  def seedDb = Def.task {
    import com.mongodb.BasicDBObject
    import com.mongodb.util._
    import com.typesafe.config.{ ConfigFactory, ConfigRenderOptions }
    import scala.collection.JavaConversions._

    def connect(uri: String): MongoDB = {
      import com.mongodb.casbah.Imports._
      val mongoUri = MongoClientURI(uri)
      val mongoClient = MongoClient(mongoUri)
      (mongoUri.database map { dbName =>
        mongoClient(dbName)
      }).get
    }

    val db = connect(mongoSeedUri.value)
    val seedFiles = (mongoSeedDir.value / "dev" * "*.conf").get
    seedFiles foreach { f =>
      val coll = db(f.getName.split('.')(0))
      val entries = ConfigFactory.parseFile(f)
      entries.getConfigList("inserts") foreach { x =>
        val json = x.root().render(ConfigRenderOptions.concise())
        val obj = JSON.parse(json).asInstanceOf[BasicDBObject]
        coll.insert(obj)
      }
    }
  }
}
