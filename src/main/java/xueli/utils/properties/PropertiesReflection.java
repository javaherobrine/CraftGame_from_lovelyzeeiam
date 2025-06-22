package xueli.utils.properties;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import xueli.utils.logger.Logger;

public class PropertiesReflection {

	private static final Logger LOGGER = new Logger();

	private static HashMap<Class<?>, Parsable<?>> parsers = new HashMap<>();
	
	static {
		parsers.put(boolean.class, Boolean::parseBoolean);
		parsers.put(byte.class, Byte::parseByte);
		parsers.put(short.class, Short::parseShort);
		parsers.put(int.class, Integer::parseInt);
		parsers.put(long.class, Long::parseLong);
		parsers.put(float.class, Float::parseFloat);
		parsers.put(double.class, Double::parseDouble);
		parsers.put(char.class, (s)->{return s.charAt(0);});
		parsers.put(String.class, (s)->{return s;});
		
	}

	public static <T> void registerParser(Parsable<T> parsable, Class<T> clazz) {
		parsers.put(clazz, parsable);

	}

	public static void reflect(Object obj, File properties) throws Exception {
//		Logger.getInstance().pushState("Reflection");

		Properties p = new Properties();
		p.load(new FileInputStream(properties));

		boolean instance = !(obj instanceof Class<?>);
		Class<?> objClazz = instance ? obj.getClass() : (Class<?>) obj;
		HashMap<String, ArrayList<Field>> annotations = new HashMap<>();
		Field[] fields0 = objClazz.getDeclaredFields();//It's slow
		for (Field f : fields0) {
			Property property = f.getAnnotation(Property.class);
			if (property == null)
				continue;
			String pName = property.value();

			if (!annotations.containsKey(pName))
				annotations.put(pName, new ArrayList<>());
			annotations.get(pName).add(f);

			f.setAccessible(true);

		}

		Object modifyTarget = instance ? obj : null;

		for (Map.Entry<Object, Object> entry : p.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			ArrayList<Field> fields = annotations.get(key);
			if (fields == null)
				continue;

			for (Field field : fields) {
				Class<?> fieldClazz = field.getType();
				if (parsers.containsKey(fieldClazz)) {
					Parsable<?> parser = parsers.get(fieldClazz);
					field.set(modifyTarget, parser.parse(value));
				} else {
					LOGGER.warning("Not supported field type \"" + fieldClazz.getName() + "\" when setting key \"" + key
							+ "\" in field \"" + field.getName() + "\"");
				}

			}

//			Logger.getInstance().popState();

		}

	}

}
