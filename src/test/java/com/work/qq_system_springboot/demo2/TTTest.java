package com.work.qq_system_springboot.demo2;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class TTTest {
    public static void quickSort(int[] arr,int low,int high){
        Scanner input = new Scanner(System.in);
        StringBuilder sb = new StringBuilder(input.next());
        sb.delete(0,1);
        System.out.println(sb);
    }


    public static void main(String[] args){
        Scanner input = new Scanner(System.in);
        String str = input.next();
        StringBuilder sb = new StringBuilder(str);

        int cnt = 0;

        boolean flag = false;
        while(!flag){
            int i=0;
            flag = true;
            while(i<sb.length()-1){
                if(sb.charAt(i)=='1'&&sb.charAt(i+1)=='0'){
                    sb.delete(i,i+2);
                    cnt += 2;
                    flag = false;
                }else{
                    i++;
                }
            }
        }


        System.out.println(cnt);
    }


}


