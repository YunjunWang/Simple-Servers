package simpleservers.simpleservers;

/**
 * Server class for accessors and mutators
 * 
 * @author Yunjun Wang
 *
 */
public class Server {
	private String id;
	private String name;
	private String desc;

	public String getID() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}

	public String getDesc() {
		return this.desc;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
