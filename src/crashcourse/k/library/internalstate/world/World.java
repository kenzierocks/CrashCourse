package crashcourse.k.library.internalstate.world;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import crashcourse.k.library.internalstate.Entity;

public class World {
	private static int DEF_WORLD_SIZE = 800;

	protected ArrayList<Entity> loadedEntities = new ArrayList<Entity>();
	private ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
	private ReentrantReadWriteLock.ReadLock read = rwLock.readLock();
	private ReentrantReadWriteLock.WriteLock write = rwLock.writeLock();

	/**
	 * Width = MAX_X Height = MAX_Y Length = MAX_Z
	 */
	public int width, height, length;

	public World() {
		this(DEF_WORLD_SIZE, DEF_WORLD_SIZE, DEF_WORLD_SIZE);
	}

	public World(int w, int h, int l) {
		width = w;
		height = h;
		length = l;
	}

	public void setSize(int newW, int newH, int newL) {
		width = newW;
		height = newH;
		length = newL;
	}

	/**
	 * Returns an unmodifiable List for access to the entities.
	 * 
	 * @return an unmodifiable List of entities in this world
	 */
	public List<Entity> getEntityList() {
		read.lock();
		List<Entity> unmodlist = Collections
				.unmodifiableList(new ArrayList<Entity>(loadedEntities));
		read.unlock();
		return unmodlist;
	}

	/**
	 * Adds <i>e</i> to the list
	 * 
	 * @param e
	 *            - the entity to add
	 */
	public void addEntity(Entity e) {
		write.lock();
		loadedEntities.add(e);
		write.unlock();
	}

	/**
	 * Updates all entities with given delta
	 * 
	 * @param delta
	 *            - time since last loop in ms
	 */
	public void update(float delta) {
		ArrayList<Entity> removeList = new ArrayList<Entity>();
		read.lock();
		for (Entity e : loadedEntities) {
			if (e.isDead()) {
				removeList.add(e);
				continue;
			}
			e.updateOnTick(delta, this);
		}
		read.unlock();
		write.lock();
		loadedEntities.removeAll(removeList);
		write.unlock();
	}

	/**
	 * Interpolates all entities with given delta
	 * 
	 * @param delta
	 *            - time since last loop in ms
	 */
	public void interpolate(int delta) {
		read.lock();
		for (Entity e : loadedEntities) {
			e.interpolate(delta);
		}
		read.unlock();
	}

	public String toString() {
		return String.format(
				"{World:{Entities:%s,width:%s,height:%s,length:%s}}",
				loadedEntities, width, height, length);
	}
}
