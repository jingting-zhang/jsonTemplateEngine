package com.justin;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONPath;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * Created by zhanglei on 2019/11/19
 *
 * json格式模板渲染引擎
 */
public class JsonTemplateEngine {
    private static final String ARRAY_FLAG = "[]";
    private static final String ARRAY_FLAG_PATTERN = "\\[\\]";
    /**
     * 渲染json模板，从<code>data</code>取数
     * @param template  jsonpath 模板
     * @param srcData   源json数据
     * @return  渲染后的json数据
     */
    public static JSONObject render(String template, JSONObject srcData) {
        JSONObject templateJO = JSON.parseObject(template);

        return render(templateJO, srcData);
    }

    public static JSONObject render(JSONObject template, JSONObject srcData) {
        JSONObject dstData = new JSONObject();

        template.forEach(new BiConsumer<String, Object>() {
            @Override
            public void accept(String s, Object o) {

                if (o instanceof JSONObject) {
                    dstData.put(s, render((JSONObject) o, srcData));
                } else if (o instanceof JSONArray) {
                    dstData.put(s, render((JSONArray) o, srcData));

                } else if (o instanceof String) {
                    //认为 string 类型应该都是 jsonpath 。是否支持 string 常量？？
                    Object oo = JSONPath.eval(srcData, (String) o);
                    if (oo != null) {
                        dstData.put(s, oo);
                    } else {
                        //目标不存在
                        dstData.put(s, null);
                    }
                } else {
                    dstData.put(s, o);
                }
            }
        });

        return dstData;
    }



    public static JSONArray renderArrayItem(JSONObject template, JSONObject srcData) {

        Map<String, JSONArray> map = new HashMap<>();
        JSONArray resultJA = new JSONArray();

        int arraySize = 0;

        template.forEach((s, o) -> {

            if (o instanceof JSONObject) {
                map.put(s, renderArrayItem((JSONObject) o, srcData));

            } else if (o instanceof JSONArray) {
                map.put(s, render((JSONArray) o, srcData));

            } else if (o instanceof String) {
                Object result = renderArrayItem((String) o, srcData);
                if (result instanceof JSONArray) {
                    map.put(s, (JSONArray) result);
                } else {
                    map.put(s, new JSONArray() {{
                        add(result);
                    }});
                }

            } else {
                //不应该出现
                throw new RuntimeException("template incorrect");
            }

        });

        //merge
        for (String s : map.keySet()) {
            if (arraySize > 0 && map.get(s).size() != arraySize) {
                //如果各个key的数组长度不一致，报错
                throw new RuntimeException("template incorrect array setting");
            }

            arraySize = map.get(s).size();
        }

        //map 里面各个属性的数组结构合并成按模板以对象为单位的数组结果
//        Map<String, List> simpleMap = new HashMap<>();
//        for (String s : map.keySet()) {
//            simpleMap.put(s, simply(map.get(s)));
//            arraySize = simpleMap.get(s).size();
//        }

//        for (int i = 0; i < arraySize; i++) {
//            JSONObject item = new JSONObject();
//            final int j = i;
//            map.forEach((k, v) -> {
//                if (j >= v.size()) {
//                    System.out.println("iiii");
//                }
//                item.put(k, v.get(j));
//            });
//
//            resultJA.add(item);
//        }

        resultJA = merge(map);

        return resultJA;
    }

    /**
     *
     * @param jsonPath
     * @param srcData
     * @return  object or jsonArray
     */
    public static Object renderArrayItem(String jsonPath, JSONObject srcData) {

        //查找第一个数组位置
        int arrLocation = jsonPath.indexOf(ARRAY_FLAG);
        if (arrLocation < 0) {
            //没找到
            return JSONPath.eval(srcData, jsonPath);
        }

        String arrJsonPath = jsonPath.substring(0, arrLocation);
        JSONArray ja = (JSONArray) JSONPath.eval(srcData, arrJsonPath);

        JSONArray resultJA = new JSONArray();

        for (int i = 0; i < ja.size(); i++) {
            resultJA.add(renderArrayItem(jsonPath.replaceFirst(ARRAY_FLAG_PATTERN, "[" + i + "]"), srcData));
        }

        return resultJA;
    }

