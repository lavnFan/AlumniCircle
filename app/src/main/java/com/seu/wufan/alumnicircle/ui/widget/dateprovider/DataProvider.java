package com.seu.wufan.alumnicircle.ui.widget.dateprovider;

import android.content.res.AssetManager;


import com.seu.wufan.alumnicircle.common.App;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.Throwable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class DataProvider {

    public static class SeuMajors {
        /**
         * 此处使用ArrayList而不使用List接口是为了OptionsPickerView的使用
         */
        private ArrayList<String> departments;
        private ArrayList<ArrayList<String>> majors;

        public SeuMajors(ArrayList<String> departments, ArrayList<ArrayList<String>> majors) {
            this.departments = departments;
            this.majors = majors;
        }

        public ArrayList<String> getDepartments() {
            return departments;
        }

        public ArrayList<ArrayList<String>> getMajors() {
            return majors;
        }
    }

    public static ArrayList<String> getEnrollYearsData() {
        ArrayList<String> enrollYears = new ArrayList<>();
        int year = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = year - 80; i <= year; i++) {
            enrollYears.add(Integer.toString(i));
        }
        return enrollYears;
    }

    public static SeuMajors getSeuMajorsData() {

        ArrayList<String> departments = new ArrayList<>();
        ArrayList<ArrayList<String>> majors = new ArrayList<>();
        Map<String, ArrayList<String>> majorMap = new HashMap<>();

        StringBuilder buffer = new StringBuilder();
        AssetManager asset = App.getContext().getAssets();
        Scanner scanner = null;
        try {
            scanner = new Scanner(asset.open("seu_majors.json"));
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (null != scanner) {
                scanner.close();
            }
        }
        try {
            JSONArray array = new JSONArray(buffer.toString());
            JSONArray major = null;
            JSONObject obj = null;
            String departmentName = null;
            ArrayList<String> majorList = null;

            Comparator chineseSort = Collator.getInstance(java.util.Locale.CHINA);
            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                major = obj.getJSONArray("majors");
                departmentName = obj.getString("department_name");
                departments.add(departmentName);
                majorList = new ArrayList<>();
                for (int j = 0; j < major.length(); j++) {
                    majorList.add(major.getString(j));
                }
                //中文按拼音序排序
                Collections.sort(majorList, chineseSort);
                majorMap.put(departmentName, majorList);
            }
            Collections.sort(departments, chineseSort);
            for (int i = 0; i < departments.size(); i++) {
//                for(int j = 0; j < majorMap.get(departments.get(i)).size(); j++){
//                    System.out.println(majorMap.get(departments.get(i)).get(j));
//                }
                majors.add(majorMap.get(departments.get(i)));
            }
            return new SeuMajors(departments, majors);
        } catch (Exception e) {
            return null;
        }
    }

    public static class SeuIndustry {
        private ArrayList<String> name;
        private ArrayList<ArrayList<String>> careers;

        public SeuIndustry(ArrayList<String> name, ArrayList<ArrayList<String>> careers) {
            this.name = name;
            this.careers = careers;
        }

        public ArrayList<String> getName() {
            return name;
        }

        public void setName(ArrayList<String> name) {
            this.name = name;
        }

        public ArrayList<ArrayList<String>> getCareers() {
            return careers;
        }

        public void setCareers(ArrayList<ArrayList<String>> careers) {
            this.careers = careers;
        }
    }

    public static SeuIndustry getSeuIndustry() {
        ArrayList<String> names = new ArrayList<>();
        ArrayList<ArrayList<String>> careers = new ArrayList<>();
        Map<String, ArrayList<String>> careerMap = new HashMap<>();

        StringBuilder buffer = new StringBuilder();
        AssetManager asset = App.getContext().getAssets();
        Scanner scanner = null;
        try {
            scanner = new Scanner(asset.open("industry.json"));
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (null != scanner) {
                scanner.close();
            }
        }
        try {
            JSONArray array = new JSONArray(buffer.toString());
            JSONArray career = null;
            JSONObject obj = null;
            JSONObject careerItemObj = null;
            String industryName = null;
            ArrayList<String> careerList = null;

            for (int i = 0; i < array.length(); i++) {
                obj = array.getJSONObject(i);
                career = obj.getJSONArray("careers");
                industryName = obj.getString("name");
                names.add(industryName);
                careerList = new ArrayList<>();
                for (int j = 0; j < career.length(); j++) {    //对career json array 进行解析
                    careerItemObj = career.getJSONObject(j);
                    String careerItemName = careerItemObj.getString("name");
                    careerList.add(careerItemName);
                }
                careerMap.put(industryName, careerList);
            }
            for (int i = 0; i < names.size(); i++) {
                careers.add(careerMap.get(names.get(i)));
            }
            return new SeuIndustry(names, careers);
        } catch (Exception e) {
            return null;
        }
    }

}
