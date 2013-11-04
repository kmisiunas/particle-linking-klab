package klab.io.infrastructure.load

/**
 * == For recognising elements inside a table ==
 *
 * User: karolis@misiunas.com
 * Date: 20/08/2013
 * Time: 16:03
 */
object TableInterpreter {

  // todo heading treated differently?

  def findElem(line: String): Array[String] =
    line.split("\\t| |;|,").map(_.trim).filterNot(_.isEmpty) // scala magic


  def identifyLines(st: String): List[String] =
    throw new Exception("use \"string\".lines instead")
}