    public static JSONArray render(JSONArray template, JSONObject srcData) {

        if (template.size() < 1) { //空数组
            return template;
        }

        Object item = template.get(0);
        if (item instanceof JSONObject) {
            return renderArrayItem((JSONObject) item, srcData);
        } else if (item instanceof JSONArray) {
            //数组的元素还是数组，应该不可能出现
            throw new RuntimeException("template incorrect array format");
        } else if (item instanceof String) {

            Object result = renderArrayItem((String) item, srcData);
            if (result instanceof JSONArray) {
                return (JSONArray) result;
            } else {
                return new JSONArray() {{
                    add(result);
                }};
            }
        } else {
            return new JSONArray() {{add(item);}};
        }
    }

    /**
     * 多维数组降维
     * @param multiList
     * @return
     */
    public static JSONArray simply(JSONArray multiList) {
        JSONArray resultList = new JSONArray();

        for (Object item : multiList) {
            if (item instanceof List) {
                for (Object item1 : (List) item) {
                    resultList.add(item1);
                }
//                resultList.addAll(simply((List) item));
            } else {
                resultList.add(item);
            }
        }

        return resultList;
    }

    public static JSONArray merge(Map<String, JSONArray> propJAMap) {
        boolean needSimply = true;
        JSONArray resultJA = new JSONArray();
        boolean isEnd = false;
        Map<String, JSONArray> simpleMap = new HashMap<>();

        for (int i = 0; ; i++) {
            JSONObject itemJO = new JSONObject();

            for (String prop : propJAMap.keySet()) {
                if (i >= propJAMap.get(prop).size()) {
                    isEnd = true;
                    break;
                }

                Object value = propJAMap.get(prop).get(i);

                if (value instanceof JSONArray) {
                    simpleMap.put(prop, (JSONArray) value);
                } else {
                    needSimply = false;
                }

                itemJO.put(prop, value);
            }

            if (isEnd) {
                break;
            }

            if (! needSimply) {
                resultJA.add(itemJO);
            } else {
                //先降维，再合并
                resultJA.add(merge(simpleMap));
//                break;
            }
        }

//        if (needSimply) {
//            resultJA.add(merge(simpleMap));
//        }

        return resultJA;
    }

