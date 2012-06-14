package org.w3.banana.jena

import org.w3.banana._

import com.hp.hpl.jena.query._
import com.hp.hpl.jena.graph.{ Node => JenaNode }
import com.hp.hpl.jena.rdf.model.RDFNode

object JenaSPARQLOperations extends SPARQLOperations[Jena, JenaSPARQL] {

  def getNode(row: JenaSPARQL#Row, v: String): JenaNode = {
    val node: RDFNode = row.get(v)
    JenaGraphTraversal.toNode(node)
  }

  def SelectQuery(query: String): JenaSPARQL#SelectQuery = QueryFactory.create(query)
    
  def ConstructQuery(query: String): JenaSPARQL#ConstructQuery = QueryFactory.create(query)

  def AskQuery(query: String): JenaSPARQL#AskQuery = QueryFactory.create(query)

  def Query(query: String): JenaSPARQL#Query  = QueryFactory.create(query)

}
