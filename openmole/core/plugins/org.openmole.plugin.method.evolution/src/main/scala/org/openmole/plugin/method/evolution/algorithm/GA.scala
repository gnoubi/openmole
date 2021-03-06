/*
 * Copyright (C) 2012 reuillon
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.openmole.plugin.method.evolution.algorithm

import fr.iscpif.mgo._
import fr.iscpif.mgo.tools.Lazy
import org.openmole.core.implementation.tools._
import org.openmole.misc.tools.script._
import org.openmole.misc.tools.service.Duration._
import scala.util.Random

object GA {

  trait GA extends G with ContextPhenotype with MG with MF with GASigma {
    val gManifest = manifest[G]
    val individualManifest = manifest[Individual[G, P, F]]
    val fManifest = manifest[F]
  }

  trait GATermination extends Termination with GA with TerminationManifest

  def counter(_steps: Int) = new GATermination with CounterTermination {
    type MF = Any
    val steps = _steps
    val stateManifest = manifest[STATE]
  }

  def timed(_duration: String) =
    new GATermination with TimedTermination {
      type MF = Any
      val duration = _duration.toMilliSeconds
      val stateManifest = manifest[STATE]
    }

  trait GARanking extends Ranking with GA

  trait GARankingBuilder {
    def apply(dominance: Dominance): GARanking
  }

  def pareto = new GARankingBuilder {
    def apply(dominance: Dominance) = new ParetoRanking with GARanking {
      def isDominated(p1: Seq[Double], p2: Seq[Double]) = dominance.isDominated(p1, p2)
    }
  }

  trait GADiversityMetric extends DiversityMetric with GA

  trait DiversityMetricBuilder {
    def apply(dominance: Dominance): GADiversityMetric
  }

  def crowding = new DiversityMetricBuilder {
    def apply(dominance: Dominance) = new CrowdingDiversity with GADiversityMetric
  }

  def hypervolume(_referencePoint: Double*) = new DiversityMetricBuilder {
    def apply(dominance: Dominance) = new HypervolumeDiversity with GADiversityMetric {
      def isDominated(p1: Seq[Double], p2: Seq[Double]) = dominance.isDominated(p1, p2)
      val referencePoint = _referencePoint
    }
  }

  def strictEpsilon(_epsilons: Double*) = new StrictEpsilonDominance {
    val epsilons = _epsilons
  }

  def nonStrictEpsilon(_epsilons: Double*) = new NonStrictEpsilonDominance {
    val epsilons = _epsilons
  }

  def strict = new StrictDominance {}
  def nonStrict = new NonStrictDominance {}

  trait GAModifier extends Modifier with GA

  trait GAAlgorithm extends Archive
    with ArchiveManifest
    with GA
    with GAModifier
    with Elitism
    with PopulationManifest
    with Selection
    with Termination
    with TerminationManifest

  trait GAAlgorithmBuilder extends A {
    def apply: GAAlgorithm
  }

  trait Optimisation extends NoArchive
    with RankDiversityModifier
    with GAAlgorithm
    with NonDominatedElitism
    with BinaryTournamentSelection

  def optimisation(
    mu: Int,
    termination: GATermination { type MF >: Optimisation#MF },
    dominance: Dominance = strict,
    ranking: GARankingBuilder = pareto,
    diversityMetric: DiversityMetricBuilder = crowding) = new GAAlgorithmBuilder {
    val (_mu, _dominance, _ranking, _diversityMetric) = (mu, dominance, ranking, diversityMetric)
    def apply =
      new Optimisation {
        val aManifest = manifest[A]
        val populationManifest = manifest[Population[G, P, F, MF]]
        val diversityMetric = _diversityMetric(dominance)
        val ranking = _ranking(dominance)
        val mu = _mu
        def diversity(individuals: Seq[Seq[Double]], ranks: Seq[Lazy[Int]]) = diversityMetric.diversity(individuals, ranks)
        def rank(individuals: Seq[Seq[Double]]) = ranking.rank(individuals)
        type STATE = termination.STATE
        implicit val stateManifest = termination.stateManifest
        def initialState: STATE = termination.initialState
        def terminated(population: ⇒ Population[G, P, F, MF], terminationState: STATE): (Boolean, STATE) = termination.terminated(population, terminationState)
      }
  }

  trait GAProfile extends GA {
    def aggregation: GAAggregation
    def x: Int
  }

  trait GenomeProfile extends GAAlgorithm
    with ProfileModifier
    with ProfileElitism
    with NoArchive
    with NoDiversity
    with ProfileGenomePlotter
    with HierarchicalRanking
    with BinaryTournamentSelection

  def genomeProfile(
    x: Int,
    nX: Int,
    aggregation: GAAggregation,
    termination: GATermination { type MF >: GenomeProfile#MF }) = {
    val (_x, _nX, _aggregation) = (x, nX, aggregation)
    new GAAlgorithmBuilder with GAProfile {
      val aggregation = _aggregation
      val x = _x

      def apply =
        new GenomeProfile {
          val aManifest = manifest[A]
          val populationManifest = manifest[Population[G, P, F, MF]]
          val x = _x
          val nX = _nX
          def aggregate(fitness: F) = _aggregation.aggregate(fitness)
          type STATE = termination.STATE
          implicit val stateManifest = termination.stateManifest
          def initialState: STATE = termination.initialState
          def terminated(population: ⇒ Population[G, P, F, MF], terminationState: STATE): (Boolean, STATE) = termination.terminated(population, terminationState)
        }
    }
  }

  trait GAProfilePlotter extends ProfilePlotter with GA with MG

  def profilePlotter(x: String) = new GAProfilePlotter {
    @transient lazy val interpreter = new GroovyProxyPool(x)

    def plot(individual: Individual[this.type#G, this.type#P, this.type#F]) =
      interpreter.execute(individual.phenotype.toBinding).asInstanceOf[Double].toInt

  }

  trait GAMap extends GA {
    def aggregation: GAAggregation
    def x: Int
    def y: Int
  }

  trait GenomeMap extends GAAlgorithm
    with MapElitism
    with MapGenomePlotter
    with NoArchive
    with NoRanking
    with NoModifier
    with MapSelection

  def genomeMap(
    x: Int,
    nX: Int,
    y: Int,
    nY: Int,
    aggregation: GAAggregation,
    termination: GATermination { type MF >: GenomeMap#MF }) = {
    val (_x, _nX, _y, _nY, _aggregation) = (x, nX, y, nY, aggregation)
    new GAAlgorithmBuilder with GAMap {
      val aggregation = _aggregation
      val x = _x
      val y = _y

      def apply =
        new GenomeMap {
          val aManifest = manifest[A]
          val populationManifest = manifest[Population[G, P, F, MF]]

          def aggregate(fitness: F) = _aggregation.aggregate(fitness)
          val x = _x
          val y = _y
          val nX = _nX
          val nY = _nY

          type STATE = termination.STATE
          implicit val stateManifest = termination.stateManifest
          def initialState: STATE = termination.initialState
          def terminated(population: ⇒ Population[G, P, F, MF], terminationState: STATE): (Boolean, STATE) = termination.terminated(population, terminationState)

        }
    }
  }

  trait GAAggregation extends Aggregation with MG
  trait GAMapPlotter extends MapPlotter with GA with MG

  def max = new MaxAggregation with GAAggregation {}

  def mapGenomePlotter(x: String, y: String) = new GAMapPlotter {
    @transient lazy val xInterpreter = new GroovyProxyPool(x)
    @transient lazy val yInterpreter = new GroovyProxyPool(y)

    def plot(individual: Individual[this.type#G, this.type#P, this.type#F]) =
      (xInterpreter.execute(individual.phenotype.toBinding).asInstanceOf[Double].toInt,
        yInterpreter.execute(individual.phenotype.toBinding).asInstanceOf[Double].toInt)
  }

  trait GACrossover extends CrossOver with GA

  trait GACrossoverBuilder {
    def apply(genomeSize: Factory[GA#G]): GACrossover
  }

  def sbx(distributionIndex: Double = 2.0) = new GACrossoverBuilder {
    val _distributionIndex = distributionIndex
    def apply(_genomeFactory: Factory[GA#G]) =
      new SBXBoundedCrossover with GACrossover {
        override val distributionIndex = _distributionIndex
        val genomeFactory = _genomeFactory
      }
  }

  trait GAMutation extends Mutation with GA

  trait GAMutationBuilder extends GA {
    def apply(genomeFactory: Factory[GA#G]): GAMutation
  }

  def coEvolvingSigma = new GAMutationBuilder {
    def apply(_genomeFactory: Factory[GA#G]) = new CoEvolvingSigmaValuesMutation with GAMutation {
      val genomeFactory = _genomeFactory
    }
  }

  def apply(
    algorithm: GAAlgorithmBuilder,
    lambda: Int,
    mutation: GAMutationBuilder = coEvolvingSigma,
    crossover: GACrossoverBuilder = sbx(),
    cloneProbability: Double = 0.0) =
    new org.openmole.plugin.method.evolution.algorithm.GAImpl(algorithm, lambda, mutation, crossover, cloneProbability)(_)

}

trait GA extends GASigmaFactory
  with EvolutionManifest
  with TerminationManifest
  with GA.GA
  with Archive
  with Termination
  with GeneticBreeding
  with MG
  with Elitism
  with Modifier
  with CloneRemoval
  with ContextPhenotype
  with PopulationManifest

sealed class GAImpl(
  val algorithm: GA.GAAlgorithmBuilder,
  val lambda: Int,
  val mutation: GA.GAMutationBuilder,
  val crossover: GA.GACrossoverBuilder,
  override val cloneProbability: Double)(val genomeSize: Int)
    extends GA { sga ⇒

  lazy val thisAlgorithm = algorithm.apply
  lazy val thisCrossover = crossover(genomeFactory)
  lazy val thisMutation = mutation(genomeFactory)

  type STATE = thisAlgorithm.STATE
  type A = thisAlgorithm.A
  type MF = thisAlgorithm.MF

  implicit val aManifest = thisAlgorithm.aManifest
  implicit val populationManifest = thisAlgorithm.populationManifest

  implicit val stateManifest = thisAlgorithm.stateManifest
  def initialState: STATE = thisAlgorithm.initialState
  def terminated(population: ⇒ Population[G, P, F, MF], terminationState: STATE): (Boolean, STATE) = thisAlgorithm.terminated(population, terminationState)
  def toArchive(individuals: Seq[Individual[G, P, F]]) = thisAlgorithm.toArchive(individuals)
  def combine(a1: A, a2: A) = thisAlgorithm.combine(a1, a2)
  def diff(a1: A, a2: A) = thisAlgorithm.diff(a1, a2)
  def initialArchive = thisAlgorithm.initialArchive
  def modify(individuals: Seq[Individual[G, P, F]], archive: A) = thisAlgorithm.modify(individuals, archive)
  def crossover(g1: G, g2: G)(implicit rng: Random) = thisCrossover.crossover(g1, g2)
  def mutate(genome: G)(implicit rng: Random) = thisMutation.mutate(genome)
  def elitism(individuals: Seq[Individual[G, P, F]], newIndividuals: Seq[Individual[G, P, F]], a: A) = thisAlgorithm.elitism(individuals, newIndividuals, a)
  def selection(population: Population[G, P, F, MF])(implicit aprng: Random): Iterator[Individual[G, P, F]] = thisAlgorithm.selection(population)

}