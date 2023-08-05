package com.work.qq_system_springboot.tools;

import com.jhlabs.image.QuantizeFilter;
import org.apache.commons.io.FileUtils;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.text.SimpleDateFormat;
import java.util.*;


public class FileOperation {

    //保存文件上传的进度条
    private static Map<String,Double> progressMap = new HashMap<>();
    public static Double getProgressById(String id){
        return progressMap.get(id)==null?0.0:progressMap.get(id);
    }
    public static void setProgressById(String id,Double progress){
        progressMap.put(id,progress);
    }


    //获取当前日期
    public static String getTime()
    {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(new Date());
    }

    //根据某一特征进行排序
    public static void sort(File []files,String sortName,String isAsc){



        if(sortName.equals("时间")){//按照日期排序
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    long diff = o1.lastModified()-o2.lastModified();
                    if(isAsc.equals("升序")){
                        if(diff>0)
                            return 1;
                        else if(diff==0)
                            return 0;
                        else
                            return -1;
                    }
                    if(diff>0)
                        return -1;
                    else if(diff==0)
                        return 0;
                    else
                        return 1;
                }
            });
        }else if(sortName.equals("名称")){//按照文件名称进行排序
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    if(isAsc.equals("升序")){
                        if(o1.isDirectory()&&o2.isFile())
                            return 1;
                        if(o1.isFile()&&o2.isDirectory())
                            return -1;
                        return o1.getName().compareTo(o2.getName());
                    }
                    if(o1.isDirectory()&&o2.isFile())
                        return -1;
                    if(o1.isFile()&&o2.isDirectory())
                        return 1;
                    return o2.getName().compareTo(o1.getName());
                }
            });
        }else if(sortName.equals("大小")){
            Arrays.sort(files, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {

                    long diff = o1.length() - o2.length();
                    if(isAsc.equals("升序")){
                        if(diff>=0)
                            return 1;
                        return -1;
                    }
                    if(diff>=0)
                        return -1;
                    return 1;
                }
            });
        }
    }

    //移动目录
    public static void removeDir(String oldDir,String newDir) throws IOException {
        Path oldPath = Paths.get(oldDir);
        Path newPath = Paths.get(newDir);
        Files.move(oldPath,newPath);
    }


    //移动文件
    public static boolean removeFile(String fileName,String destinationFloderUrl)
    {
        File file = new File(fileName);
        File destFloder = new File(destinationFloderUrl);
        //检查目标路径是否合法
        if(destFloder.exists())
        {
            if(destFloder.isFile())
            {
                System.out.println("目标路径是个文件，请检查目标路径！");
                return false;
            }
        }else
        {
            if(!destFloder.mkdirs())
            {
                System.out.println("目标文件夹不存在，创建失败！");
                return false;
            }
        }
        //检查源文件是否合法
        if(file.isFile() &&file.exists())
        {
            String destinationFile = destinationFloderUrl+"/"+file.getName();
            if(!file.renameTo(new File(destinationFile)))
            {
                System.out.println("移动文件失败！");
                return false;
            }
        }else
        {
            System.out.println("要备份的文件路径不正确，移动失败！");
            return false;
        }
        System.out.println("已成功移动文件"+file.getName()+"到"+destinationFloderUrl);
        return true;
    }


    /**
     * 判断目录是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsDir(File file) {
        // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    /**
     * 判断文件是否存在，不存在则判断是否创建成功
     *
     * @param file 文件
     * @return {@code true}: 存在或创建成功<br>{@code false}: 不存在或创建失败
     */
    public static boolean createOrExistsFile(File file) {
        if (file == null) return false;
        // 如果存在，是文件则返回true，是目录则返回false
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            return file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将输入流写入文件
     *
     * @param file   文件
     * @param is     输入流
     * @param append 是否追加在文件末
     * @return {@code true}: 写入成功<br>{@code false}: 写入失败
     */
    public static boolean writeFileFromIS(File file, InputStream is, boolean append) throws IOException {
        if (file == null || is == null) return false;
        if (!createOrExistsFile(file)) return false;
        OutputStream os = null;
        try {
            os = new BufferedOutputStream(new FileOutputStream(file, append));
            byte data[] = new byte[1024];
            int len;
            while ((len = is.read(data, 0, 1024)) != -1) {
                os.write(data, 0, len);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            is.close();
            os.close();
        }
    }

    /**
     * 复制或移动文件
     *
     * @param srcFile  源文件
     * @param destFile 目标文件
     * @param isMove   是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    public static boolean copyOrMoveFile(File srcFile, File destFile, boolean isMove) throws IOException {
        if (srcFile == null || destFile == null) return false;
        // 源文件不存在或者不是文件则返回false
        if (!srcFile.exists() || !srcFile.isFile())
        {
            System.out.println("源文件不存在或者不是文件则返回false");
            return false;}
        if(destFile.isDirectory()){
            destFile = new File(destFile.getPath()+"/"+srcFile.getName());
        }
        // 目标文件存在且是文件则返回false
        if (destFile.exists() && destFile.isFile()){
            System.out.println("目标文件存在且是文件则返回false");
            return false;
        }
        // 目标目录不存在返回false
        if (!createOrExistsDir(destFile.getParentFile())){
            System.out.println("目标目录不存在返回false");
            return false;
        }
        try {
            return writeFileFromIS(destFile, new FileInputStream(srcFile), false)
                    && !(isMove && !deleteFile(srcFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteFile(File file) {
        return file != null && (!file.exists() || file.isFile() && file.delete());
    }

    //移动或复制文件或目录
    public static boolean removeOrCopyDirOrFile(File srcDir,File destDir,boolean isMove) throws IOException {
        if(srcDir.isDirectory()){
            System.out.println("isDir");
            destDir = new File(destDir.getPath()+"/"+srcDir.getName());
            return copyOrMoveDir(srcDir,destDir,isMove);
        }else{
            System.out.println("isFile");
            return copyOrMoveFile(srcDir,destDir,isMove);
        }
    }

    /**
     * 复制或移动目录
     *
     * @param srcDir  源目录
     * @param destDir 目标目录
     * @param isMove  是否移动
     * @return {@code true}: 复制或移动成功<br>{@code false}: 复制或移动失败
     */
    public static boolean copyOrMoveDir(File srcDir, File destDir, boolean isMove) throws IOException {
        if (srcDir == null || destDir == null) return false;
        // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
        // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
        // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
        // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
        String srcPath = srcDir.getPath() + File.separator;
        String destPath = destDir.getPath() + File.separator;
        if (destPath.contains(srcPath)) return false;
        // 源文件不存在或者不是目录则返回false
        if (!srcDir.exists() || !srcDir.isDirectory()) return false;
        // 目标目录不存在返回false
        if (!createOrExistsDir(destDir)) return false;
        File[] files = srcDir.listFiles();
        for (File file : files) {
            File oneDestFile = new File(destPath + file.getName());
            if (file.isFile()) {
                // 如果操作失败返回false
                if (!copyOrMoveFile(file, oneDestFile, isMove)) return false;
            } else if (file.isDirectory()) {
                // 如果操作失败返回false
                if (!copyOrMoveDir(file, oneDestFile, isMove)) return false;
            }
        }
        return !isMove || deleteDir(srcDir);
    }

    //获取当前目录下面的内容
    public static String getContentOfDir(String dir,String sortName,String isAsc)
    {
        File file = new File(dir);
        //如果文件不存在或文件夹里面是空记录
        if(!file.exists()||file.listFiles().length==0)
            return "空记录";



        //如果不是空记录
        String res = "";//要返回的字符串
        File[] listFiles = file.listFiles();

        //排序
        sort(listFiles,sortName,isAsc);

        for (File listFile : listFiles) {
            res += listFile.getName()+","+listFile.isFile()+";";
        }
        res = res.substring(0,res.length()-1);//去掉最后一个分号
        return res;
    }

    //获取当前目录下面的音频列表
    public static List<String> getMediaList(String dir,String sortName,String isAsc){
        List<String> mediaList = new ArrayList<>();
        File file = new File(dir);
        if(!file.exists()||file.listFiles().length==0)
            return mediaList;

        //排序
        File[] listFiles = file.listFiles();
        sort(listFiles,sortName,isAsc);
        for(File temp:listFiles){
            if(temp.isFile()){
                String fileName = temp.getName();
                String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
                if(suffix.equals("mp4")||suffix.equals("mp3")
                        ||suffix.equals("jpg")||suffix.equals("jpeg")||suffix.equals("png")){
                    mediaList.add(fileName);
                }
            }
        }
        return mediaList;
    }

//    //删除某个目录及其该目录下面的所有内容
//    public static void deleteDir(File path)
//    {
//        if(path.isFile())
//            path.delete();
//        else
//        {
//            File[] items = path.listFiles();
//            for (File item : items) {
//                deleteDir(item);
//            }
//            path.delete();
//        }
//    }


    /**
     * 删除目录
     *
     * @param dir 目录
     * @return {@code true}: 删除成功<br>{@code false}: 删除失败
     */
    public static boolean deleteDir(File dir) {
        if (dir == null) return false;
        // 目录不存在返回true
        if (!dir.exists()) return true;
        // 现在文件存在且是文件夹
        File[] files = dir.listFiles();
        if (files != null && files.length != 0) {
            for (File file : files) {
                if (file.isFile()) {
                    if (!deleteFile(file)) return false;
                } else if (file.isDirectory()) {
                    if (!deleteDir(file)) return false;
                }
            }
        }
        return dir.delete();
    }

    //读取文件
    public static String readTxt(String path) throws IOException{
        String res = "";//要返回的字符串
        FileInputStream inputStream = new FileInputStream(path);//文件输入流
        if(path.endsWith(".txt")||path.endsWith(".py"))//读取txt文件
        {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"utf8");
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line!=null)
            {
                res += line+"\n";
                line = reader.readLine();
            }
            reader.close();
            inputStreamReader.close();
        }
        else if(path.endsWith(".docx")||path.endsWith(".doc"))
        {
            try {
                XWPFDocument document = new XWPFDocument(inputStream);
                XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                res = extractor.getText();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                res = "这里面没有记录或出现了异常！";
            }
        }

        inputStream.close();
        return res;
    }

    public static Map<String,String> getFileInfo(String path) throws IOException {
        Map<String,String> map = new HashMap<>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        File file = new File(path);
        long fileBytes = FileUtils.sizeOf(file);
        String fileSize = FileUtils.byteCountToDisplaySize(fileBytes);
        BasicFileAttributes basicFileAttributes = Files.readAttributes(Paths.get(path), BasicFileAttributes.class);
        FileTime creationTime = basicFileAttributes.creationTime();
        FileTime lastAccessTime = basicFileAttributes.lastAccessTime();
        FileTime lastModifiedTime = basicFileAttributes.lastModifiedTime();
        map.put("fileSize",fileSize);
        map.put("creationTime",format.format(creationTime.toMillis()));
        map.put("lastAccessTime",format.format(lastAccessTime.toMillis()));
        map.put("lastModifiedTime",format.format(lastModifiedTime.toMillis()));
        if(file.isDirectory())
            map.put("numsOfItems",file.listFiles().length+"");
        return map;
    }

    //写入文件
    public static void writeTxt(String path,String content,boolean isAppend) throws IOException {
        //文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(path, isAppend);
        if(path.endsWith(".txt"))
        {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(fileOutputStream, "UTF-8");
            BufferedWriter writer = new BufferedWriter(outputStreamWriter);
            writer.write(content);
            writer.close();
            outputStreamWriter.close();
        }
        else if(path.endsWith(".doc")||path.endsWith(".docx"))
        {
            XWPFDocument document = new XWPFDocument();
            XWPFParagraph paragraph = document.createParagraph();
            XWPFRun run = paragraph.createRun();
            run.setText(content);
            //输出流
            BufferedOutputStream stream = new BufferedOutputStream(fileOutputStream,1024);
            document.write(stream);
            stream.close();
        }
        fileOutputStream.close();
    }

    //写入日志
    public static void writeLog(String msg,String dir,String fileName) throws IOException {
        File file = new File(dir);
        if(!file.exists())file.mkdirs();
        file = new File(dir+"/"+fileName);
        if(!file.exists())file.createNewFile();
        writeTxt(dir+"/"+fileName,msg,true);
    }


    public static byte[] streamToByteArray(InputStream is)throws Exception{
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] b = new byte[1024];

        int len;
        while ((len=is.read(b))!=-1){
            bos.write(b,0,len);
        }
        byte[] array = bos.toByteArray();
        bos.close();
        return array;
    }

}
