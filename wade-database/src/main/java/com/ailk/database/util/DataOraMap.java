package com.ailk.database.util;

import java.lang.reflect.Array;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.CHAR;
import oracle.sql.CharacterSet;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

import com.ailk.common.data.IData;
import com.ailk.common.data.impl.DataMap;

public class DataOraMap extends DataMap {
	private static final long serialVersionUID = 1L;
	private static oracle.sql.CharacterSet thischarset = CharacterSet
			.make(CharacterSet.ZHS16GBK_CHARSET);

	public void set(String name, int index, Object value) throws Exception
	{
		Object obj = ((Map) this).get(name);
		if (obj instanceof Array)
		{
			Array.set(obj, index, value);
		} else if (obj instanceof List)
		{
			((List) obj).add(index, value);
		} else if (obj == null)
		{
			obj = new Vector();
			((Vector) obj).add(index, value);
			((Map) this).put(name, obj);
		}

	}

	public void set(String name, Object value) throws Exception
	{
		((Map) this).put(name, value);
	}

	public static IData fromOraStruct(Object o) throws Exception
	{

		if (!(o instanceof oracle.sql.STRUCT))
		{
			throw new Exception();
		}
		Object values[] = ((STRUCT) o).getOracleAttributes();
		return unpackMap((ARRAY) (values[0]));
	}

	private static IData unpackMap(ARRAY ar) throws Exception
	{
		DataOraMap dm = new DataOraMap();
		ResultSet mrec = ar.getResultSet();
		while (mrec.next())
		{
			oracle.sql.STRUCT mapNode = (oracle.sql.STRUCT) mrec.getObject(2);
			Object values[] = mapNode.getOracleAttributes();
			oracle.sql.CHAR s = (CHAR) values[0];
			String s2 = new String(s.getBytes());
			oracle.sql.ARRAY oraarray = (oracle.sql.ARRAY) values[1];
			List l = unpackArrayList(oraarray);
			dm.set(s2, l);
		}

		return dm;
	}

	public static List unpackArrayList(ARRAY ar) throws Exception
	{
		ArrayList al = new ArrayList();
		ResultSet mrec = ar.getResultSet();
		while (mrec.next())
		{
			Object o=mrec.getObject(2);
			if (o == null)
			{
				al.add("");
			} else if(o instanceof oracle.sql.CHAR)
			{
				al.add(new String(((CHAR)o).getBytes()));
			}
			else if(o instanceof java.lang.String)
			{
				al.add(o);
			}
			else
			{
				System.out.println(o.getClass());
				throw new Exception();
			}

		}
		return al;
	}

	public static Object toOraStruct(IData data, Connection conn)
			throws Exception
	{
		StructDescriptor wadeIDataDesc = StructDescriptor.createDescriptor(
				"WADE_IDATA", conn);
		ARRAY wadeMap = packMap(data, conn);
		Object[] wadeIdataElem =
		{ wadeMap };
		STRUCT wadeIdata = new STRUCT(wadeIDataDesc, conn, wadeIdataElem);
		return wadeIdata;
	}

	private static ARRAY packArrayList(List l, Connection conn)
			throws Exception
	{

		ArrayDescriptor wadeArrayListDesc = ArrayDescriptor.createDescriptor(
				"WADE_ARRAYLIST", conn);
		CHAR[] wadeArrayListElem = new CHAR[l.size()];
		for (int i = 0, size = l.size(); i < size; i++)
		{
			Object value = l.get(i);

			if (value instanceof String)
			{
				wadeArrayListElem[i] = new CHAR((String) value, thischarset);
			} else if (value == null)
			{
				wadeArrayListElem[i] = new CHAR("", thischarset);
			} else if (value instanceof Date)
			{
				String s = value.toString();
				wadeArrayListElem[i] = new CHAR(s, thischarset);
			} else if (value instanceof Long || value instanceof Integer
					|| value instanceof Double || value instanceof Float)
			{
				String s = value.toString();
				wadeArrayListElem[i] = new CHAR(s, thischarset);
			} else
			{
				String s = ("[\"{OBJ}" + value.toString() + "\"]");
				wadeArrayListElem[i] = new CHAR(s, thischarset);
			}
		}
		ARRAY wadeArrayList = new ARRAY(wadeArrayListDesc, conn,
				wadeArrayListElem);

		return wadeArrayList;
	}

	private static STRUCT packMapNode(String key, List value, Connection conn)
			throws Exception
	{
		StructDescriptor wadeMapNodeDesc = StructDescriptor.createDescriptor(
				"WADE_MAP_NODE", conn);
		ARRAY wadeArrayListElem = packArrayList(value, conn);
		Object[] wadeMapNodeElem =
		{ new CHAR(key, thischarset), wadeArrayListElem };
		STRUCT wadeMapNode = new STRUCT(wadeMapNodeDesc, conn, wadeMapNodeElem);
		return wadeMapNode;
	}

	private static ARRAY packMap(IData data, Connection conn) throws Exception
	{
		ArrayDescriptor wadeMapDesc = ArrayDescriptor.createDescriptor(
				"WADE_MAP", conn);
		Object[] wadeMapElem = new Object[data.size()];

		Iterator i = data.keySet().iterator();
		boolean hasNext = i.hasNext();
		int j = 0;
		while (hasNext)
		{
			String key = (String) (i.next());
			if (!(key instanceof String))
			{
				throw new Exception();
			}
			Object value = data.get(key);

			if (value instanceof List)
			{
				wadeMapElem[j] = packMapNode(key, (List) value, conn);
			} else
			{
				ArrayList al = new ArrayList();
				al.add(value);
				wadeMapElem[j] = packMapNode(key, al, conn);
			}
			hasNext = i.hasNext();
			j++;
		}
		ARRAY wadeMap = new ARRAY(wadeMapDesc, conn, wadeMapElem);
		return wadeMap;
	}

}
