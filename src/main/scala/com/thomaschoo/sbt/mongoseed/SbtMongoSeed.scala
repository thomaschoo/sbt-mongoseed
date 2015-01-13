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

    mongoSeedDev <<= seedDb("dev"),
    mongoSeedTest <<= seedDb("test"),
    mongoSeedProd <<= seedDb("prod")
  )

  def getMongoUri = Def.setting {
    import com.typesafe.config.ConfigFactory
    val file = baseDirectory.value / "conf" / "application.conf"
    val conf = ConfigFactory.parseFile(file).getConfig("mongodb").resolve()
    conf.getString("uri")
  }

  def seedDb(env: String) = Def.task {
    import com.mongodb.BasicDBObject
    import com.typesafe.config.Config

    def seedDb(): Unit = {
      import com.typesafe.config.ConfigFactory
      import scala.collection.JavaConversions._

      val db = connect(getMongoUri.value)
      getFiles foreach { f =>
        val coll = db(f.getName.split('.').head)
        val entries = ConfigFactory.parseFile(f)
        entries.getConfigList("inserts") foreach { x =>
          coll.insert(getDbObject(x))
        }
      }
    }

    def connect(uri: String): MongoDB = {
      import com.mongodb.casbah.Imports._

      val mongoUri = MongoClientURI(uri)
      val mongoClient = MongoClient(mongoUri)
      (mongoUri.database map { dbName =>
        mongoClient(dbName)
      }).get
    }

    def getFiles: Seq[File] = (mongoSeedDir.value / env * "*.conf").get

    def getDbObject(conf: Config): BasicDBObject = {
      import com.mongodb.util._
      import com.typesafe.config.ConfigRenderOptions

      val json = conf.root().render(ConfigRenderOptions.concise())
      JSON.parse(json).asInstanceOf[BasicDBObject]
    }

    seedDb()
  }
}
