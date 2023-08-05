package com.work.qq_system_springboot;

import java.util.*;

public class AEFSD {


    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        List<List<Integer>> items = new ArrayList();
        List<Integer> item = null;
        String str = input.next();
        for(int i=1;i<str.length()-1;i++){
            if(str.charAt(i)=='['){
                item = new ArrayList();
            }else if(str.charAt(i)==']'){
                items.add(item);
            }else if(str.charAt(i)==','){

            }else{
                item.add(str.charAt(i)-'0');
            }
        }

        int m = items.size();
        if(m==0){
            System.out.println(0);
            return;
        }
        int n = items.get(0).size();
        int dp[][] = new int[m][n];
        dp[0][0] = items.get(0).get(0);
        for(int i=1;i<m;i++)
            dp[i][0] = dp[i-1][0]+items.get(i).get(0);
        for(int i=1;i<n;i++)
            dp[0][i] = dp[0][i-1]+items.get(0).get(i);
        for(int i=1;i<m;i++){
            for(int j=1;j<n;j++){
                dp[i][j] = Math.max(dp[i-1][j],dp[i][j-1])+items.get(i).get(j);
            }
        }
        System.out.println(dp[m-1][n-1]);
    }


}

class Mjias{

    public static int washCount (int[][] g) {
        // write code here
        int nrow = g.length;
        int ncol = g[0].length;
        int matrix[][] = new int[ncol][nrow];
        for(int i=0;i<ncol;i++)
            for(int j=0;j<nrow;j++)
                matrix[i][j] = g[j][i];
        Arrays.sort(matrix, new Comparator<int[]>() {
            @Override
            public int compare(int[] o1, int[] o2) {
                int len1 = 0,len2 = 0;
                for(int i=0;i<o1.length;i++){
                    if(o1[i]==1)len1++;
                    if(o2[i]==1)len2++;
                }
                return len1-len2;
            }
        });

        int arr[] = new int[nrow];
        int res = 0;
        for(int i=0;i<ncol;i++){
            for(int j=0;j<nrow;j++){
                if(matrix[i][j]==1&&arr[j]==0){
                    res++;
                    arr[j] = 1;
                }
            }
        }
        return res;

    }

    public static void main(String[] args) {
        int matrix[][] = {
                {1,0,0},
                {0,1,1},
                {1,0,1},
        };
        int i = washCount(matrix);
        System.out.println(i);
    }
}
