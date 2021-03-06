package simulator;

import java.util.*;
import java.util.stream.Collectors;

public class Simulator<W extends AWorld> {
	private int totalTickCount;
	private final List<IActor<W>> actors;
	private final W world;

	/**
	 * Simulator constructor setting up the warehouse and entities
	 * 
	 * @throws LocationNotValidException
	 */
	public Simulator(W world, List<IActor<W>> actors) {
		this.totalTickCount = 0;
		this.world = world;
		this.actors = actors;
	}

	/**
	 * Simulator run method, keeps the simulation until all orders have been dispatched.
	 * 
	 * @throws Exception
	 */
	public void run() throws Exception {
		while (!this.world.isComplete())
			tick();

		// this.world.getReportWriter().write(this, Paths.get("./EndReport.txt"));
		System.out.println("Completed, end report created.");
	}

	/**
	 * Tick method which gets all of the actors to tick simultaneously.
	 * 
	 * @throws Exception
	 */
	public void tick() throws Exception {
		this.totalTickCount++;
		System.out.println("Simulation: Tick Count now " + this.totalTickCount);
		for (IActor<W> actor : actors) {
			actor.tick(this.world, this.totalTickCount);
		}
	}

	/**
	 * @return the totalTickCounts
	 */
	public int getTotalTickCount() {
		return totalTickCount;
	}

	public List<IActor<W>> getActors() {
		return Collections.unmodifiableList(this.actors);
	}

	public List<AMover<W>> getMovers() {
		return this.actors.stream().filter(actor -> actor instanceof AMover)
				.map(mover -> (AMover<W>) mover).collect(Collectors.toList());
	}

	public W getWorld() {
		return this.world;
	}

}
