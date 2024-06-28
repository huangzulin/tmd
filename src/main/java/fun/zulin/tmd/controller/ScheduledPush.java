package fun.zulin.tmd.controller;

import fun.zulin.tmd.data.item.DownloadItemService;
import fun.zulin.tmd.telegram.Tmd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@DependsOn("tmd")
public class ScheduledPush {

    @Autowired
    DownloadItemService service;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Scheduled(fixedRate = 2000)
    public void downloaded() {
        try {
            if (Tmd.client != null) {
                var items = service.getDownloadedItem();
                simpMessagingTemplate.convertAndSend("/topic/downloaded", items);
            }
        } catch (Exception e) {
        }

    }


    @Scheduled(fixedRate = 500)
    public void downloading() {
        try {
            if (Tmd.client != null) {
                var items = service.getDownloading();
                simpMessagingTemplate.convertAndSend("/topic/downloading", items);
            }
        } catch (Exception e) {

        }
    }
}

