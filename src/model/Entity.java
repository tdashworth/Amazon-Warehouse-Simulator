package model;

import javafx.scene.shape.Shape;

/**
 *	A model a something that physically exists on the warehouse floor.
 */
public abstract class Entity {
	// Stores the UID in the string format (S)SNN. 
	protected String uid;
	// Stores the current location of the entity on the warehouse floor.
	protected Location location;
	// The UI element.
	private final Shape sprite; 
	
	/**
	 * A model a something that physically exists on the warehouse floor.
	 * @param uid
	 * @param location
	 */
	public Entity(String uid, Location location, Shape sprite) {
		this.uid = uid;
		this.location = location;
		this.sprite = sprite;
	}
	
	/**
	 * @return The UID.
	 */
	public String getUID() {
		return this.uid;
	}
	
	/**
	 * @return The location.
	 */
	public Location getLocation() {
		return this.location;
	}
	
	protected void log(String message) {
		String classType = this.getClass().getSimpleName();

		System.out.println(String.format("%s(%s): %s", classType, this.uid, message));
	}
	
	protected void log(String format, Object... args) {
		this.log(String.format(format, args));
	}
	
	public Shape getSprite() {
		if (this.sprite == null) throw new IllegalStateException("'sprite' has not been defined.");
		return sprite;
	}
	
	/**
	 *	@return A string representation of a charging pod.
	 */
	public String toString() {
		String className = this.getClass().getSimpleName();
		return String.format("%s(%s) - %s", className, this.uid, this.location);
	} 
	
}
