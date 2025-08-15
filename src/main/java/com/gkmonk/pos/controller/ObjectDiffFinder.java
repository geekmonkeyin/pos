package com.gkmonk.pos.controller;

import com.gkmonk.pos.model.pod.PackedOrder;
import com.gkmonk.pos.model.pod.ProductDetails;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ObjectDiffFinder {

    public static Map<String, Object> findDiff(Object oldObj, Object newObj, String name) {
        return findDiffRecursive(oldObj, newObj, name);
    }

    private static Map<String, Object> findDiffRecursive(Object oldObj, Object newObj, String path) {
        Map<String, Object> diffMap = new HashMap<>();

        if (oldObj == null && newObj == null) return diffMap;
        if (oldObj == null || newObj == null || !oldObj.getClass().equals(newObj.getClass())) {
            diffMap.put(path, "Old: " + oldObj + ", New: " + newObj);
            return diffMap;
        }

        Class<?> clazz = oldObj.getClass();

        if (clazz.isPrimitive() || clazz == String.class || Number.class.isAssignableFrom(clazz) || clazz == Boolean.class) {
            if (!Objects.equals(oldObj, newObj)) {
                diffMap.put(path,newObj);
            }
            return diffMap;
        }

        if (Collection.class.isAssignableFrom(clazz)) {
            Collection<?> oldList = (Collection<?>) oldObj;
            Collection<?> newList = (Collection<?>) newObj;

            Iterator<?> oldIt = oldList.iterator();
            Iterator<?> newIt = newList.iterator();
            int index = 0;
            while (oldIt.hasNext() && newIt.hasNext()) {
                Object oldItem = oldIt.next();
                Object newItem = newIt.next();
                diffMap.putAll(findDiffRecursive(oldItem, newItem, path + "[" + index + "]"));
                index++;
            }
            while (oldIt.hasNext()) {
                diffMap.put(path + "[" + index + "]", "Old: " + oldIt.next() + ", New: null");
                index++;
            }
            while (newIt.hasNext()) {
                diffMap.put(path + "[" + index + "]", "Old: null, New: " + newIt.next());
                index++;
            }
            return diffMap;
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object oldValue = field.get(oldObj);
                Object newValue = field.get(newObj);
                diffMap.putAll(findDiffRecursive(oldValue, newValue, path + "." + field.getName()));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to access field", e);
            }
        }

        return diffMap;
    }

    public static void main(String[] args) {
        // Example usage
        // Replace with your real test
        // Class T -> X, Y definitions needed here

        ProductDetails productDetailsObj = new ProductDetails();
        productDetailsObj.setProductName("p1");

        PackedOrder original = new PackedOrder();
        List<ProductDetails> productDetailsList = new ArrayList<>();
        productDetailsList.add(productDetailsObj);
        original.setProductDetails(productDetailsList);

        PackedOrder newProduct = new PackedOrder();
        List<ProductDetails> newProductList = new ArrayList<>();
        newProductList.add(productDetailsObj);
        original.setProductDetails(productDetailsList);

        System.out.println(findDiff(original,newProduct,"cartonRequest"));

    }
}
