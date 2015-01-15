package com.thomaschoo.sbt.mongoseed

import sbt._
import Keys._

object SbtMongoSeed extends AutoPlugin {

  object autoImport {
    val mongoSeedUri = settingKey[String]("The MongoDB connection string.")
    val mongoSeedDirDev = settingKey[PathFinder]("The directory for the development seed files.")
    val mongoSeedDirTest = settingKey[PathFinder]("The directory for the test seed files.")
    val mongoSeedDirProd = settingKey[PathFinder]("The directory for the production seed files.")
    val mongoSeedDev = taskKey[Unit]("Seeds mongodb for development")
    val mongoSeedTest = taskKey[Unit]("Seeds mongodb for test")
    val mongoSeedProd = taskKey[Unit]("Seeds mongodb for production")
  }

  import autoImport._

  override def requires = sbt.plugins.JvmPlugin

  override def projectSettings: Seq[Setting[_]] = Seq(
    mongoSeedUri <<= getMongoUri,
    mongoSeedDirDev := (baseDirectory.value / "conf" / "seeds" / "dev"),
    mongoSeedDirTest := (baseDirectory.value / "conf" / "seeds" / "test"),
    mongoSeedDirProd := (baseDirectory.value / "conf" / "seeds" / "prod"),

    mongoSeedDev <<= seedDb(mongoSeedDirDev),
    mongoSeedTest <<= seedDb(mongoSeedDirTest),
    mongoSeedProd <<= seedDb(mongoSeedDirProd)
  )

  def getMongoUri = Def.setting {
    import com.typesafe.config.ConfigFactory
    ConfigFactory
      .parseFile(baseDirectory.value / "conf" / "application.conf")
      .getConfig("mongodb")
      .resolve()
      .getString("uri")
  }

  def seedDb(path: SettingKey[PathFinder]) = Def.task {
    import com.mongodb.BasicDBObject
    import com.mongodb.casbah.MongoDB
    import com.typesafe.config.Config

    def seedDb(): Unit = {
      import com.typesafe.config.ConfigFactory
      import scala.collection.JavaConversions._

      val db = connect()
      (path.value * "*.conf").get foreach { f =>
        val coll = db(f.getName.split('.').head)
        val entries = ConfigFactory.parseFile(f)
        entries.entrySet().toList sortBy (_.getKey) foreach { entry =>
          val action = entry.getKey.split('.').last
          entries.getConfigList(entry.getKey) foreach { x =>
            action match {
              case "indexes" => coll.ensureIndex(getDbObject(x))
              case "inserts" => coll.insert(getDbObject(x))
              case "dropIndexes" => coll.dropIndex(getDbObject(x))
              case "removes" => coll.remove(getDbObject(x))
            }
          }
        }
      }
    }

    def connect(): MongoDB = {
      import com.mongodb.casbah.Imports._

      val mongoUri = MongoClientURI(mongoSeedUri.value)
      val mongoClient = MongoClient(mongoUri)

      mongoUri.database map { dbName =>
        mongoClient(dbName)
      } match {
        case Some(db) => db
        case None => throw new RuntimeException(s"Failed to connect to MongoDB with uri: ${mongoSeedUri.value}")
      }
    }

    def getDbObject(conf: Config): BasicDBObject = {
      import com.mongodb.util._
      import com.typesafe.config.ConfigRenderOptions

      val json = conf.root.render(ConfigRenderOptions.concise())
      JSON.parse(json).asInstanceOf[BasicDBObject]
    }

    seedDb()
  }
}
