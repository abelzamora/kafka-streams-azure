package es.hablapps

import java.time.Duration
import java.util.Properties

import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.{KafkaStreams, StreamsBuilder, StreamsConfig}

object KSMain extends App {

  val config = {
    val p = new Properties()
    p.put(StreamsConfig.APPLICATION_ID_CONFIG, "stream-application")
    p.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "10.0.0.4:9092")
    p.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass)
    p
  }

  val builder = new StreamsBuilder
  val textLines: KStream[Array[Byte], String] = builder.stream[Array[Byte], String]("TextLinesTopic")

  val uppercasedWithMapValues = textLines.mapValues(_.toUpperCase)
  uppercasedWithMapValues.to("UppercasedTextLinesTopic")

  val streams: KafkaStreams = new KafkaStreams(builder.build(), config)
  streams.start()

  sys.ShutdownHookThread {
    streams.close(Duration.ofSeconds(10L))
  }


}