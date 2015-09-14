package net.wicp.tams.commons.connector.beans.property;

@SuppressWarnings("rawtypes")
public abstract class BasicHandler extends AbstractDynaClassProperty {
	private static final long serialVersionUID = -1521003667299895737L;

	public BasicHandler(String name) {
		super(name);
	}

	public BasicHandler(String name, Class type) {
		super(name, type);
	}

	public BasicHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

}
