package com.work.qq_system_springboot.controller;

import com.work.qq_system_springboot.entity.OprLog;
import com.work.qq_system_springboot.service.OprLogService;
import com.work.qq_system_springboot.tools.HostHolder;
import com.work.qq_system_springboot.tools.QQSystemConstant;
import com.work.qq_system_springboot.tools.QQSystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/game")
public class GameController implements QQSystemConstant{

    @Value("${community.game.path}")
    private String gameRoot;

    @Value("${community.file_system.path}")
    private String fileRoot;

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private OprLogService oprLogService;

    @RequestMapping("/gameList")
    public String gameList(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了游戏列表页面");
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);


        return "game/game_list";
    }


//    @RequestMapping("/warGame")
//    public String warGame(HttpServletRequest request) throws IOException {
//
//
//        //记录日志
//        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
//        oprLog.setUserId(hostHolder.get().getId());
//        oprLog.setUserName(hostHolder.get().getUsername());
//        oprLog.setInfo("进入了棋盘游戏页面");
//        oprLog.setType(LOG_TYPE_GAME);
//        oprLogService.addOprLog(oprLog);
//
//        return "game/MRGame";
//    }

    @RequestMapping("/chooseQizi")
    @ResponseBody
    public void chooseQizi(HttpServletRequest request){
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("选择了一个黑色棋子");
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);
    }

    @RequestMapping("/snack")
    public String snack(HttpServletRequest request) throws IOException {



        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了贪吃蛇游戏页面");
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);

        return "game/snack-copy";
    }

    @RequestMapping("/changeDir")
    @ResponseBody
    public void changeDir(String dir,HttpServletRequest request){
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("贪吃蛇改变方向："+dir);
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);
    }

    @RequestMapping("/snackGrow")
    @ResponseBody
    public void snackGrow(String size,HttpServletRequest request){
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("贪吃蛇得分："+size);
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);
    }

    @RequestMapping("/tanker")
    public String tanker(HttpServletRequest request) throws IOException {


        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了坦克大战游戏页面");
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);

        return "game/tanker";
    }

    @RequestMapping("/tanker2")
    public String tanker2(HttpServletRequest request){

        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了坦克大战2游戏页面");
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);

        return "game/坦克大战";
    }

    @RequestMapping("/tanker-multi")
    public String tankerMulti(HttpServletRequest request){
        //记录日志
        OprLog oprLog = QQSystemUtil.getRequestInfo(request);
        oprLog.setUserId(hostHolder.get().getId());
        oprLog.setUserName(hostHolder.get().getUsername());
        oprLog.setInfo("进入了【坦克大战-多人混战】游戏页面");
        oprLog.setType(LOG_TYPE_GAME);
        oprLogService.addOprLog(oprLog);
        return "redirect:http://120.48.134.2:6060";
    }

    @RequestMapping("/getImage")
    public void getImage(String fileName, HttpServletResponse response){
        try (
                ServletOutputStream os = response.getOutputStream();
                FileInputStream fis = new FileInputStream(gameRoot+"/"+fileName);
        ){
            response.setContentType("image/png");

            byte []buffer = new byte[256];
            int b = 0;
            while((b=fis.read(buffer))!=-1){
                os.write(buffer,0,b);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
