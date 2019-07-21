package frha.origo;

public class Station {
	public int station_id;
	public String name;
	public int numBikes;
	public int numLocks;
	@Override
	public String toString() {
		return "Station [station_id=" + station_id + ", name=" + name + ", numBikes=" + numBikes + ", numLocks="
				+ numLocks + "]";
	}
	public boolean isActive() {
		return numBikes + numLocks > 0;
	}
	

}
