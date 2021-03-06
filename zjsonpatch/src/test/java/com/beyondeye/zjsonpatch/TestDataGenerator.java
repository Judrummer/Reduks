package com.beyondeye.zjsonpatch;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * User: gopi.vishwakarma
 * Date: 05/08/14
 */
public class TestDataGenerator {
    private static Random random = new Random();
    private static List<String> name = Arrays.asList("summers", "winters", "autumn", "spring", "rainy");
    private static List<Integer> age = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
    private static List<String> gender = Arrays.asList("male", "female");
    private static List<String> country = Arrays.asList("india", "aus", "nz", "sl", "rsa", "wi", "eng", "bang", "pak");
    private static List<String> friends = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j",
            "a", "b", "c", "d", "e", "f", "g", "h", "i", "j");

    public static JsonElement generate(int count) {
        JsonArray jsonNode = new JsonArray();
        for (int i = 0; i < count; i++) {
            JsonObject objectNode = new JsonObject();
            objectNode.addProperty("name", name.get(random.nextInt(name.size())));
            objectNode.addProperty("age", age.get(random.nextInt(age.size())));
            objectNode.addProperty("gender", gender.get(random.nextInt(gender.size())));
            JsonArray countryNode = getArrayNode(country.subList(random.nextInt(country.size() / 2), (country.size() / 2) + random.nextInt(country.size() / 2)));
            objectNode.add("country", countryNode);
            JsonArray friendNode = getArrayNode(friends.subList(random.nextInt(friends.size() / 2), (friends.size() / 2) + random.nextInt(friends.size() / 2)));
            objectNode.add("friends", friendNode);
            jsonNode.add(objectNode);
        }
        return jsonNode;
    }

    private static JsonArray getArrayNode(List<String> args) {
        JsonArray countryNode = new JsonArray();
        for(String arg : args){
            countryNode.add(arg);
        }
        return countryNode;
    }
}
