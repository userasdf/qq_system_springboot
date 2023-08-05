package com.work.qq_system_springboot.controller;

import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.service.OprLogService;
import com.work.qq_system_springboot.service.PythonService;
import com.work.qq_system_springboot.tools.FileOperation;
import com.work.qq_system_springboot.tools.HostHolder;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/file")
public class FileController implements QQSystemConstant{

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private PythonService pythonService;

    @Autowired
    private OprLogService oprLogService;

    @Value("${community.file_system.path}")
    private String fileRoot;



//    //文件上传
//    @RequestMapping("/upload")
//    public String upload(String tempDir, String fileName, MultipartFile uploadFile,
//                         HttpServletRequest request, Model model,String isAbsolutePath) throws IOException {
//
//
//        //获取当前目录，处理中文乱码
//        tempDir = URLDecoder.decode(tempDir,"utf8");
//
//        //获取当前路径，若不存在，则创建
//        String root = fileRoot+"/"+tempDir;
//        if(isAbsolutePath.equals("1"))
//            root = tempDir;
//
//        System.out.println("root:"+root);
//
//        //获取用户输入的文件名字
//        fileName = fileName.trim();//首先截取字符串两端的空格
//        if("".equals(fileName))//如果用户没有输入文件名字，就使用文件原始名字
//            fileName = uploadFile.getOriginalFilename();
//        String filePath = root+"/"+fileName;
//        System.out.println("filePath:"+fileName);
//        //上传文件
//        File file = new File(filePath);
//        uploadFile.transferTo(file);
//        System.out.println("success upload!");
//        //将dir返回
//        model.addAttribute("tempDir",tempDir);
//        model.addAttribute("isAbsolutePath",isAbsolutePath);
//
//        //记录日志
//        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
//        oprLog.setUserId(hostHolder.get().getId());
//        oprLog.setUserName(hostHolder.get().getUsername());
//        oprLog.setInfo("上传了文件：\""+fileName+"\"");
//        oprLog.setType(LOG_TYPE_FILE);
//        oprLogService.addOprLog(oprLog);
//        return "index";
//    }

    //根据id获取文件上传进度条
    @RequestMapping("/getUploadProgress")
    @ResponseBody
    public Double getUploadProgress(){
        return FileOperation.getProgressById("id_"+hostHolder.get().getId());
    }

    //文件上传
    @RequestMapping("/upload")
    @ResponseBody
    public String upload(String tempDir, String fileName, MultipartFile uploadFile,
                         HttpServletRequest request, Model model,String isAbsolutePath) throws IOException {

        System.out.println("上传中。。。。。。");


        //获取当前目录，处理中文乱码
        tempDir = URLDecoder.decode(tempDir,"utf8");

        //获取当前路径，若不存在，则创建
        String root = fileRoot+"/"+tempDir;
        if(isAbsolutePath.equals("1"))
            root = tempDir;

        //获取用户输入的文件名字
        fileName = fileName.trim();//首先截取字符串两端的空格
        if("".equals(fileName))//如果用户没有输入文件名字，就使用文件原始名字
            fileName = uploadFile.getOriginalFilename();
        String filePath = root+"/"+fileName;
        System.out.println("filePath:"+fileName);
        //上传文件
        OutputStream outputStream = new FileOutputStream(filePath);
        InputStream inputStream = uploadFile.getInputStream();
        double havaUploadSize = 0;
        byte b[] = new byte[10240];
        int len;
        while ((len = inputStream.read(b))!=-1){
            outputStream.write(b,0,len);
            outputStream.flush();
            havaUploadSize += len;
            FileOperation.setProgressById("id_"+hostHolder.get().getId(),havaUploadSize);
        }
        outputStream.close();
        System.out.println("success upload!");

        //将dir返回
        model.addAttribute("tempDir",tempDir);
        model.addAttribute("isAbsolutePath",isAbsolutePath);

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("上传了文件：\""+fileName+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);

        //将上传进度设置为0
        FileOperation.setProgressById("id_"+hostHolder.get().getId(),0.0);
        return "index";
    }

