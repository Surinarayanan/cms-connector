package com.p3.content.utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @Author : Suri Aravind @Creation Date : 16/02/24
 */
public class Utility {
    public static Gson gson = new Gson();
    Utility() {}
    public static  <T> T mapUtils(String input, Class<T> tClass) {
        Type fooType = new TypeToken<List<T>>() {}.getType();
        return gson.fromJson(input, fooType);
    }
    public static  <T> T mapUtils(String input, Type fooType) {
        return gson.fromJson(input, fooType);
    }

    public static String readAll(String filePath){
        StringBuilder data = new StringBuilder();
        try(BufferedReader br = new BufferedReader(new FileReader(filePath));){
            String st;
            while ((st = br.readLine()) != null)
            data.append(st);
        }catch (Exception exception){

        }
        return data.toString();
    }
}
