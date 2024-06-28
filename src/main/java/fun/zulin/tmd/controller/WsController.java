package fun.zulin.tmd.controller;

import fun.zulin.tmd.data.item.DownloadItem;
import fun.zulin.tmd.data.item.DownloadItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class WsController {

    @Autowired
    private DownloadItemService service;

    @MessageMapping("/downloading")
    @SendTo("/topic/downloading")
    public List<DownloadItem> downloading() throws Exception {

        return service.getDownloading();
    }


    @MessageMapping("/downloaded")
    @SendTo("/topic/downloaded")
    public List<DownloadItem> downloaded() throws Exception {
        return service.getDownloadedItem();
    }

}