    //显示当前文件夹中的内容
    @RequestMapping(value = "/loadContent",produces = "text/html;charset=utf8")
    @ResponseBody
    public String loadContent(String tempDir,String sortName,String isAsc,String isAbsolutePath,HttpServletRequest request) throws UnsupportedEncodingException {



        //处理中文乱码，获取文件夹名字
        tempDir = URLDecoder.decode(tempDir,"utf8");

        //获取文件夹路径
        String path = fileRoot+"/"+tempDir;
        if(isAbsolutePath.equals("1"))
            path = tempDir;



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("访问了路径：\""+tempDir+"\"下的内容");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);


        //返回当前目录下面的文件及文件夹
        return FileOperation.getContentOfDir(path,sortName,isAsc);
    }


    //新建文件夹
    @RequestMapping(value = "/createFolder",produces = "text/html;charset=utf8")
    @ResponseBody
    public void createFolder(String tempDir,String newFolder,HttpServletRequest request,String isAbsolutePath) throws IOException {

        //处理中文乱码，获取文件夹名字
        tempDir = URLDecoder.decode(tempDir, "utf8");
        newFolder = URLDecoder.decode(newFolder, "utf8");

        //获取文件夹路径
        String path = fileRoot+"/"+tempDir+"/"+newFolder;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+newFolder;
        //创建文件夹
        new File(path).mkdirs();


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("新建了文件夹：\""+newFolder+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);

    }

    //新建记事本
    @RequestMapping("/createTxt")
    @ResponseBody
    public void createTxt(String tempDir,String fileName,HttpServletRequest request,String isAbsolutePath) throws IOException {
        //处理中文乱码，获取文件夹名字
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");

        //获取文件夹路径
        String path = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+fileName;
        //创建文件
        new File(path).createNewFile();



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("新建了记事本：\""+fileName+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);
    }

    //文件下载
    @RequestMapping("/down")
    @ResponseBody
    public void down(String tempDir, String fileName, HttpServletResponse response,HttpServletRequest request,String isAbsolutePath) throws Exception{

        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");

        //模拟文件路径
        String str = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            str = tempDir+"/"+fileName;


        //获取输入流
        InputStream bis = new BufferedInputStream(new FileInputStream(new File(str)));
        //转码，免得文件名中文乱码
        fileName = URLEncoder.encode(fileName,"UTF-8");
        //设置文件下载头
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        //设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("multipart/form-data");
        BufferedOutputStream out = new BufferedOutputStream(response.getOutputStream());
        int len = 0;
        while((len = bis.read()) != -1){
            out.write(len);
        }
        out.flush();
        out.close();

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("下载了文件：\""+str+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);
    }

    //文件删除
    @RequestMapping("/delete")
    @ResponseBody
    public void delete(String path,HttpServletRequest request,String isAbsolutePath) throws IOException {
        //处理中文乱码
        path = URLDecoder.decode(path,"utf8");
        File file = new File(fileRoot+"/" + path);
        if(isAbsolutePath.equals("1"))
            file = new File(path);
        System.out.println(file.getPath());
        FileOperation.deleteDir(file);


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("删除了路径：\""+path+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);
    }
    //文件重命名
    @RequestMapping("/rename")
    @ResponseBody
    public void rename(String tempDir,String oldName,String newName,HttpServletRequest request,String isAbsolutePath) throws IOException {
        //解决中文乱码
        tempDir = URLDecoder.decode(tempDir,"utf8");
        oldName = URLDecoder.decode(oldName,"utf8");
        newName = URLDecoder.decode(newName,"utf8");

        //获取根路径
        String root = fileRoot+"/"+tempDir;
        if(isAbsolutePath.equals("1"))
            root = tempDir;

        System.out.println(root);

        File file = new File(root+"/"+oldName);
        file.renameTo(new File(root+"/"+newName));



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("把文件或文件夹：\""+oldName+"\"重命名为：\""+newName+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);
    }

    //复制或移动目录或文件
    @RequestMapping("copyOrMoveDirOrFile")
    @ResponseBody
    public String copyOrMoveDirOrFile(String srcDir,String destDir,String isRemove,HttpServletRequest request,String isAbsolutePath) throws IOException {
        //解决中文乱码
        srcDir = URLDecoder.decode(srcDir,"utf8");
        destDir = URLDecoder.decode(destDir,"utf8");

        if(!isAbsolutePath.equals("1")){
            srcDir = fileRoot+"/"+srcDir;
            destDir = fileRoot+"/"+destDir;
        }

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setType(LOG_TYPE_FILE);



        if(isRemove.equals("true")) {
            boolean resRemove = FileOperation.removeOrCopyDirOrFile(new File(srcDir), new File(destDir), true);
            if(resRemove==false){
                oprLog.setInfo("目标路径:\""+destDir+"\"已经存在:\""+srcDir+"\",无法移动！");
                oprLogService.addOprLog(oprLog);
                return "error";
            }
            oprLog.setInfo("成功将路径:\""+srcDir+"\"移到了:\""+destDir);
            oprLogService.addOprLog(oprLog);
            return "success";
        }
        else {
            boolean resCopy = FileOperation.removeOrCopyDirOrFile(new File(srcDir), new File(destDir), false);
            if(resCopy==false){
                oprLog.setInfo("目标路径:\""+destDir+"\"已经存在:\""+srcDir+"\",无法复制！");
                oprLogService.addOprLog(oprLog);
                return "error";
            }
            oprLog.setInfo("成功将路径:\""+srcDir+"\"复制了:\""+destDir);
            oprLogService.addOprLog(oprLog);
            return "success";
        }

    }

