package com.work.qq_system_springboot.service;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Queue;

@Component
public class PythonService {

    public void execPython(String root,String url,String userName){
        // TODO Auto-generated method stub
        Process proc;
        try {
            String[] arr = new String[]
                    { "python"
                            , "D:\\work\\data\\python_script\\爬取抖音用户.py"
                            , url
                            , root
                            , userName
                    };
            proc = Runtime.getRuntime().exec(arr);// 执行py文件

            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GB2312"));
            String line = null;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void execPythonUpdated(String root,String userName){
        // TODO Auto-generated method stub
        Process proc;
        try {
            String[] arr = new String[]
                    { "python"
                            , "D:\\work\\data\\python_script\\爬取抖音用户-进阶版.py"
                            , root
                            , userName
                    };
            proc = Runtime.getRuntime().exec(arr);// 执行py文件

            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GB2312"));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //爬取哔哩哔哩视频
    public void scrapyBilibiliMedia(String root,String userName,String url){
        // TODO Auto-generated method stub
        Process proc;
        try {
            String[] arr = new String[]
                    { "python"
                      ,"D:\\work\\data\\python_script\\爬取bilibili视频.py"
                      ,root
                      ,userName
                      ,url
                    };
            proc = Runtime.getRuntime().exec(arr);// 执行py文件

            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GB2312"));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //爬取抖音当前视频
    public void scrapyDouyinByUrl(String root,String url){
        // TODO Auto-generated method stub
        Process proc;
        try {
            String[] arr = new String[]
                    { "python"
                            ,"D:\\work\\data\\python_script\\爬取抖音当前视频.py"
                            ,root
                            ,url
                    };
            proc = Runtime.getRuntime().exec(arr);// 执行py文件

            //用输入输出流来截取结果
            BufferedReader in = new BufferedReader(new InputStreamReader(proc.getInputStream(),"GB2312"));
            String line;
            while ((line = in.readLine()) != null) {
                System.out.println(line);
            }
            in.close();
            proc.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
