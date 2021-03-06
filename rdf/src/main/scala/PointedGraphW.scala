package org.w3.banana

import scala.util._

class PointedGraphW[Rdf <: RDF](val pointed: PointedGraph[Rdf]) extends AnyVal {

  import pointed.graph

  def as[T](implicit binder: PointedGraphBinder[Rdf, T]): Try[T] =
    binder.fromPointedGraph(pointed)

  def as2[T1, T2](implicit b1: PointedGraphBinder[Rdf, T1], b2: PointedGraphBinder[Rdf, T2]): Try[(T1, T2)] =
    for {
      t1 <- b1.fromPointedGraph(pointed)
      t2 <- b2.fromPointedGraph(pointed)
    } yield (t1, t2)

  def a(clazz: Rdf#URI)(implicit ops: RDFOps[Rdf]): PointedGraph[Rdf] = {
    import ops._
    val newGraph = graph union Graph(Triple(pointer, rdf("type"), clazz))
    PointedGraph(pointer, newGraph)
  }

  def --(p: Rdf#URI): PointedGraphPredicate[Rdf] = new PointedGraphPredicate[Rdf](pointed, p)

  def -<-(p: Rdf#URI): PredicatePointedGraph[Rdf] = new PredicatePointedGraph[Rdf](p, pointed)

  def /(p: Rdf#URI)(implicit ops: RDFOps[Rdf]): PointedGraphs[Rdf] = {
    val nodes = ops.getObjects(graph, pointer, p)
    new PointedGraphs[Rdf](nodes, graph)
  }

  def pointer: Rdf#Node = pointed.pointer

  def predicates(implicit ops: RDFOps[Rdf]) = ops.getPredicates(graph, pointer)

  def isA(clazz: Rdf#URI)(implicit ops: RDFOps[Rdf]): Boolean = {
    import ops._
    def isAIfNodeOrBNode = {
      val classes = getObjects(graph, pointer, rdf("type"))
      classes exists { _ == clazz }
    }
    pointer.fold(
      uri => isAIfNodeOrBNode,
      bnode => isAIfNodeOrBNode,
      literal => false
    )
  }

}