//    //读取.txt文件
//    @RequestMapping(value = "/readTxt",produces = "text/html;charset=utf8")
//    public String readTxt(String tempDir, String fileName, Model model) throws IOException {
//
//        //中文乱码处理
//        tempDir = URLDecoder.decode(tempDir,"utf8");
//        fileName = URLDecoder.decode(fileName,"utf8");
//
//        //获取文件路径
//        String path = fileRoot+"/"+tempDir+"/"+fileName;
//        String content = FileOperation.readTxt(path);//读取的文本
//
//
//
//        model.addAttribute("content",content);//存储读取的文本
//        model.addAttribute("tempDir",tempDir);//存储当前目录
//        model.addAttribute("fileName",fileName);//存储当前目录
//        return "site/readTxt";
//    }

    //获取读取文本页面
    @RequestMapping("/getTxtOrPdfPage")
    public String getTxtOrPdfPage(String tempDir, String fileName,String isAbsolutePath
            ,boolean isPDF, Model model,HttpServletRequest request) throws IOException {
        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");
        model.addAttribute("tempDir",tempDir);//存储当前目录
        model.addAttribute("fileName",fileName);//存储当前目录
        model.addAttribute("isAbsolutePath",isAbsolutePath);


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("读取文件：\""+fileName+"\"的内容");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);

        if(isPDF){
            return "site/readPdfPage";
        }else{
            return "site/readTxtPage";
        }
    }


    //读取文本内容
    @RequestMapping("/readTxtContent")
    @ResponseBody
    public String readTxtContent(String tempDir, String fileName,String isAbsolutePath) throws IOException {

        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");
        //获取文件路径
        String path = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+fileName;
        String content = FileOperation.readTxt(path);//读取的文本

        return content;
    }

    //读取PDF
    @RequestMapping("/readPDF")
    @ResponseBody
    public void readPDF(String tempDir, String fileName,String isAbsolutePath,HttpServletResponse response) throws UnsupportedEncodingException {

        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");
        //获取文件路径
        String path = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+fileName;
        try (
                ServletOutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(path);
        ){
            byte []buffer = new byte[12];
            int b = 0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //显示文件详细信息
    @RequestMapping("/showFileInfo")
    @ResponseBody
    public Map<String, String> showFileInfo(String tempDir,String fileName,String isAbsolutePath,HttpServletRequest request) throws IOException {
        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");
        //获取文件路径
        String path = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+fileName;
        Map<String, String> fileInfo = FileOperation.getFileInfo(path);

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("查看文件或文件夹：\""+fileName+"\"的详细信息");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);

        return fileInfo;
    }


    //保存文件
    @RequestMapping("/saveTxt")
    public String saveTxt(String content,String tempDir,String fileName
            ,HttpServletRequest request,String isAbsolutePath,Model model) throws IOException {
        //处理中文乱码

        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");
        content = URLDecoder.decode(content,"utf8");


        model.addAttribute("tempDir",tempDir);//存储当前目录
        model.addAttribute("fileName",fileName);//存储当前目录
        model.addAttribute("isAbsolutePath",isAbsolutePath);

        //获取文件路径
        String path = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+fileName;
        //写入文件
        FileOperation.writeTxt(path,content,false);



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("保存了文件：\""+fileName+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);

        return "site/readTxtPage";
    }


    //获取媒体页面
    @RequestMapping("/getMediaPage")
    public String getVideoPage(String tempDir,String fileName,String sortName,String isAsc
            ,String type,String pattern,HttpServletRequest request,Model model,String isAbsolutePath) throws IOException {

        if(pattern==null){
            pattern = "自动切换";
        }

        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");
        type  = URLDecoder.decode(type,"utf8");
        pattern = URLDecoder.decode(pattern,"utf8");
        //视频请求地址
        String url = "/qq_system/file";
        if(type.equals("mp4")||type.equals("mp3"))
            url += "/getVideo";
        else
            url += "/down";
        url += "?tempDir="+tempDir+"&fileName="+fileName+"&isAbsolutePath="+isAbsolutePath;

        model.addAttribute("url",url);
        model.addAttribute("type",type);
        model.addAttribute("tempDir",tempDir);
        model.addAttribute("isAbsolutePath",isAbsolutePath);
        model.addAttribute("fileName",fileName);
        model.addAttribute("pattern",pattern);

        //获取当前目录下面的媒体文件
        String path = fileRoot+"/"+tempDir;
        if(isAbsolutePath.equals("1"))
            path = tempDir;
        List<String> mediaList = FileOperation.getMediaList(path,sortName,isAsc);
        //统计文件个数
        model.addAttribute("fileListSize",mediaList.size());
        model.addAttribute("tempI",mediaList.indexOf(fileName)+1);

        if(mediaList.size()>1){

            System.out.println(mediaList);
            System.out.println(fileName);

            int tempI = mediaList.indexOf(fileName);//当前文件的位置
            int nextI = (tempI+1)%mediaList.size();//下一个文件的位置
            int preI = tempI==0?(mediaList.size()-1):(tempI-1);//上一个文件的位置
            String nextFileName = mediaList.get(nextI);//下一个文件名
            String preFileName = mediaList.get(preI);//上一个文件名

            //下一个文件类型
            String nextFileType = nextFileName.substring(nextFileName.lastIndexOf("."));
            //上一个文件类型
            String preFileType = preFileName.substring(preFileName.lastIndexOf("."));


            //下一个按钮的访问地址
            String nextUrl = "/qq_system/file/getMediaPage?type=";
            //上一个按钮的访问地址
            String preUrl = "/qq_system/file/getMediaPage?type=";
            if(nextFileType.equals(".mp4")) {
                nextUrl += "mp4";
            }
            else if(nextFileType.equals(".mp3")) {
                nextUrl += "mp3";
            }
            else {
                nextUrl += "img";
            }
            nextUrl += "&tempDir="+tempDir+"&fileName="+nextFileName+"&pattern="+pattern;
            if(preFileType.equals(".mp4"))
                preUrl += "mp4";
            else if(preFileType.equals(".mp3"))
                preUrl += "mp3";
            else
                preUrl += "img";
            preUrl += "&tempDir="+tempDir+"&fileName="+preFileName+"&pattern="+pattern;


            nextUrl += "&sortName="+sortName+"&isAsc="+isAsc+"&isAbsolutePath="+isAbsolutePath;
            preUrl += "&sortName="+sortName+"&isAsc="+isAsc+"&isAbsolutePath="+isAbsolutePath;
            model.addAttribute("nextFile",nextUrl);
            model.addAttribute("preFile",preUrl);
        }



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了媒体播放页面，播放了媒体：\""+fileName+"\"");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);

        return "site/video_play";
    }


    //播放视频
    @RequestMapping("/getVideo")
    public void getVideo(HttpServletRequest request, HttpServletResponse response,String tempDir, String fileName,String isAbsolutePath) throws IOException {


        //中文乱码处理
        tempDir = URLDecoder.decode(tempDir,"utf8");
        fileName = URLDecoder.decode(fileName,"utf8");

        //获取文件路径
        String path = fileRoot+"/"+tempDir+"/"+fileName;
        if(isAbsolutePath.equals("1"))
            path = tempDir+"/"+fileName;


        //视频资源存储信息
//        response.reset();
        //获取从那个字节开始读取文件
        String rangeString = request.getHeader("Range");
        OutputStream outputStream = null;
        RandomAccessFile targetFile = null;
        try {
            //获取响应的输出流
            outputStream = response.getOutputStream();
            File file = new File(path);
            if(file.exists()){
                targetFile = new RandomAccessFile(file, "r");
                System.out.println("opened:"+fileName);
                long fileLength = targetFile.length();
                //播放
                if(rangeString != null){

                    long range = Long.parseLong(rangeString.substring(rangeString.indexOf("=") + 1, rangeString.indexOf("-")));
                    //设置内容类型
                    response.setHeader("Content-Type", "video/mov");
                    //设置此次相应返回的数据长度
                    response.setHeader("Content-Length", String.valueOf(fileLength - range));
                    //设置此次相应返回的数据范围
                    response.setHeader("Content-Range", "bytes "+range+"-"+(fileLength-1)+"/"+fileLength);
                    //返回码需要为206，而不是200
                    response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
                    //设定文件读取开始位置（以字节为单位）
                    targetFile.seek(range);
                }else {//下载

                    //设置响应头，把文件名字设置好
                    response.setHeader("Content-Disposition", "attachment; filename="+fileName );
                    //设置文件长度
                    response.setHeader("Content-Length", String.valueOf(fileLength));
                    //解决编码问题
                    response.setHeader("Content-Type","application/octet-stream");
                }


                byte[] cache = new byte[1024 * 300];
                int flag;
                while ((flag = targetFile.read(cache))!=-1){
                    outputStream.write(cache, 0, flag);
                }
            }else {
                String message = "file:"+fileName+" not exists";
                //解决编码问题
                response.setHeader("Content-Type","application/json");
                outputStream.write(message.getBytes(StandardCharsets.UTF_8));
            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(outputStream!=null){
                outputStream.flush();
                outputStream.close();
            }
            if(targetFile!=null){
                targetFile.close();
                System.out.println("closed"+fileName);
            }
        }



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("视频或音频：\""+fileName+"\"播放完毕！");
        oprLog.setType(LOG_TYPE_FILE);
        oprLogService.addOprLog(oprLog);
    }

    //获取爬取数据页面
    @RequestMapping("/getScrapyPage")
    public String getScrapyPage(String tempDir,Model model){
        model.addAttribute("tempDir",tempDir);
        return "python/scrapy_data";
    }

    //爬取数据
    @RequestMapping("/scrapyData")
    public String scrapyData(String tempDir,String userName,Model model,String isAbsolutePath){
        model.addAttribute("tempDir",tempDir);
        //异步方式爬取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isAbsolutePath.equals("1"))
                    pythonService.execPythonUpdated(tempDir,userName);
                else
                    pythonService.execPythonUpdated(fileRoot+"/"+tempDir,userName);
            }
        }).start();
        return "index";
    }

    //爬取哔哩哔哩视频
    @RequestMapping("/scrapyBiliBiliData")
    public String scrapyBiliBiliData(String tempDir,String userName,String url,Model model,String isAbsolutePath){
        model.addAttribute("tempDir",tempDir);
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isAbsolutePath.equals("1"))
                    pythonService.scrapyBilibiliMedia(tempDir,userName,url);
                else
                    pythonService.scrapyBilibiliMedia(fileRoot+"/"+tempDir,userName,url);
            }
        }).start();
        return "index";
    }

    //爬取抖音当前页视频
    @RequestMapping("/scrapyDouyinByUrl")
    public String scrapyDouyinByUrl(String tempDir,String url,Model model,String isAbsolutePath){
        model.addAttribute("tempDir",tempDir);
        //异步方式爬取数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isAbsolutePath.equals("1"))
                    pythonService.scrapyDouyinByUrl(tempDir,url);
                else
                    pythonService.scrapyDouyinByUrl(fileRoot+"/"+tempDir,url);
            }
        }).start();
        return "index";
    }

    //电脑休眠
    @RequestMapping("/shutdown")
    @ResponseBody
    public void shutDown() throws IOException {
        Runtime runtime = Runtime.getRuntime();
        runtime.exec("cmd /c shutdown -h");
    }
}
