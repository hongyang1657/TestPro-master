package com.byids.hy.testpro.utils;

import android.util.Log;

import com.byids.hy.testpro.newBean.AllJsonData;
import com.byids.hy.testpro.newBean.CommandUser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by gqgz2 on 2016/10/25.
 */

public class NewJsonParseUtils {
    private String json11 = "{\n" +
            "    \"CommandUser\": {\n" +
            "        \"hid\": \"56e276f3736fb0872c69d876\",\n" +
            "        \"loginName\": \"byidsoffice\",\n" +
            "        \"password\": 123\n" +
            "    },\n" +
            "    \"CommandData\": {\n" +
            "        \"ower\": \"byidsoffice\",\n" +
            "        \"comments\": \"拜爱展厅\",\n" +
            "        \"address\": \"野芷湖西路创意天地\",\n" +
            "        \"telephone\": \"87743339\",\n" +
            "        \"package\": \"<套餐标识>\",\n" +
            "        \"profile\": {\n" +
            "            \"music\": {\n" +
            "                \"acitve\": 1,\n" +
            "                \"music_mach_type\": \"yz200\",\n" +
            "                \"muti_area_support\": 1,\n" +
            "                \"music_sound_card\": 1,\n" +
            "                \"voice_assistant_areaID\": 1\n" +
            "            },\n" +
            "            \"camera\": {\n" +
            "                \"active\": 1\n" +
            "            },\n" +
            "            \"door\": {\n" +
            "                \"active\": 1,\n" +
            "                \"array\": [\n" +
            "                    {\n" +
            "                        \"number\": 1,\n" +
            "                        \"protocol\": \"hdl\",\n" +
            "                        \"array\": [\n" +
            "                            {\n" +
            "                                \"operation\": \"open\",\n" +
            "                                \"software_mesg\": {},\n" +
            "                                \"hardware_mesg\": {\n" +
            "                                    \"subnet_id\": \"3\",\n" +
            "                                    \"device_id\": \"16\",\n" +
            "                                    \"type\": \"通用开关\",\n" +
            "                                    \"length\": \"2\",\n" +
            "                                    \"serial_area_id\": \"2\",\n" +
            "                                    \"value\": \"255\"\n" +
            "                                }\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"operation\": \"close\",\n" +
            "                                \"software_mesg\": {},\n" +
            "                                \"hardware_mesg\": {\n" +
            "                                    \"subnet_id\": \"3\",\n" +
            "                                    \"device_id\": \"16\",\n" +
            "                                    \"type\": \"通用开关\",\n" +
            "                                    \"length\": \"2\",\n" +
            "                                    \"serial_area_id\": \"3\",\n" +
            "                                    \"value\": \"255\"\n" +
            "                                }\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"security\": {\n" +
            "                \"active\": 1,\n" +
            "                \"array\": [\n" +
            "                    {\n" +
            "                        \"number\": 1,\n" +
            "                        \"protocol\": \"hdl\",\n" +
            "                        \"array\": [\n" +
            "                            {\n" +
            "                                \"operation\": \"normal\",\n" +
            "                                \"software_mesg\": {},\n" +
            "                                \"hardware_mesg\": {\n" +
            "                                    \"subnet_id\": \"2\",\n" +
            "                                    \"device_id\": \"13\",\n" +
            "                                    \"type\": \"通用开关\",\n" +
            "                                    \"length\": \"2\",\n" +
            "                                    \"serial_area_id\": \"2\",\n" +
            "                                    \"value\": \"255\"\n" +
            "                                }\n" +
            "                            },\n" +
            "                            {\n" +
            "                                \"operation\": \"close\",\n" +
            "                                \"software_mesg\": {},\n" +
            "                                \"hardware_mesg\": {\n" +
            "                                    \"subnet_id\": \"2\",\n" +
            "                                    \"device_id\": \"13\",\n" +
            "                                    \"type\": \"通用开关\",\n" +
            "                                    \"length\": \"2\",\n" +
            "                                    \"serial_area_id\": \"3\",\n" +
            "                                    \"value\": \"255\"\n" +
            "                                }\n" +
            "                            }\n" +
            "                        ]\n" +
            "                    }\n" +
            "                ]\n" +
            "            },\n" +
            "            \"rooms\": {\n" +
            "                \"active\": 1,\n" +
            "                \"array\": [\n" +
            "                    {\n" +
            "                        \"active\": 1,\n" +
            "                        \"protocol\": \"hdl\",\n" +
            "                        \"room_db_name\": \"woshi\",\n" +
            "                        \"room_zh_name\": \"卧室\",\n" +
            "                        \"room_dev_mesg\": {\n" +
            "                            \"light\": {\n" +
            "                                \"active\": 1,\n" +
            "                                \"protocol\": \"hdl\",\n" +
            "                                \"array\": [\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 1,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"床灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"1\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 2,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"门口灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"2\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 3,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"侧灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"3\"\n" +
            "                                        }\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            },\n" +
            "                            \"curtain\": {\n" +
            "                                \"active\": 1,\n" +
            "                                \"protocol\": \"hdl\",\n" +
            "                                \"array\": [\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 1,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"tag\": \"left\",\n" +
            "                                            \"type\": \"bulian\",\n" +
            "                                            \"displayer_name\": \"布帘\"\n" +
            "                                        },\n" +
            "                                        \"array\": [\n" +
            "                                            {\n" +
            "                                                \"operation\": \"open\",\n" +
            "                                                \"software_mesg\": {\n" +
            "                                                    \"tag\": \"left\",\n" +
            "                                                    \"type\": \"bulian\",\n" +
            "                                                    \"displayer_name\": \"布帘\"\n" +
            "                                                },\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"1\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"close\",\n" +
            "                                                \"software_mesg\": {\n" +
            "                                                    \"tag\": \"left\",\n" +
            "                                                    \"type\": \"bulian\",\n" +
            "                                                    \"displayer_name\": \"布帘\"\n" +
            "                                                },\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"2\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"pause\",\n" +
            "                                                \"software_mesg\": {\n" +
            "                                                    \"tag\": \"left\",\n" +
            "                                                    \"type\": \"bulian\",\n" +
            "                                                    \"displayer_name\": \"布帘\"\n" +
            "                                                },\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            },\n" +
            "                            \"music\": {\n" +
            "                                \"active\": 1,\n" +
            "                                \"area\": \"1\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    },\n" +
            "                    {\n" +
            "                        \"active\": 1,\n" +
            "                        \"protocol\": \"hdl\",\n" +
            "                        \"room_db_name\": \"keting\",\n" +
            "                        \"room_zh_name\": \"客厅\",\n" +
            "                        \"room_dev_mesg\": {\n" +
            "                            \"light\": {\n" +
            "                                \"active\": 1,\n" +
            "                                \"protocol\": \"hdl\",\n" +
            "                                \"array\": [\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 1,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"大灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"1\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 2,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"门口灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"2\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 3,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"侧灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"3\"\n" +
            "                                        }\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 4,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"display_name\": \"环灯\"\n" +
            "                                        },\n" +
            "                                        \"hardware_mesg\": {\n" +
            "                                            \"subnet_id\": \"1\",\n" +
            "                                            \"device_id\": \"12\",\n" +
            "                                            \"type\": \"回路控制\",\n" +
            "                                            \"length\": \"4\",\n" +
            "                                            \"serial_area_id\": \"4\"\n" +
            "                                        }\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            },\n" +
            "                            \"curtain\": {\n" +
            "                                \"active\": 1,\n" +
            "                                \"protocol\": \"hdl\",\n" +
            "                                \"array\": [\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 1,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"tag\": \"left\",\n" +
            "                                            \"type\": \"bulian\",\n" +
            "                                            \"displayer_name\": \"布帘\"\n" +
            "                                        },\n" +
            "                                        \"array\": [\n" +
            "                                            {\n" +
            "                                                \"operation\": \"open\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"close\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"pause\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 2,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"tag\": \"right\",\n" +
            "                                            \"type\": \"shalian\",\n" +
            "                                            \"displayer_name\": \"纱帘\"\n" +
            "                                        },\n" +
            "                                        \"array\": [\n" +
            "                                            {\n" +
            "                                                \"operation\": \"open\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"close\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"pause\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 3,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"tag\": \"right\",\n" +
            "                                            \"type\": \"shalian\",\n" +
            "                                            \"displayer_name\": \"纱帘\"\n" +
            "                                        },\n" +
            "                                        \"array\": [\n" +
            "                                            {\n" +
            "                                                \"operation\": \"open\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"close\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"pause\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    },\n" +
            "                                    {\n" +
            "                                        \"active\": 1,\n" +
            "                                        \"protocol\": \"hdl\",\n" +
            "                                        \"number\": 4,\n" +
            "                                        \"software_mesg\": {\n" +
            "                                            \"tag\": \"left\",\n" +
            "                                            \"type\": \"bulian\",\n" +
            "                                            \"displayer_name\": \"布帘\"\n" +
            "                                        },\n" +
            "                                        \"array\": [\n" +
            "                                            {\n" +
            "                                                \"operation\": \"open\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"1\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"close\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"3\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            },\n" +
            "                                            {\n" +
            "                                                \"operation\": \"pause\",\n" +
            "                                                \"software_mesg\": {},\n" +
            "                                                \"hardware_mesg\": {\n" +
            "                                                    \"subnet_id\": \"1\",\n" +
            "                                                    \"device_id\": \"12\",\n" +
            "                                                    \"type\": \"通用开关\",\n" +
            "                                                    \"length\": \"2\",\n" +
            "                                                    \"serial_area_id\": \"2\",\n" +
            "                                                    \"value\": \"255\"\n" +
            "                                                }\n" +
            "                                            }\n" +
            "                                        ]\n" +
            "                                    }\n" +
            "                                ]\n" +
            "                            },\n" +
            "                            \"music\": {\n" +
            "                                \"active\": 1,\n" +
            "                                \"area\": \"2\"\n" +
            "                            }\n" +
            "                        }\n" +
            "                    }\n" +
            "                ]\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";
    private String TAG = "result";

    /*public NewJsonParseUtils(String json) {
        this.json = json;
    }*/











    public AllJsonData newJsonParse(){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        AllJsonData allJsonData = gson.fromJson(json11,AllJsonData.class);      //把解析的数据保存在homeData中

        try {
            JSONObject jsonObject = new JSONObject(json11);
            JSONObject obj = jsonObject.getJSONObject("CommandData");
            String Package = obj.getString("package");
            Log.i(TAG, "newJsonParse: ！！！！！！！！！！！！！！！！！！！"+Package);
            Log.i(TAG, "newJsonParse: @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@"+allJsonData.getCommandData());
            allJsonData.getCommandData().setPackage(Package);
            JSONObject profile = obj.getJSONObject("profile");
            String profileStr = profile.toString();
            Log.i(TAG, "newJsonParse: ------------------------"+profileStr);

            Log.i(TAG, "newJsonParse: ----ower"+allJsonData.getCommandData().getOwer()+"----comments"+allJsonData.getCommandData().getComments()
                    +"-----address"+allJsonData.getCommandData().getAddress()+"----telephone"+allJsonData.getCommandData().getTelephone()+"----package"
                    +allJsonData.getCommandData().getPackage()+"------"+allJsonData.getCommandData().getProfile().getCamera().getActive()+"------"
                    +allJsonData.getCommandData().getProfile().getRooms().getArray().get(0).getActive()+"-------"
                    +allJsonData.getCommandData().getProfile().getRooms().getArray().get(0).getRoom_db_name()+"-----"
                    +allJsonData.getCommandData().getProfile().getRooms().getArray().get(0).getRoom_dev_mesg().getCurtain().getArray().get(0).getSoftware_mesg().getDisplayer_name());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return allJsonData;
    }
}
