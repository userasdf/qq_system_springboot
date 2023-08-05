package com.work.qq_system_springboot.demo;
import com.work.qq_system_springboot.tools.QQSystemUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExecPythonTest {



        public static void main(String[] args) {
            String text = "阿斯蒂芬https://b23.tv/ATD470m你好";

            String urlFromText = QQSystemUtil.getUrlFromText(text);
            System.out.println(urlFromText);
        }
}
