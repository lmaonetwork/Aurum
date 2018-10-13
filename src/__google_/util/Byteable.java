package __google_.util;

import java.lang.reflect.Constructor;

public interface Byteable {
	default byte[] toBytes(){
		ByteZip zip = toByteZip();
		if(zip != null)return zip.build();
		return Coder.toBytes(toString());
	}

	default ByteZip toByteZip(){
		return null;
	}

	static <T extends Byteable> T toByteable(byte array[], Class<T> clazz){
		Constructor<T> constructor = Reflect.getConstructor(clazz, ByteUnzip.class);
		if(constructor != null)return Reflect.create(constructor, new ByteUnzip(array));
		constructor = Reflect.getConstructor(clazz, byte[].class);
		if(constructor != null)return Reflect.create(constructor, array);
		throw new IllegalArgumentException("No such constructor :c");
	}
}