    public static void main(String[] args) {
        String template = "{\n" +
                "  \"personal_info\": {\n" +
                "    \"email_address\": \"$.personal_info.email_address\",\n" +
                "    \"phone_number\": \"$.personal_info.phone_number\",\n" +
                "    \"name\": \"$.personal_info.name\",\n" +
                "    \"gender\": \"$.personal_info.gender\",\n" +
                "    \"birthday\": \"$.personal_info.birthday\",\n" +
                "    \"verified\": \"$.personal_info.verified\"\n" +
                "  },\n" +
                "  \"address_book\": {\n" +
                "    \"shipping_address\":{\n" +
                "      \"name\":\"$.address_book.shipping_address.name\",\n" +
                "      \"address\":\"$.address_book.shipping_address.address\",\n" +
                "      \"area\":\"$.address_book.shipping_address.area\",\n" +
                "      \"phone_number\":\"$.address_book.shipping_address.phone_number\"\n" +
                "    },\n" +
                "    \"billing_address\": {\n" +
                "      \"name\":\"$.address_book.billing_address.name\",\n" +
                "      \"address\":\"$.address_book.billing_address.address\",\n" +
                "      \"area\":\"$.address_book.billing_address.area\",\n" +
                "      \"phone_number\":\"$.address_book.billing_address.phone_number\"\n" +
                "    },\n" +
                "    \"other_address\": [\n" +
                "      {\n" +
                "        \"name\":\"$.address_book.other_address[0].name\",\n" +
                "        \"address\":\"$.address_book.other_address[0].address\",\n" +
                "        \"area\":\"$.address_book.other_address[0].area\",\n" +
                "        \"phone_number\":\"$.address_book.other_address[0].phone_number\"\n" +
                "      }\n" +
                "    ]\n" +
                "  },\n" +
                "  \"orders\": [\n" +
                "    {\n" +
                "      \"order_no\": \"$.orders[0].order_no\",\n" +
                "      \"order_time\": \"$.orders[0].order_time\",\n" +
                "      \"grand_total\": \"$.orders[0].grand_total\",\n" +
                "      \"sub_total\": \"$.orders[0].sub_total\",\n" +
                "      \"shipping_cost\": \"$.orders[0].shipping_cost\",\n" +
                "      \"promotion\": \"$.orders[0].promotion\",\n" +
                "      \"shipping_address\": {\n" +
                "        \"name\":\"$.orders[0].shipping_address.name\",\n" +
                "        \"address\":\"$.orders[0].shipping_address.address\",\n" +
                "        \"phone_number\":\"$.orders[0].shipping_address.phone_number\"\n" +
                "      },\n" +
                "      \"billing_address\": {\n" +
                "        \"name\":\"$.orders[0].billing_address.name\",\n" +
                "        \"address\":\"$.orders[0].billing_address.address\",\n" +
                "        \"phone_number\":\"$.orders[0].billing_address.phone_number\"\n" +
                "      },\n" +
                "      \"packages\":[\n" +
                "        {\n" +
                "          \"status\":\"$.orders[0].packages[0].status\",\n" +
                "          \"shipping\":\"$.orders[0].packages[0].shipping\",\n" +
                "          \"sold_by\":\"$.orders[0].packages[0].sold_by\",\n" +
                "          \"goods\": [\n" +
                "            {\n" +
                "              \"name\":\"$.orders[0].packages[0].goods[0].name\",\n" +
                "              \"price\":\"$.orders[0].packages[0].goods[0].price\",\n" +
                "              \"amount\":\"$.orders[0].packages[0].goods[0].amount\"\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"wishlist\": [\n" +
                "    {\n" +
                "      \"name\": \"$.wishlist[0].name\",\n" +
                "      \"total_item\": \"$.wishlist[0].total_item\",\n" +
                "      \"items\": [\n" +
                "        {\n" +
                "          \"name\": \"$.wishlist[0].items[0].name\",\n" +
                "          \"availability\": \"$.wishlist[0].items[0].availability\",\n" +
                "          \"price\": \"$.wishlist[0].items[0].price\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ],\n" +
                "  \"payment_method\": [\n" +
                "    {\n" +
                "    \"card_type\":\"$.payment_method[0].card_type\",\n" +
                "    \"ends_with\":\"$.payment_method[0].ends_with\",\n" +
                "    \"valid_until\":\"$.payment_method[0].valid_until\"\n" +
                "    }\n" +
                "    ]\n" +
                "}\n" +
                "\n";
        String input = "{\n" +
                "    \"personal_info\":{\n" +
                "        \"birthday\":\"06/21/2000\",\n" +
                "        \"email_address\":\"hz*****uck@126.com\",\n" +
                "        \"gender\":\"male\",\n" +
                "        \"name\":\"zho****e\",\n" +
                "        \"phone_number\":\"+08******359\"\n" +
                "    },\n" +
                "    \"wishlist\":[\n" +
                "        {\n" +
                "            \"total_item\":\"9\",\n" +
                "            \"name\":null,\n" +
                "            \"items\":[\n" +
                "                {\n" +
                "                    \"price\":\"60000\",\n" +
                "                    \"name\":\"Goo.N Excelent Soft Premium Pants Jumbo M isi 32\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"1790000\",\n" +
                "                    \"name\":\"YI LITE 4K Action Camera Original International Version - Hitam FREE Kaos Olahraga\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"265000\",\n" +
                "                    \"name\":\"Onix COGNOS Action Camera 1080p CYGNUS - 12MP\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"339000\",\n" +
                "                    \"name\":\"Kurma Sukari - Sukkary 3kg (1dus)\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"369000\",\n" +
                "                    \"name\":\"Paket Mikrofon PROFFESIONAL 2 Microphone Taffware BM700 + Stand Gantung + Pop Filter + Splitter / Paket Smule Karaoke Lazpedia\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"3299000\",\n" +
                "                    \"name\":\"Oppo F3 4GB/64GB Gold\\u2013 Smartphone Dual Selfie Camera (Garansi Resmi Oppo Indonesia, Cicilan Tanpa Kartu Kredit, Gratis Ongkir)\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"165000\",\n" +
                "                    \"name\":\"HouseOfOrganix - Natural Whole Almond - 1000gr\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"499000\",\n" +
                "                    \"name\":\"JBL Clip 2 Bluetooth Speaker - Biru\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"2399000\",\n" +
                "                    \"name\":\"Xiaomi Mi A1 64GB - Black - Snapdragon 625\",\n" +
                "                    \"availability\":null\n" +
                "                }\n" +
                "            ]\n" +
                "        },\n" +
                "{\n" +
                "            \"total_item\":\"9\",\n" +
                "            \"name\":null,\n" +
                "            \"items\":[\n" +
                "                {\n" +
                "                    \"price\":\"60000\",\n" +
                "                    \"name\":\"Goo.N Excelent Soft Premium Pants Jumbo M isi 32\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"1790000\",\n" +
                "                    \"name\":\"YI LITE 4K Action Camera Original International Version - Hitam FREE Kaos Olahraga\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"265000\",\n" +
                "                    \"name\":\"Onix COGNOS Action Camera 1080p CYGNUS - 12MP\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"339000\",\n" +
                "                    \"name\":\"Kurma Sukari - Sukkary 3kg (1dus)\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"369000\",\n" +
                "                    \"name\":\"Paket Mikrofon PROFFESIONAL 2 Microphone Taffware BM700 + Stand Gantung + Pop Filter + Splitter / Paket Smule Karaoke Lazpedia\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"3299000\",\n" +
                "                    \"name\":\"Oppo F3 4GB/64GB Gold\\u2013 Smartphone Dual Selfie Camera (Garansi Resmi Oppo Indonesia, Cicilan Tanpa Kartu Kredit, Gratis Ongkir)\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"165000\",\n" +
                "                    \"name\":\"HouseOfOrganix - Natural Whole Almond - 1000gr\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"499000\",\n" +
                "                    \"name\":\"JBL Clip 2 Bluetooth Speaker - Biru\",\n" +
                "                    \"availability\":null\n" +
                "                },\n" +
                "                {\n" +
                "                    \"price\":\"2399000\",\n" +
                "                    \"name\":\"Xiaomi Mi A1 64GB - Black - Snapdragon 625\",\n" +
                "                    \"availability\":null\n" +
                "                }\n" +
                "            ]\n" +
                "        }\n" +
                "    ],\n" +
                "    \"orders\":[\n" +
                "        {\n" +
                "            \"order_no\":\"2062******2403\",\n" +
                "            \"shipping_cost\":\"26500\",\n" +
                "            \"sub_total\":\"2253000\",\n" +
                "            \"billing_address\":{\n" +
                "                \"address\":\"1212***df, A***, Bu***, L***-\",\n" +
                "                \"name\":\"hzx at th\",\n" +
                "                \"phone_number\":\"08*****4\"\n" +
                "            },\n" +
                "            \"order_time\":\"09/05/2018\",\n" +
                "            \"grand_total\":\"2279500\",\n" +
                "            \"shipping_address\":{\n" +
                "                \"address\":\"1212***df, A***, Bu***, L***-\",\n" +
                "                \"name\":\"hzx at th\",\n" +
                "                \"phone_number\":\"08*****4\"\n" +
                "            },\n" +
                "            \"packages\":[\n" +
                "                {\n" +
                "                    \"shipping\":null,\n" +
                "                    \"sold_by\":\"Lazada E-Services Philippines\",\n" +
                "                    \"goods\":[\n" +
                "                        {\n" +
                "                            \"amount\":\"1\",\n" +
                "                            \"price\":\"2253000\",\n" +
                "                            \"name\":\"Apple iPhone 6s Plus Gold 32GB\"\n" +
                "                        }\n" +
                "                    ],\n" +
                "                    \"status\":null\n" +
                "                }\n" +
                "            ],\n" +
                "            \"promotion\":\"0\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"address_book\":{\n" +
                "        \"other_address\":[\n" +
                "            {\n" +
                "                \"area\":\"B***,K**. T****,T*****n\",\n" +
                "                \"address\":\"test2\",\n" +
                "                \"name\":\"zh****g\",\n" +
                "                \"phone_number\":\"08*****678\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"billing_address\":{\n" +
                "            \"area\":null,\n" +
                "            \"address\":null,\n" +
                "            \"name\":null,\n" +
                "            \"phone_number\":null\n" +
                "        },\n" +
                "        \"shipping_address\":{\n" +
                "            \"area\":\"D** J****,K*** Jak*** U***,K***\",\n" +
                "            \"address\":\"ke***an t**u u***a\",\n" +
                "            \"name\":\"zh****g\",\n" +
                "            \"phone_number\":\"08******59\"\n" +
                "        }\n" +
                "    },\n" +
                "    \"payment_method\":[\n" +
                "        {\n" +
                "            \"ends_with\":\"5719\",\n" +
                "            \"card_type\":\"VISA\",\n" +
                "            \"valid_util\":\"07/2020\"\n" +
                "        }\n" +
                "    ]\n" +
                "}";

        template = template.replaceAll("\\[0\\]", "[]");
        System.out.println(JSON.toJSONString(render(template, JSON.parseObject(input)), SerializerFeature.WriteMapNullValue));
    }
}
