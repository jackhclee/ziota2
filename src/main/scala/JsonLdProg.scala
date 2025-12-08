
import org.apache.jena.riot.RDFDataMgr
import org.apache.jena.query.DatasetFactory
import org.apache.jena.riot.Lang
import software.amazon.awssdk.utils.StringInputStream

import java.io.ByteArrayInputStream
object JsonLdProg extends App {

    def read() = {
      val payload =
        """
          |{
          |    "@context": "https://schema.org/",
          |    "@type": "Person",
          |    "name": "Jane Doe",
          |    "jobTitle": "Professor",
          |    "telephone": "(425) 123-4567",
          |    "url": "https://www.janedoe.example/"
          |  }
          |""".stripMargin
      val ds = DatasetFactory.create()
      RDFDataMgr.read(ds, new ByteArrayInputStream(payload.getBytes), Lang.JSONLD)
      //val dsg = ds.asDatasetGraph()
      val model = ds.getDefaultModel
      model.listStatements().forEach(println)
      model.close
      ds.close()
    }
   println("Stage 1. hit key to continue")
   Console.in.readLine()
   read()
   println("Stage 2. after read before gc. hit key to continue")
   Console.in.readLine()
   sys.runtime.gc()
   println("Stage 3. gc ed. hit key to continue")
   Console.in.readLine()
}
