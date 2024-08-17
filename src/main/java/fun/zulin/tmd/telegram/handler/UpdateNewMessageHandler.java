package fun.zulin.tmd.telegram.handler;

import fun.zulin.tmd.data.item.DownloadItem;
import fun.zulin.tmd.data.item.DownloadItemServiceImpl;
import fun.zulin.tmd.data.item.DownloadState;
import fun.zulin.tmd.telegram.DownloadManage;
import fun.zulin.tmd.telegram.Tmd;
import fun.zulin.tmd.utils.SpringContext;
import it.tdlight.jni.TdApi;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class UpdateNewMessageHandler {

    public static void accept(TdApi.UpdateNewMessage update) {

        var messageContent = update.message.content;
        var messageId = update.message.id;
        TdApi.User me = Tmd.me;
        TdApi.Chat savedMessagesChat = Tmd.savedMessagesChat;

        if (update.message.chatId != savedMessagesChat.id) {
            return;
        }

        String text = "";
        if (messageContent instanceof TdApi.MessageText messageText) {
            // Get the text of the text message
            text = messageText.text.text;
        } else {
            if (messageContent instanceof TdApi.MessageVideo video) {

                var service = SpringContext.getBean(DownloadItemServiceImpl.class);
                var uniqueId = video.video.video.remote.uniqueId;

                var item = service.getByUniqueId(uniqueId);
                if (item != null) {
                    return;
                }
                item = DownloadItem.builder()
                        .caption(video.caption.text)
                        .createTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")))
                        .downloadedSize(0)
                        .fileId(video.video.video.id)
                        .filename(video.video.fileName)
                        .fileSize(video.video.video.size)
                        .massageId(messageId)
                        .uniqueId(uniqueId)
                        .state(DownloadState.Created.name())
                        .build();
                service.save(item);
                //
                DownloadManage.addDownloadingItems(item);
                // 下载视频
                DownloadManage.download(item);

            }
        }

    }

}
