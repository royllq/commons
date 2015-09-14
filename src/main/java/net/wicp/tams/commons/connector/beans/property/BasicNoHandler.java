package net.wicp.tams.commons.connector.beans.property;

import org.apache.commons.beanutils.DynaClass;


@SuppressWarnings("rawtypes")
public abstract class BasicNoHandler extends AbstractDynaClassProperty {
	private static final long serialVersionUID = -3270322795171114590L;

	public BasicNoHandler(String name, DynaClass valueClass) {
		super(name);
	}

	public BasicNoHandler(String name, Class type, Class contentType) {
		super(name, type, contentType);
	}

	public BasicNoHandler(String name, Class type) {
		super(name, type);
	}


}
