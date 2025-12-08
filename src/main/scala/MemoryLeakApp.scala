import java.io.StringReader

object MemoryLeakApp extends App{
//  def main() = {

    println("before all")
   java.lang.System.in.readNBytes(2)
   Range
      .inclusive(1, 20000)
      .map(i => s"$i*******").foreach(s => {

       val sr = new StringReader(s);
       sr.read()
       sr.close
     })
    println("before gc")
    java.lang.System.in.readNBytes(2)
    sys.runtime.gc
    println("after gc")
    java.lang.System.in.readNBytes(2)
//  }
}
