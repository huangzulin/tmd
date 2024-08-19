package fun.zulin.tmd.telegram;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.RandomUtil;
import fun.zulin.tmd.data.item.DownloadItem;
import fun.zulin.tmd.data.item.DownloadItemServiceImpl;
import fun.zulin.tmd.data.item.DownloadState;
import fun.zulin.tmd.utils.SpringContext;
import it.tdlight.jni.TdApi;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class DownloadManage {

    private static List<DownloadItem> downloadingItems = new ArrayList<>();

    public static List<DownloadItem> getItems() {
        return CollectionUtil.emptyIfNull(downloadingItems);
    }

    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();

    /**
     * 下载视频
     *
     * @param item 视频数据
     */
    public static void download(DownloadItem item) {
        executorService.submit(() -> {
            CountDownLatch countDownLatch = new CountDownLatch(1);

            Tmd.client.send(new TdApi.DownloadFile(item.getFileId(), 16, 0, 0, true), result -> {
                var service = SpringContext.getBean(DownloadItemServiceImpl.class);
                var saveItem = service.getByUniqueId(item.getUniqueId());

                //下载完成后更改数据状态
                saveItem.setState(DownloadState.Complete.name());
                saveItem.setDownloadedSize(result.get().size);
                service.updateById(saveItem);
                //从下载队列中移除
                removeDownloadingItems(item.getUniqueId());
                countDownLatch.countDown();
            });

            try {
                countDownLatch.await(20, TimeUnit.MINUTES);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

    }


    public static void startDownloading() {
        var service = SpringContext.getBean(DownloadItemServiceImpl.class);
        var items = service.getDownloadingItemsFromDB();
        downloadingItems = CollectionUtil.emptyIfNull(items);


        downloadingItems.forEach(item -> {
            Tmd.client.send(new TdApi.GetMessage(Tmd.savedMessagesChat.id, item.getMassageId()), message -> {
                if (message.get().content instanceof TdApi.MessageVideo video) {
                    var fileId = video.video.video.id;
                    item.setFileId(fileId);
                    DownloadManage.download(item);
                }
            });
        });

    }

    public static void addDownloadingItems(DownloadItem item) {
        downloadingItems.add(item);
    }

    public static void removeDownloadingItems(String uniqueId) {
        downloadingItems = downloadingItems.stream().filter(d -> !d.getUniqueId().equals(uniqueId)).collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * 更新下载进度
     *
     * @param uniqueId       唯一id
     * @param downloadedSize 已下载
     */
    public static void updateProgress(String uniqueId, Long downloadedSize) {

        for (DownloadItem item : downloadingItems) {
            if (uniqueId.equals(item.getUniqueId())) {

                if (item.getDownloadCount() < 5) {
                    item.setDownloadCount(item.getDownloadCount() + 1);
                } else {
                    item.setDownloadCount(0);
                    var downloadDiff = downloadedSize - item.getDownloadedSize();
                    var timeDiff = Duration.between(item.getDownloadUpdateTime(), LocalDateTime.now(ZoneId.of("Asia/Shanghai"))).toMillis();

                    item.setDownloadUpdateTime(LocalDateTime.now(ZoneId.of("Asia/Shanghai")));
                    item.setDownloadedSize(downloadedSize);

                    var speed = (((float) downloadDiff / timeDiff) * 1000);

                    item.setDownloadBytePerSec((long) speed);
                }
            }
        }


    }

}
