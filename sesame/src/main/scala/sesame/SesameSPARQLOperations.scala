package org.w3.banana.sesame

import org.w3.banana._
import org.openrdf.query.parser.sparql.SPARQLParserFactory
import org.openrdf.query.parser.{ ParsedBooleanQuery, ParsedGraphQuery, ParsedTupleQuery }
import scalaz.{ Failure, Success, Validation }
import org.openrdf.query.MalformedQueryException
import scala.collection.JavaConverters._

object SesameSPARQLOperations extends SPARQLOperations[Sesame, SesameSPARQL] {

  private val p = new SPARQLParserFactory().getParser()

  def SelectQuery(query: String): SesameSPARQL#SelectQuery =
    p.parseQuery(query, "http://todo.example/").asInstanceOf[ParsedTupleQuery]

  def ConstructQuery(query: String): SesameSPARQL#ConstructQuery =
    p.parseQuery(query, "http://todo.example/").asInstanceOf[ParsedGraphQuery]

  def AskQuery(query: String): SesameSPARQL#AskQuery =
    p.parseQuery(query, "http://todo.example/").asInstanceOf[ParsedBooleanQuery]

  def Query(query: String): Validation[Exception, SesameSPARQL#Query] = try {
    Success(p.parseQuery(query, "http://todo.example/"))
  } catch {
    case e: MalformedQueryException => Failure(e)
  }

  def fold[T](query: SesameSPARQL#Query)(select: (SesameSPARQL#SelectQuery) => T,
    construct: (SesameSPARQL#ConstructQuery) => T,
    ask: SesameSPARQL#AskQuery => T) =
    query match {
      case qs: SesameSPARQL#SelectQuery => select(qs)
      case qc: SesameSPARQL#ConstructQuery => construct(qc)
      case qa: SesameSPARQL#AskQuery => ask(qa)
    }

  def getNode(solution: SesameSPARQL#Solution, v: String): Validation[BananaException, Sesame#Node] = {
    val node = solution.getValue(v)
    if (node == null)
      Failure(VarNotFound("var " + v + " not found in BindingSet " + solution.toString))
    else
      Success(node)
  }

  def varnames(solution: SesameSPARQL#Solution): Set[String] = solution.getBindingNames.asScala.toSet

  def solutionIterator(solutions: SesameSPARQL#Solutions): Iterable[SesameSPARQL#Solution] = {
    val iterator = new Iterator[SesameSPARQL#Solution] {
      def hasNext: Boolean = solutions.hasNext
      def next(): SesameSPARQL#Solution = solutions.next()
    }
    iterator.toIterable
  }

}