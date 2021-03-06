package simulator.utils;

import org.junit.Test;
import static org.junit.Assert.*;
import simulator.Location;
import simulator.utils.BasicPathCostEstimator;
import simulator.utils.IPathCostEstimator;


public class BasicPathCostEstimatorTests {
  private enum Factors { cost1, cost2 };

  @Test
  public void testConstructorWithValidParametersShouldSuccussfullyCreate() {
    Location origin = new Location(0, 0);

    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);

    assertEquals(0.0, pathCostEstimator.getEstimatedCost(), 0.0);
  }

  @Test
  public void testAddCostWithValidParametersShouldSuccussfullyCreateCosts() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);

    pathCostEstimator.addCost(Factors.cost1, 1.0);
    pathCostEstimator.addCost(Factors.cost2, 2.2);
  }

  @Test
  public void testAddCostWithNullLabelShouldThrowException() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);

    try {
      pathCostEstimator.addCost(null, 1.0);
      fail("Adding a cost with a null label is forbiden and should fail.");
    } catch (Exception e) {
      assertEquals("The parameter 'factor' must not be null.", e.getMessage());
    }
  }

  @Test
  public void testAddCostWithNullCostShouldThrowException() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);

    try {
      pathCostEstimator.addCost(Factors.cost2, null);
      fail("Adding a cost with a null value is forbiden and should fail.");
    } catch (Exception e) {
      assertEquals("The parameter 'value' must not be null.", e.getMessage());
    }
  }

  @Test
  public void testAddLocationWithValidLocationShouldNotThrowException() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);
    pathCostEstimator.addCost(Factors.cost1, 1.0);

    pathCostEstimator.addLocation(new Location(0, 1), Factors.cost1);
  }

  @Test
  public void testAddLocationWithNullLocationShouldThrowException() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);
    pathCostEstimator.addCost(Factors.cost1, 1.0);

    try {
      pathCostEstimator.addLocation(null, Factors.cost1);
      fail("Adding a location with a null value is forbiden and should fail.");
    } catch (Exception e) {
      assertEquals("The parameter 'location' must not be null.", e.getMessage());
    }
  }

  @Test
  public void testAddLocationWithNullCostShouldThrowException() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);
    pathCostEstimator.addCost(Factors.cost1, 1.0);

    try {
      pathCostEstimator.addLocation(new Location(0, 1), null);
      fail("Adding a location with a null cost label is forbiden and should fail.");
    } catch (Exception e) {
      assertEquals(
          "The parameter 'factor' must not be null.",
          e.getMessage());
    }
  }

  @Test
  public void testAddLocationWithInvalidCostShouldThrowException() throws Exception {
    Location origin = new Location(0, 0);
    IPathCostEstimator<Factors> pathCostEstimator = new BasicPathCostEstimator<>(origin);
    pathCostEstimator.addCost(Factors.cost1, 1.0);

    try {
      pathCostEstimator.addLocation(new Location(0, 1), Factors.cost2);
      fail("Adding a location with cost label of 'cost2' should fail becasue it doesn't exist.");
    } catch (Exception e) {
      assertEquals(
          "Cost factor 'cost2' has not been set. Use the addCost method to set it before using it here.",
          e.getMessage());
    }
  }
}
