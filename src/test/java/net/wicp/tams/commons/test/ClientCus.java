package net.wicp.tams.commons.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.wicp.tams.commons.connector.IClientCus;
import net.wicp.tams.commons.constant.ColProperty;

public class ClientCus implements IClientCus {

	@Override
	public List<Map<ColProperty, String>> confClientInput() {
		List<Map<ColProperty, String>> retlist = new ArrayList<>();
		Map<ColProperty, String> col1 = new HashMap<>();
		col1.put(ColProperty.name, "test");
		col1.put(ColProperty.type, "string");
		retlist.add(col1);
		return retlist;
	}

	@Override
	public List<Map<ColProperty, String>> confClientOutput() {
		// TODO Auto-generated method stub
		return null;
	}

}
