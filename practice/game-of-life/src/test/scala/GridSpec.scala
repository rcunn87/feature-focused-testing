
import org.scalatest.{FeatureSpec, GivenWhenThen, Matchers}

import scala.util.Random

class GridSpec extends FeatureSpec with Matchers with GivenWhenThen with GridFixture {

  feature("A 2D grid of living and dead cells.") {
    scenario("The grid is 32 bits, two dimensional, and holds living and dead cells.") {
      Given("randomly distributed locations of living and dead cells")
      val livingCells = randomCells()
      val deadCells = randomCells(excluding = livingCells)

      When("a grid is initialized with living and dead cell locations")
      val grid = new Grid(livingCells)

      Then("the grid gives the correct state for each cell")
      for (c <- livingCells) {
        grid.isAlive(c) shouldBe true
      }
      for (c <- deadCells) {
        grid.isAlive(c) shouldBe false
      }
    }

    scenario("The number of live neighbors of a cell can be counted.") {
      Given("cells with between 0 and 8 random live neighbors")
      val liveNeighborCountPossibilities = 0 to 8

      case class TestScenario(grid: Grid, cell: Cell, expectedLiveNeighborCount: Int)

      When("a grid is initialized for each cell plus its live neighbors")
      val testScenarios = liveNeighborCountPossibilities map { liveNeighborCount =>
        val cell = randomCell()
        val neighbors = getNeighborCells(cell)
        val randomLiveNeighbors = Random.shuffle(neighbors).take(liveNeighborCount)
        val grid = new Grid(randomLiveNeighbors + cell)

        TestScenario(grid, cell, liveNeighborCount)
      }

      Then("the correct live neighbor count is given for each cell")
      for (s <- testScenarios) {
        import s._
        grid.countLiveNeighbors(cell) shouldBe expectedLiveNeighborCount
      }
    }

    scenario("Grids can be compared for equality.") {
      val cells = randomCells()
      val matchingCells = Set() ++ cells
      val unmatchingCells = randomCells()

      val grid = new Grid(cells)
      val matchingGrid = new Grid(matchingCells)
      val notMatchingGrid = new Grid(unmatchingCells)
      val notGridAtAll = new AnyRef

      grid shouldBe matchingGrid
      grid should not be notMatchingGrid
      grid should not be notGridAtAll
    }
  }

  private def randomBooleanSquare(width: Int, height: Int): Seq[Seq[Boolean]] = {
    0 until height map { y =>
      0 until width map { x =>
        Random.nextBoolean()
      }
    }
  }

}
