package com.hongcha.pigeon.core.remoting.protocol.v1;

import com.hongcha.pigeon.core.error.PigeonException;

import java.io.*;
import java.lang.reflect.Type;

public class ObjectSerializable {
    /**
     * 对象转数组
     *
     * @param obj
     * @return
     */
    public static byte[] toByteArray(Object obj) {
        byte[] bytes = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
            oos.close();
            bos.close();
        } catch (IOException ex) {
            throw new PigeonException(ex);
        }
        return bytes;
    }

    public static <T> T toObject(byte[] bytes, Class<T> tClass) {
        Object o = toObject(bytes);
        return tClass.cast(o);
    }


    /**
     * 数组转对象
     *
     * @param bytes
     * @return
     */
    public static Object toObject(byte[] bytes) {
        Object obj = null;
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis);
            obj = ois.readObject();
            ois.close();
            bis.close();
        } catch (IOException ex) {
            throw new PigeonException(ex);
        } catch (ClassNotFoundException ex) {
            throw new PigeonException(ex);
        }
        return obj;
    }

    public static boolean isSerializable(Object o) {
        Type[] genericInterfaces = o.getClass().getGenericInterfaces();
        for (Type genericInterface : genericInterfaces) {
            if (genericInterface.equals(Serializable.class)) return true;
        }
        return false;
    }

}
