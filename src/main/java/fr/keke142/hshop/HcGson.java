package fr.keke142.hshop;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


public class HcGson {
  
  //https://gist.github.com/RingOfStorms/8a06f895250d6cec9c87
  
  private final static String CLASS_KEY = "SERIAL-ADAPTER-CLASS-KEY";

  private final static Gson gson;
  private final static Type serializeType;

  static {
    serializeType = new TypeToken<Map<String, Object>>() {}.getType();
    gson = new Gson();
  }

  static public String serializeItemStack(ItemStack item) {

    Map<String, Object> serial = item.serialize();

    if (serial.get("meta") != null) {
      ItemMeta itemMeta = item.getItemMeta();

      Map<String, Object> originalMeta = itemMeta.serialize();
      Map<String, Object> meta = new HashMap<String, Object>();
      for (Entry<String, Object> entry : originalMeta.entrySet())
        meta.put(entry.getKey(), entry.getValue());
      Object o;
      for (Entry<String, Object> entry : meta.entrySet()) {
        o = entry.getValue();
        if (o instanceof ConfigurationSerializable) {
          ConfigurationSerializable serializable = (ConfigurationSerializable) o;
          Map<String, Object> serialized = recursiveSerialization(serializable);
          meta.put(entry.getKey(), serialized);
        }
      }
      serial.put("meta", meta);
    }

    return gson.toJson(serial);
  }

  static public ItemStack deserializeItemStack(String raw) {
    Map<String, Object> keys = gson.fromJson(raw, serializeType);

    if (keys.get("amount") != null) {
      Double d = (Double) keys.get("amount");
      Integer i = d.intValue();
      keys.put("amount", i);
    }

    ItemStack item;
    try {
      item = ItemStack.deserialize(keys);
    } catch (Exception e) {
      return null;
    }

    if (item == null)
      return null;

    if (keys.containsKey("meta")) {
      @SuppressWarnings("unchecked")
      Map<String, Object> itemmeta = (Map<String, Object>) keys.get("meta");
      itemmeta = recursiveDoubleToInteger(itemmeta);
      ItemMeta meta =
          (ItemMeta) ConfigurationSerialization.deserializeObject(itemmeta,
              ConfigurationSerialization.getClassByAlias("ItemMeta"));
      item.setItemMeta(meta);
    }

    return item;

  }

  private static Map<String, Object> recursiveSerialization(ConfigurationSerializable o) {
    Map<String, Object> originalMap = o.serialize();
    Map<String, Object> map = new HashMap<String, Object>();
    for (Entry<String, Object> entry : originalMap.entrySet()) {
      Object o2 = entry.getValue();
      if (o2 instanceof ConfigurationSerializable) {
        ConfigurationSerializable serializable = (ConfigurationSerializable) o2;
        Map<String, Object> newMap = recursiveSerialization(serializable);
        newMap.put(CLASS_KEY, ConfigurationSerialization.getAlias(serializable.getClass()));
        map.put(entry.getKey(), newMap);
      }
    }
    map.put(CLASS_KEY, ConfigurationSerialization.getAlias(o.getClass()));
    return map;
  }

  private static Map<String, Object> recursiveDoubleToInteger(Map<String, Object> originalMap) {
    Map<String, Object> map = new HashMap<String, Object>();
    for (Entry<String, Object> entry : originalMap.entrySet()) {
      Object o = entry.getValue();
      if (o instanceof Double) {
        Double d = (Double) o;
        Integer i = d.intValue();
        map.put(entry.getKey(), i);
      } else if (o instanceof Map) {
        @SuppressWarnings("unchecked")
        Map<String, Object> subMap = (Map<String, Object>) o;
        map.put(entry.getKey(), recursiveDoubleToInteger(subMap));
      } else {
        map.put(entry.getKey(), o);
      }
    }
    return map;
  }
}
