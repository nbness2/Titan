package org.maxgamer.rs.cache.format;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.maxgamer.rs.cache.Archive;
import org.maxgamer.rs.cache.IDX;
import org.maxgamer.rs.cache.RSInputStream;
import org.maxgamer.rs.cache.reference.ChildReference;
import org.maxgamer.rs.cache.reference.Reference;
import org.maxgamer.rs.cache.reference.ReferenceTable;
import org.maxgamer.rs.core.Core;
import org.maxgamer.rs.util.io.ByteBufferInputStream;

public final class ClientScriptSettings {

	private static final ConcurrentHashMap<Integer, ClientScriptSettings> widgetScripts = new ConcurrentHashMap<Integer, ClientScriptSettings>();

	private String defaultStringValue;
	private int defaultIntValue;
	private HashMap<Long, Object> values;

	public static final ClientScriptSettings getSettings(int scriptId) {
		ClientScriptSettings script = widgetScripts.get(scriptId);
		if (script != null)
			return script;
		ByteBuffer data = null;
		try {
			ReferenceTable table = Core.getCache().getReferenceTable(IDX.CLIENTSCRIPT_SETTINGS);
			Reference ref = table.getReference(scriptId >>> 0xba9ed5a8);
			Archive archive = Core.getCache().getArchive(IDX.CLIENTSCRIPT_SETTINGS, ref.getId());
			ChildReference child = ref.getChild(scriptId & 0xff);
			data = archive.get(child.getId());

			script = new ClientScriptSettings();
			if (data != null) {
				script.readValueLoop(new RSInputStream(new ByteBufferInputStream(data)));
				widgetScripts.put(scriptId, script);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return script;

	}

	public HashMap<Long, Object> getValues() {
		return values;
	}

	public Object getValue(long key) {
		if (values == null)
			return null;
		return values.get(key);
	}

	public long getKeyForValue(Object value) {
		for (Long key : values.keySet()) {
			if (values.get(key).equals(value))
				return key;
		}
		return -1;
	}

	public int getSize() {
		if (values == null)
			return 0;
		return values.size();
	}

	public int getIntValue(long key) {
		if (values == null)
			return defaultIntValue;
		Object value = values.get(key);
		if (value == null || !(value instanceof Integer))
			return defaultIntValue;
		return (Integer) value;
	}

	public String getStringValue(long key) {
		if (values == null)
			return defaultStringValue;
		Object value = values.get(key);
		if (value == null || !(value instanceof String))
			return defaultStringValue;
		return (String) value;
	}

	private void readValueLoop(RSInputStream stream) throws Exception {
		for (;;) {
			int opcode = stream.readByte() & 0xFF;
			if (opcode == 0)
				break;
			readValues(stream, opcode);
		}
	}

	private void readValues(RSInputStream stream, int opcode) throws Exception {
		if (opcode == 1)
			stream.readByte();
		else if (opcode == 2)
			stream.readByte();
		else if (opcode == 3)
			defaultStringValue = stream.readPJStr1();
		else if (opcode == 4)
			defaultIntValue = stream.readInt();
		else if (opcode == 5 || opcode == 6 || opcode == 7 || opcode == 8) {
			int count = stream.readUnsignedShort();
			int loop = opcode == 7 || opcode == 8 ? stream.readUnsignedShort() : count;
			values = new HashMap<Long, Object>(getHashMapSize(count));
			for (int i = 0; i < loop; i++) {
				int key = opcode == 7 || opcode == 8 ? stream.readUnsignedShort() : stream.readInt();
				Object value = opcode == 5 || opcode == 7 ? stream.readPJStr1() : stream.readInt();
				values.put((long) key, value);
			}
		}
	}

	private int getHashMapSize(int size) {
		size--;
		size |= size >>> -1810941663;
		size |= size >>> 2010624802;
		size |= size >>> 10996420;
		size |= size >>> 491045480;
		size |= size >>> 1388313616;
		return 1 + size;
	}

	private ClientScriptSettings() {
		defaultStringValue = "null";
	}
}